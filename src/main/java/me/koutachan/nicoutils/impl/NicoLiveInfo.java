package me.koutachan.nicoutils.impl;

import me.koutachan.nicoutils.NicoUtils;
import me.koutachan.nicoutils.impl.builder.NicoLiveBuilder;
import me.koutachan.nicoutils.impl.options.enums.live.Latency;
import me.koutachan.nicoutils.impl.options.enums.live.LiveQuality;
import me.koutachan.nicoutils.impl.options.enums.live.PlatForm;
import me.koutachan.nicoutils.impl.util.FileUtils;
import me.koutachan.nicoutils.impl.websocket.LiveChatSocket;
import me.koutachan.nicoutils.impl.websocket.LiveSocket;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

public class NicoLiveInfo {

    public static void main(String[] args) {
        NicoUtils.getLiveBuilder().setURL("https://live.nicovideo.jp/watch/lv337806735?ref=live2gate")
                .setLatency(Latency.HIGH)
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
            Document document = Jsoup.connect(builder.getURL())
                    .get();

            String element = document.getElementById("embedded-data").attr("data-props");

            JSONObject relive = new JSONObject(element).getJSONObject("site").getJSONObject("relive");

            String webSocketURL = relive.getString("webSocketUrl");

            final boolean ended = webSocketURL.isEmpty();

            if (ended) {
                throw new IllegalStateException("already live ended. s=" + relive);
            }

            liveSocket.start(URI.create(webSocketURL));

            //デバッグ用
            while (true) {
                //System.out.println("begin: " + liveSocket.getBegin() + " start:" + liveSocket.getEnd());
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final String PLATFORM = PlatForm.WINDOWS.getPlatform();

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
            Connection.Response video = Jsoup.connect(videoContentURL)
                    .header("User-Agent", builder.getRequestSettings().getAgent())
                    .ignoreContentType(true)
                    .method(Connection.Method.GET)
                    .execute();

            new Thread(() -> FileUtils.downloadFileFromURL(videoContentURL, Paths.get("", "test",sequence + ".ts").toFile())).start();

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

            System.out.println(m3u8.body());
        } catch (Exception e) {
            //エラーが発生した場合
            //またアクセスしてシークエンスを取得する予定
            e.printStackTrace();
        }
    }

    public void start() {
        stop();

        thread = new Thread(() -> {
            task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        callSequence();
                    } catch (Exception ex) {
                        //();
                    }
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