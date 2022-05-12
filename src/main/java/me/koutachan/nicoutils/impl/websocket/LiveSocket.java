package me.koutachan.nicoutils.impl.websocket;

import jakarta.websocket.*;
import me.koutachan.nicoutils.impl.NicoLiveInfo;
import me.koutachan.nicoutils.impl.options.enums.live.Disconnect;
import me.koutachan.nicoutils.impl.data.Statistics;
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

    private Thread thread;

    private boolean first = true;

    private Disconnect disconnect;

    private Statistics statistics;

    public LiveSocket(NicoLiveInfo liveInfo) {
        super();

        this.nicoLiveInfo = liveInfo;
    }

    public void onOpen(Session session, EndpointConfig config) {
        System.out.println("oepnded!");

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

    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    public void onMessage(String message) {
        JSONObject jsonObject = new JSONObject(message);

        String type = jsonObject.getString("type");

        if (type.equalsIgnoreCase("ping")) {
            session.getAsyncRemote().sendText(new JSONObject().put("type", "pong").toString());
        }

        if (type.equalsIgnoreCase("seat")) {
            this.keepIntervalSeconds = jsonObject.getJSONObject("data").getInt("keepIntervalSec");

            startKeepInterval();
            sendAkashic(false);
        }

        if (type.equalsIgnoreCase("statistics")) {
            statistics = new Statistics(jsonObject.getJSONObject("data"));

            if (first) {
                sendAkashic(false);
            }
        }

        if (type.equalsIgnoreCase("disconnect")) {
            String reason = jsonObject.getJSONObject("data").getString("reason");

            if (reason.equalsIgnoreCase("takeover")) {
                disconnect = Disconnect.TAKEOVER;
            } else {
                disconnect = Disconnect.UNKNOWN;
            }

            stop();
        }

        System.out.println(jsonObject);
    }

    public void startKeepInterval() {
        stopKeepInterval();

        if (session.isOpen()) {
            thread = new Thread(() -> {
                try {
                    while (session.isOpen()) {
                        Thread.sleep(keepIntervalSeconds * 1000L);

                        session.getAsyncRemote().sendText(new JSONObject().put("type", "keepSeat").toString());
                    }
                } catch (InterruptedException e) {
                    thread.interrupt();
                }
            });

            thread.start();
        }
    }

    public void sendAkashic(final boolean chasePlay) {
        JSONObject object = new JSONObject()
                .put("type", "getAkashic")
                .put("data", new JSONObject().put("chasePlay", chasePlay));

        session.getAsyncRemote().sendText(object.toString());

        first = false;
    }

    public void stopKeepInterval() {
        thread.interrupt();
    }

    public void stop() {
        try {
            session.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClose(Session session) {

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
}