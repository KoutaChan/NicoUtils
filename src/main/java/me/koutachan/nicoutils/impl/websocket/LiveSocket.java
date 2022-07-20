package me.koutachan.nicoutils.impl.websocket;

import jakarta.websocket.*;
import me.koutachan.nicoutils.impl.NicoLiveInfo;
import me.koutachan.nicoutils.impl.data.Statistics;
import me.koutachan.nicoutils.impl.event.Listener;
import me.koutachan.nicoutils.impl.event.tests.LiveEventListener;
import me.koutachan.nicoutils.impl.options.enums.live.Disconnect;
import me.koutachan.nicoutils.impl.options.enums.live.Latency;
import me.koutachan.nicoutils.impl.options.enums.live.LiveQuality;
import me.koutachan.nicoutils.impl.util.NicoTimeUtils;
import me.koutachan.nicoutils.impl.util.QualityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class LiveSocket extends Endpoint {

    private Session session;

    private final NicoLiveInfo info;

    //普通30秒
    private int keepIntervalSeconds = 30;

    private LiveChatSocket chatSocket;

    private Thread thread;

    private boolean first = true;

    private Disconnect disconnect = Disconnect.UNKNOWN;

    private Statistics statistics;
    private Date end, begin;

    private List<LiveQuality> availableQualities;
    private LiveQuality liveQuality;

    private String uri, sync_uri, threadId;

    public LiveSocket(NicoLiveInfo liveInfo) {
        super();

        this.info = liveInfo;
    }

    public void onOpen(Session session, EndpointConfig config) {
        JSONObject quality = new JSONObject()
                .put("quality", info.getQuality().getType())
                //他のタイプがあるか検証してもいいかもしれない
                //high=hls only
                //low= hls+mp4
                .put("protocol", info.getLatency() == Latency.LOW ? "hls+mp4" : "hls")
                .put("latency", info.getLatency().getType())
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

        //for tests
        Listener.addListener(new LiveEventListener());
    }

    public void onMessage(String message) {
        JSONObject jsonObject = new JSONObject(message);

        String type = jsonObject.getString("type");

        JSONObject data = jsonObject.optJSONObject("data");

        if (type.equalsIgnoreCase("ping")) {
            session.getAsyncRemote().sendText(new JSONObject().put("type", "pong").toString());
        } else if (type.equalsIgnoreCase("room")) {
            if (info.getBuilder().isOpenChatSocket()) {
                URI commentServer = URI.create(data.getJSONObject("messageServer").getString("uri"));

                threadId = data.getString("threadId");

                chatSocket = new LiveChatSocket(info, threadId);
                chatSocket.start(commentServer);
            }
        } else if (type.equalsIgnoreCase("seat")) {
            keepIntervalSeconds = data.getInt("keepIntervalSec");

            startKeepInterval();
        } else if (type.equalsIgnoreCase("statistics")) {
            statistics = new Statistics(data);

            if (first) {
                sendAkashic(false);
            }
        } else if (type.equalsIgnoreCase("stream")) {
            uri = data.getString("uri");
            sync_uri = data.getString("syncUri");

            availableQualities = QualityUtils.getAllowedQuality(data);
            liveQuality = QualityUtils.getQualityEnum(data.getString("quality"));

            info.call();
        } else if (type.equalsIgnoreCase("schedule")) {

            //beta...
            begin = NicoTimeUtils.toDate(data.getString("begin"));
            end = NicoTimeUtils.toDate(data.getString("end"));

        } else if (type.equalsIgnoreCase("disconnect")) {
            String reason = data.getString("reason");

            try {
                disconnect = Disconnect.valueOf(reason.toUpperCase());
            } catch (Exception ex) {
                disconnect = Disconnect.UNKNOWN;
            }

            stop();
        } else if (type.equalsIgnoreCase("error")) {
            //{"data":{"code":"NO_STREAM_AVAILABLE"},"type":"error"}
            String code = data.getString("code");

            throw new IllegalStateException("error occurred on LiveSocket (code: " + code + ")");
        }
        //todo: akashic
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

    /**
     * ライブを維持するために実行されるもの
     * <br> 止めたい場合は{@link LiveSocket#stopKeepInterval()}を実行してください
     */
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
                    //todo: stop();
                }
            });

            thread.start();
        }
    }

    public void stopKeepInterval() {
        if (thread != null && !thread.isInterrupted() && thread.isAlive()) {
            thread.interrupt();

            thread = null;
        }
    }

    public void sendChange(LiveQuality liveQuality, Latency latency, final boolean chasePlay) {
        if (session.isOpen()) {
            JSONObject qualityJson = new JSONObject()
                    .put("quality", liveQuality.getType())
                    .put("protocol", "hls+fmp4")
                    .put("latency", latency.getType())
                    .put("chasePlay", chasePlay);

            JSONObject sendJson = new JSONObject()
                    .put("type", "changeStream")
                    .put("data", qualityJson);

            session.getAsyncRemote().sendText(sendJson.toString());
        }
    }


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

            if (session != null && session.isOpen()) {
                session.close();
                session = null;
            }

            info.stop();

            if (chatSocket != null) {
                chatSocket.stop();
                chatSocket = null;
            }
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
                   headers.put("User-Agent", Collections.singletonList(info.getBuilder().getRequestSettings().getAgent()));
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

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
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

    public List<LiveQuality> getAvailableQualities() {
        return availableQualities;
    }

    public void setAvailableQualities(List<LiveQuality> availableQualities) {
        this.availableQualities = availableQualities;
    }

    public LiveQuality getQuality() {
        return liveQuality;
    }

    public void setQuality(LiveQuality liveQuality) {
        this.liveQuality = liveQuality;
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String uri) {
        this.uri = uri;
    }

    public String getSyncURI() {
        return sync_uri;
    }

    public void setSyncURI(String sync_uri) {
        this.sync_uri = sync_uri;
    }

    public int getKeepIntervalSeconds() {
        return keepIntervalSeconds;
    }

    public void setKeepIntervalSeconds(int keepIntervalSeconds) {
        if (keepIntervalSeconds >= 0) this.keepIntervalSeconds = keepIntervalSeconds;
        else throw new IllegalStateException("keepIntervalSeconds is negative (" + keepIntervalSeconds + ")");
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getBegin() {
        return begin;
    }

    public void setBegin(Date begin) {
        this.begin = begin;
    }
}