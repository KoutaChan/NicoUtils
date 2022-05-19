package me.koutachan.nicoutils.impl;

import me.koutachan.nicoutils.impl.builder.NicoLiveBuilder;
import me.koutachan.nicoutils.impl.websocket.LiveChatSocket;
import me.koutachan.nicoutils.impl.websocket.LiveSocket;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URI;

public class NicoLiveInfo {

    public static void main(String[] args) {
        new NicoLiveInfo().init();
    }

    private NicoLiveBuilder builder = new NicoLiveBuilder();

    private LiveSocket liveSocket = new LiveSocket(this);

    private void init() {
        try {
            Document document = Jsoup.connect(null)
                    .get();

            String element = document.getElementById("embedded-data").attr("data-props");

            JSONObject json = new JSONObject(element);

            JSONObject relive = json.getJSONObject("site").getJSONObject("relive");

            String webSocketURL = relive.getString("webSocketUrl");

            final boolean ended = webSocketURL.isEmpty();

            System.out.println(relive);

            if (!ended) {
                System.out.println(webSocketURL);

                liveSocket.start(URI.create(webSocketURL + "&frontend_id=9"));
            }

            //デバッグ用
            while (true) {

            }

            //wss://a.live2.nicovideo.jp/unama/wsapi/v2/watch/105701637620330?audience_token=105701637620330_anonymous-user-d74a2006-bb6c-4028-8aa4-af0238a1eaf6_1652345076_1a88ca82b8a3aff9311d873454fe32249115f177&frontend_id=9
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public NicoLiveBuilder getBuilder() {
        return builder;
    }

    public void setBuilder(NicoLiveBuilder builder) {
        this.builder = builder;
    }

    public LiveSocket getLiveSocket() {
        return liveSocket;
    }

    public void setLiveSocket(LiveSocket liveSocket) {
        this.liveSocket = liveSocket;
    }

    public LiveChatSocket getChatSocket() {
        return liveSocket.getChatSocket();
    }

    public void setChatSocket(LiveChatSocket liveChatSocket) {
        liveSocket.setChatSocket(liveChatSocket);
    }
}