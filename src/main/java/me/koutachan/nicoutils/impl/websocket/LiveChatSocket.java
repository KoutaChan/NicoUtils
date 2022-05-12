package me.koutachan.nicoutils.impl.websocket;

import jakarta.websocket.*;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LiveChatSocket extends Endpoint {

    private Session session;

    private String threadId;

    public LiveChatSocket(String threadId) {
        this.threadId = threadId;
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        //

        System.out.println("aa");
    }

    public void onMessage(String message) {
        System.out.println(message);
    }

    @Override
    public void onError(Session session, Throwable thr) {
        System.out.println("aa");
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        System.out.println("aa");
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
}
