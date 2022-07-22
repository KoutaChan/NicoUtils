package me.koutachan.nicoutils.impl;

import me.koutachan.nicoutils.NicoUtils;
import me.koutachan.nicoutils.impl.builder.NicoLiveBuilder;
import me.koutachan.nicoutils.impl.options.enums.live.Latency;
import me.koutachan.nicoutils.impl.options.enums.live.LiveQuality;
import me.koutachan.nicoutils.impl.websocket.LiveChatSocket;
import me.koutachan.nicoutils.impl.websocket.LiveSocket;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

public class NicoLiveInfo {

    public static void main(String[] args) {
        NicoUtils.getLiveBuilder().setURL("https://live.nicovideo.jp/watch/lv337809108?ref=live2gate")
                .setLatency(Latency.LOW)
                .create();
    }

    private NicoLiveBuilder builder;

    private Latency latency;
    private LiveQuality quality;

    private LiveSocket liveSocket = new LiveSocket(this);

    private long sequence = 0;

    private Thread thread;
    private TimerTask task;

    public NicoLiveInfo(NicoLiveBuilder builder) {
        this.builder = builder;

        this.quality = builder.getQuality();
        this.latency = builder.getLatency();

        init();
    }


    private void init() {
        try {
            //todo: regexでニコニコのURLか確認する？
            Document document = Jsoup.connect(builder.getURL())
                    .get();

            String element = document.getElementById("embedded-data").attr("data-props");

            JSONObject relive = new JSONObject(element).getJSONObject("site").getJSONObject("relive");

            String webSocketURL = relive.getString("webSocketUrl");

            if (webSocketURL.isEmpty()) {
                throw new IllegalStateException("already live ended. s=" + relive);
            }

            liveSocket.start(URI.create(webSocketURL));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void call() {
        try {
            Document document = Jsoup.connect(this.liveSocket.getSyncURI())
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

    public void callSequence() {
        try {

            String videoContentURL = latency == Latency.LOW ?
                    getLiveSocket().getURI().replaceFirst("master.m3u8", "1/mp4/" + sequence + ".mp4")
                    : getLiveSocket().getURI().replaceFirst("master.m3u8", "1/ts/" + sequence + ".ts");

            //mp4 || ts ready!
            //mp4は圧縮でもされているのかな？
            System.out.println(videoContentURL);

            Connection.Response video = Jsoup.connect(videoContentURL)
                    .header("User-Agent", builder.getRequestSettings().getAgent())
                    .ignoreContentType(true)
                    .method(Connection.Method.GET)
                    .execute();

            sequence++;

            String m3u8URL = latency == Latency.LOW ?
                    getLiveSocket().getURI().replaceFirst("master", "1/mp4/playlist") + "&_poll_=" + sequence
                    : getLiveSocket().getURI().replaceFirst("master.m3u8", "1/ts/playlist.m3u8");

            //m3u8 ready!
            Connection.Response m3u8 = Jsoup.connect(m3u8URL)
                    .header("User-Agent", builder.getRequestSettings().getAgent())
                    .ignoreContentType(true)
                    .method(Connection.Method.GET)
                    .execute();
        } catch (Exception e) {
            //reload!
            call();
        }
    }

    public void start() {
        stop();

        thread = new Thread(() -> {
            task = new TimerTask() {
                @Override
                public void run() {
                    callSequence();
                }
            };

            //重要な基盤なためTimerTaskで動かす
            //Thread.sleep()だと信頼不足？
            //low=5seconds
            //high=1.5seconds
            //m3u8を読み取って動かしてもいいかも
            new Timer().scheduleAtFixedRate(task,0, latency == Latency.LOW ? 5000 : 1500);

        });

        thread.start();
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }

        if (thread != null && !thread.isInterrupted() && thread.isAlive()) {
            thread.interrupt();
            thread = null;
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

    /**
     * ライブの遅延 詳しくは {@link NicoLiveBuilder#setLatency(Latency)} を視てください
     */
    public Latency getLatency() {
        return latency;
    }

    public void setLatency(Latency latency) {
        this.latency = latency;
    }

    public LiveQuality getQuality() {
        return quality;
    }

    public void setQuality(LiveQuality quality) {
        this.quality = quality;
    }
}