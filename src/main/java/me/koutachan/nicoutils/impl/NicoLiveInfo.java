package me.koutachan.nicoutils.impl;

import me.koutachan.nicoutils.NicoUtils;
import me.koutachan.nicoutils.impl.builder.NicoLiveBuilder;
import me.koutachan.nicoutils.impl.options.enums.live.Latency;
import me.koutachan.nicoutils.impl.options.enums.live.PlatForm;
import me.koutachan.nicoutils.impl.websocket.LiveChatSocket;
import me.koutachan.nicoutils.impl.websocket.LiveSocket;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;

public class NicoLiveInfo {

    public static void main(String[] args) {
        NicoUtils.getLiveBuilder().setURL("https://live.nicovideo.jp/watch/lv337466060")
                .create();
    }

    private NicoLiveBuilder builder;

    private LiveSocket liveSocket = new LiveSocket(this);

    private final String HTTP_PARAMETER = "&frontend_id=9";

    private long sequence = 0;

    private Thread thread;

    public NicoLiveInfo(NicoLiveBuilder builder) {
        this.builder = builder;

        init();
    }


    private void init() {
        try {
            Document document = Jsoup.connect(builder.getURL())
                    .get();

            String element = document.getElementById("embedded-data").attr("data-props");

            JSONObject relive = new JSONObject(element).getJSONObject("site").getJSONObject("relive");

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

    private final String PLATFORM = PlatForm.WINDOWS.getPlatform();

    public void call() {
        try {
            Document document = Jsoup.connect(this.liveSocket.getSyncURI())
                    .header("sec-ch-ua-mobile", "?0")
                    .header("sec-ch-ua-platform", PLATFORM)
                    .header("Sec-Fetch-Dest", "empty")
                    .header("Sec-Fetch-Site", "cross-site")
                    .header("User-Agent", builder.getRequestSettings().getAgent())
                    .ignoreContentType(true)
                    .get();

            JSONObject jsonObject = new JSONObject(document.text());

            JSONObject meta = jsonObject.getJSONObject("meta");

            if (meta.getInt("status") != 200 || !meta.getString("message").equals("ok"))
                throw new IllegalStateException("request failed. s=" + jsonObject);

            JSONArray segments = jsonObject.getJSONObject("data").getJSONObject("stream_sync").getJSONArray("segments_metadata");

            JSONObject obj = segments.getJSONObject(0);

            this.sequence = obj.getLong("sequence");

            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        stop();

        thread = new Thread(() -> {
            try {
                while (true) {
                    Latency latency = builder.getLatency();

                    if (latency == Latency.LOW) {

                    } else {

                    }

                    Thread.sleep(builder.getLatency() == Latency.LOW ? 2 * 1000L : 5 * 1000L);

                    //TODO: call()
                }
            } catch (InterruptedException e) {
                stop();
            }
        });

        thread.start();
    }

    public void stop() {
        if (thread != null && !thread.isInterrupted() && thread.isAlive()) thread.interrupt();
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