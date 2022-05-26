package me.koutachan.nicoutils.impl.websocket;

import jakarta.websocket.*;
import me.koutachan.nicoutils.impl.data.Comment;
import me.koutachan.nicoutils.impl.event.Listener;
import me.koutachan.nicoutils.impl.event.LiveEvent;
import me.koutachan.nicoutils.impl.event.tests.LiveEventListener;
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

    private Thread thread;

    public LiveChatSocket(String threadId) {
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

        //for tests
        Listener.addListener(new LiveEventListener());

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
        if (thread != null && !thread.isInterrupted() && thread.isAlive()) thread.interrupt();
    }

    public void stop() {
        try {
            stopChatTimer();

            session.close();
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
}
