package me.koutachan.nicoutils.impl.websocket;

import jakarta.websocket.*;
import me.koutachan.nicoutils.impl.NicoLiveInfo;
import me.koutachan.nicoutils.impl.event.Listener;
import me.koutachan.nicoutils.impl.options.enums.live.Disconnect;
import me.koutachan.nicoutils.impl.data.Statistics;
import me.koutachan.nicoutils.impl.options.enums.live.Latency;
import me.koutachan.nicoutils.impl.options.enums.live.Quality;
import me.koutachan.nicoutils.impl.util.QualityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LiveSocket extends Endpoint {

    private Session session;

    private final NicoLiveInfo nicoLiveInfo;

    //普通30秒
    private int keepIntervalSeconds = 30;

    private LiveChatSocket chatSocket;

    private Thread thread;

    private boolean first = true;

    private Disconnect disconnect = Disconnect.UNKNOWN;

    private Statistics statistics;

    private List<Quality> availableQualities;
    private Quality quality;

    private String uri, sync_uri;

    public LiveSocket(NicoLiveInfo liveInfo) {
        super();

        this.nicoLiveInfo = liveInfo;
    }

    public void onOpen(Session session, EndpointConfig config) {
        JSONObject quality = new JSONObject()
                .put("quality", nicoLiveInfo.getBuilder().getQuality().getType())
                //他のタイプがあるか検証してもいいかもしれない
                .put("protocol", "hls+fmp4")
                .put("latency", nicoLiveInfo.getBuilder().getLatency().getType())
                .put("chasePlay", false);

        JSONObject protocol = new JSONObject()
                .put("protocol", "webSocket")
                //TODO:　これが何になるのかよくわからない、後で検証
                .put("commentable", true);

        JSONObject sendJson = new JSONObject()
                .put("type", "startWatching")
                .put("data", new JSONObject()
                        .put("stream", quality)
                        .put("room", protocol)
                        .put("reconnect", false));

        session.getAsyncRemote().sendText(sendJson.toString());
    }

    public void onMessage(String message) {
        JSONObject jsonObject = new JSONObject(message);

        String type = jsonObject.getString("type");

        if (type.equalsIgnoreCase("ping")) {
            session.getAsyncRemote().sendText(new JSONObject().put("type", "pong").toString());
        }

        if (type.equalsIgnoreCase("room")) {
            if (nicoLiveInfo.getBuilder().isOpenChatSocket()) {
                JSONObject data = jsonObject.getJSONObject("data");

                URI commentServer = URI.create(data.getJSONObject("messageServer").getString("uri"));

                String threadId = data.getString("threadId");

                chatSocket = new LiveChatSocket(threadId);

                chatSocket.start(commentServer);
            }
        }

        if (type.equalsIgnoreCase("seat")) {
            this.keepIntervalSeconds = jsonObject.getJSONObject("data").getInt("keepIntervalSec");

            startKeepInterval();
        }

        if (type.equalsIgnoreCase("statistics")) {
            statistics = new Statistics(jsonObject.getJSONObject("data"));

            if (first) {
                sendAkashic(false);
            }
        }

        if (type.equalsIgnoreCase("stream")) {
            JSONObject data = jsonObject.getJSONObject("data");
            
            this.uri = data.getString("uri");
            this.sync_uri = data.getString("syncUri");

            this.availableQualities = QualityUtils.getAllowedQuality(data);
            this.quality = QualityUtils.getQualityEnum(data.getString("quality"));
        }

        if (type.equalsIgnoreCase("disconnect")) {
            String reason = jsonObject.getJSONObject("data").getString("reason");

            try {
                disconnect = Disconnect.valueOf(reason.toUpperCase());
            } catch (Exception ex) {
                disconnect = Disconnect.UNKNOWN;
            }

            stop();
        }

        Listener.getLiveListener().forEach(event -> event.onLiveJsonEvent(jsonObject.toString()));
    }

    @Override
    public void onError(Session session, Throwable thr) {
        thr.printStackTrace();
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {

    }

    public void callInterval() {
        if (session.isOpen()) {
            session.getAsyncRemote().sendText(new JSONObject().put("type", "keepSeat").toString());
        }
    }

    public void startKeepInterval() {
        stopKeepInterval();

        if (session.isOpen()) {
            thread = new Thread(() -> {
                try {
                    while (session.isOpen()) {
                        Thread.sleep(keepIntervalSeconds * 1000L);

                        callInterval();
                    }
                } catch (InterruptedException e) {
                    stop();
                }
            });

            thread.start();
        }
    }

    public void stopKeepInterval() {
        if (thread != null && !thread.isInterrupted() && thread.isAlive()) thread.interrupt();
    }

    public void sendChange(Quality quality, Latency latency, final boolean chasePlay) {
        if (session.isOpen()) {
            JSONObject qualityJson = new JSONObject()
                    .put("quality", quality.getType())
                    .put("protocol", "hls+fmp4")
                    .put("latency", latency.getType())
                    .put("chasePlay", chasePlay);

            JSONObject sendJson = new JSONObject()
                    .put("type", "changeStream")
                    .put("data", qualityJson);

            session.getAsyncRemote().sendText(sendJson.toString());
        }
    }

    /**
     *
     * @param chasePlay
     */
    public void sendAkashic(final boolean chasePlay) {
        JSONObject object = new JSONObject()
                .put("type", "getAkashic")
                .put("data", new JSONObject().put("chasePlay", chasePlay));

        session.getAsyncRemote().sendText(object.toString());

        first = false;
    }

    public void stop() {
        try {
            Listener.getLiveListener().forEach(event -> event.onEndEvent(session, chatSocket.getSession(), disconnect));
            stopKeepInterval();

            session.close();

            chatSocket.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(URI URI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();

            ClientEndpointConfig.Builder configBuilder = ClientEndpointConfig.Builder.create();

            configBuilder.configurator(new ClientEndpointConfig.Configurator() {
                /**
                 * ヘッダーが必要です ヘッダーがない場合エラーが吐かれます
                 */
                public void beforeRequest(Map<String, List<String>> headers) {
                   headers.put("User-Agent", Collections.singletonList("User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.54 Safari/537.36"));
                   headers.put("Accept-Language", Collections.singletonList("ja-JP,ja;q=0.9,en-US;q=0.8,en;q=0.7"));
                   headers.put("Host", Collections.singletonList("a.live2.nicovideo.jp"));
                   headers.put("Origin", Collections.singletonList("https://live.nicovideo.jp"));
                   headers.put("Pragma", Collections.singletonList("no-cache"));
                }
            });

            ClientEndpointConfig clientConfig = configBuilder.build();

            this.session = container
                    .connectToServer(this, clientConfig, URI);

            session.addMessageHandler(String.class, this::onMessage);
        } catch (DeploymentException | IOException e) {
            e.printStackTrace();
        }
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Disconnect getDisconnect() {
        return disconnect;
    }

    public void setDisconnect(Disconnect disconnect) {
        this.disconnect = disconnect;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public LiveChatSocket getChatSocket() {
        return chatSocket;
    }

    public void setChatSocket(LiveChatSocket chatSocket) {
        this.chatSocket = chatSocket;
    }

    public List<Quality> getAvailableQualities() {
        return availableQualities;
    }

    public void setAvailableQualities(List<Quality> availableQualities) {
        this.availableQualities = availableQualities;
    }

    public Quality getQuality() {
        return quality;
    }

    public void setQuality(Quality quality) {
        this.quality = quality;
    }
}