package me.koutachan.nicoutils.impl.websocket;

import jakarta.websocket.*;
import me.koutachan.nicoutils.impl.NicoLiveInfo;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;

@ClientEndpoint
public class LiveSocket {

    private Session session;

    private final NicoLiveInfo nicoLiveInfo;

    public LiveSocket(NicoLiveInfo liveInfo) {
        super();

        this.nicoLiveInfo = liveInfo;
    }

    @OnOpen
    public void onOpen(Session session) {
        JSONObject quality = new JSONObject()
                .put("quality", nicoLiveInfo.getBuilder().getQuality().getType())
                //他のタイプがあるか検証してもいいかもしれない
                .put("protocol", "hls+fmp4")
                .put("latency", nicoLiveInfo.getBuilder().getLatency().getType())
                .put("chasePlay", false);

        JSONObject protocol = new JSONObject()
                .put("protocol", "webSocket")
                //TODO:　取得する
                .put("commentable", true);

        JSONObject sendJson = new JSONObject()
                .put("type", "startWatching")
                .put("data", new JSONObject().put("stream", quality).put("stream", protocol))
                .put("reconnect", true);

        session.getAsyncRemote().sendText(sendJson.toString());
    }

    @OnError
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println(message);
    }

    @OnClose
    public void onClose(Session session) {

    }

    public void start(URI URI) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();

            this.session = container.connectToServer(this, URI);
        } catch (DeploymentException | IOException e) {
            e.printStackTrace();
        }
    }

}