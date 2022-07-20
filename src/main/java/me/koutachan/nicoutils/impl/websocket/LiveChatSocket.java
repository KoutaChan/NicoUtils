package me.koutachan.nicoutils.impl.websocket;

import jakarta.websocket.*;
import me.koutachan.nicoutils.impl.NicoLiveInfo;
import me.koutachan.nicoutils.impl.data.Comment;
import me.koutachan.nicoutils.impl.event.Listener;
import me.koutachan.nicoutils.impl.event.LiveEvent;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LiveChatSocket extends Endpoint {

    private Session session;

    private final String threadId;
    private URI URI;

    private Thread thread;
    private NicoLiveInfo info;

    public LiveChatSocket(NicoLiveInfo info, String threadId) {
        this.info = info;

        this.threadId = threadId;
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        JSONArray json = new JSONArray()
                .put(new JSONObject().put("ping", new JSONObject().put("content", "rs:0")).toMap())
                .put(new JSONObject().put("ping", new JSONObject().put("content", "ps:0")).toMap())
                .put(new JSONObject().put("thread", new JSONObject()
                        .put("thread", threadId)
                        .put("version", "20061206")
                        .put("user_id", "guest")
                        //コメント件数
                        .put("res_from", -150)
                        .put("with_global", 1)
                        .put("scores", 1)
                        .put("nicoru", 0)))
                .put(new JSONObject().put("ping", new JSONObject().put("content", "pf:0")).toMap())
                .put(new JSONObject().put("ping", new JSONObject().put("content", "rf:0")).toMap());

        session.getAsyncRemote().sendText(json.toString());

        startChatTimer();
    }

    public void onMessage(String message) {
        List<LiveEvent> events = Listener.getLiveListener();

        events.forEach(event -> event.onChatJsonEvent(message));

        JSONObject jsonObject = new JSONObject(message);

        if (jsonObject.has("chat")) {
            events.forEach(event -> event.onChatEvent(new Comment(jsonObject.getJSONObject("chat"))));
        }
    }

    @Override
    public void onError(Session session, Throwable thr) {
        thr.printStackTrace();
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
    }

    public void call() {
        if (session.isOpen()) {
            session.getAsyncRemote().sendText("");
        }
    }

    public void startChatTimer() {
        stopChatTimer();

        if (session != null && session.isOpen()) {
            thread = new Thread(() -> {
                try {
                    while (session.isOpen()) {
                        Thread.sleep(60000L);

                        call();
                    }
                } catch (InterruptedException e) {
                    stop();
                }
            });

            thread.start();
        }
    }

    public void stopChatTimer() {
        if (thread != null && !thread.isInterrupted() && thread.isAlive()) {
            thread.interrupt();
        }
    }

    public void stop() {
        try {
            stopChatTimer();

            if (session != null && session.isOpen()) {
                session.close();
                session = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(URI URI) {
        try {
            this.URI = URI;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();

            ClientEndpointConfig.Builder configBuilder = ClientEndpointConfig.Builder.create();

            configBuilder.configurator(new ClientEndpointConfig.Configurator() {
                /**
                 * ヘッダーが必要です ヘッダーがない場合エラーが吐かれます
                 */
                public void beforeRequest(Map<String, List<String>> headers) {
                    headers.put("User-Agent", Collections.singletonList(info.getBuilder().getRequestSettings().getAgent()));
                    headers.put("Accept-Language", Collections.singletonList("ja-JP,ja;q=0.9,en-US;q=0.8,en;q=0.7"));
                    headers.put("Host", Collections.singletonList("msgd.live2.nicovideo.jp"));
                    headers.put("Origin", Collections.singletonList("https://live.nicovideo.jp"));
                    headers.put("Pragma", Collections.singletonList("no-cache"));
                    headers.put("Sec-WebSocket-Protocol", Collections.singletonList("msg.nicovideo.jp#json"));
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

    public URI getURI() {
        return URI;
    }

    public void setURI(java.net.URI URI) {
        this.URI = URI;
    }
}
