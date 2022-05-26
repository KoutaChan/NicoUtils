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
        new NicoLiveBuilder().setURL("https://live.nicovideo.jp/watch/lv337086176?ref=pc_userpage_nicorepo")
                .create();
    }

    private NicoLiveBuilder builder;

    private LiveSocket liveSocket = new LiveSocket(this);

    private final String HTTP_PARAMETER = "&frontend_id=9";

    public NicoLiveInfo(NicoLiveBuilder builder) {
        this.builder = builder;

        init();
    }


    private void init() {
        try {
            Document document = Jsoup.connect(builder.getURL())
                    .get();

            String element = document.getElementById("embedded-data").attr("data-props");

            JSONObject json = new JSONObject(element);

            JSONObject relive = json.getJSONObject("site").getJSONObject("relive");

            String webSocketURL = relive.getString("webSocketUrl");

            final boolean ended = webSocketURL.isEmpty();

            if (ended) throw new IllegalStateException("already live ended. s=" + relive);

            liveSocket.start(URI.create(webSocketURL + HTTP_PARAMETER));

            //デバッグ用
            while (true) {

            }
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