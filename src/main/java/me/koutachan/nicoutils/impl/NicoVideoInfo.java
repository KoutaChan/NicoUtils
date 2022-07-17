package me.koutachan.nicoutils.impl;

import me.koutachan.nicoutils.NicoUtils;
import me.koutachan.nicoutils.impl.builder.NicoVideoBuilder;
import me.koutachan.nicoutils.impl.data.Comment;
import me.koutachan.nicoutils.impl.options.enums.video.CommentLabel;
import me.koutachan.nicoutils.impl.options.enums.video.Language;
import me.koutachan.nicoutils.impl.options.enums.video.VideoType;
import me.koutachan.nicoutils.impl.options.video.CommentSettings;
import me.koutachan.nicoutils.impl.util.FileUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class NicoVideoInfo {

    private String url, title, sessionId, contentURL;

    private boolean heartbeat, success;

    private JSONObject sentJson, receivedJson;

    private List<String> description = new ArrayList<>();
    private List<Comment> comments = new ArrayList<>();

    private Thread thread;

    private final CommentSettings commentSettings;

    private final VideoType videoType;

    //40 seconds
    private long delay = 40000;

    public NicoVideoInfo(NicoVideoBuilder builder) {
        this.url = builder.getURL();

        this.heartbeat = builder.isHeartBeat();
        this.videoType = builder.getVideoType();

        this.commentSettings = builder.getCommentSettings();

        init();
    }

    public static void main(String[] args) {
        NicoUtils.getVideoBuilder()
                .setURL("https://www.nicovideo.jp/watch/sm39411572")
                .getCommentSettings().setGetComment(true)
                .getCommentSettings().setLanguage(Language.ENGLISH)
                .getCommentSettings().setLabel(CommentLabel.DEFAULT_COMMENT)
                .setVideoType(VideoType.HTTP)
                .create()
                .getDescription().forEach(System.out::println);
    }

    private void init() {
        try {
            Document document = Jsoup.connect(url)
                    .get();

            title = document.title();

            String element = document.getElementById("js-initial-watch-data").attr("data-api-data");

            final JSONObject json = new JSONObject(element);

            //何故かhtmlコードで存在しているのでjsoupに解読してもらう
            String descriptionHtml = json.getJSONObject("video").getString("description");

            Elements href = Jsoup.parse(descriptionHtml).getElementsByAttribute("href");

            for (Element descriptionElement : href) {
                /*
                 * リンクを置き換える
                 * <a href="https://www.nicovideo.jp/watch/sm33475587" class="watch">sm33475587</a>
                 * <a href="https://ch.nicovideo.jp/article/ar1248104" target="_blank" rel="noopener">ar1248104</a>
                 */
                descriptionHtml = descriptionHtml.replaceAll(descriptionElement.outerHtml(), descriptionElement.attr("href"));
            }

            description = Arrays.asList(Jsoup.clean(descriptionHtml, new Safelist().addTags("br"))
                    .replaceAll("&nbsp;", "").split("<br>"));

            if (commentSettings.isGetComment()) {

                JSONObject thread = json.getJSONObject("comment").getJSONArray("threads").getJSONObject(0);

                JSONObject commentJson = new JSONObject()
                        .put("thread", thread.getInt("id"))
                        .put("version", 20090904)
                        .put("scores", commentSettings.isAddNGScoreInfo() ? 1 : 0)
                        .put("fork", commentSettings.getLabel().getFork())
                        .put("res_from", String.valueOf(commentSettings.getGetCommentFrom()))
                        .put("language", commentSettings.getLanguage().getLanguage());

                //todo: uses https://nvcomment.nicovideo.jp/v1/threads
                if (commentSettings.getUnixTime() != 0) {
                    commentJson.put("when", commentSettings.getUnixTime());
                }

                Document commentDocument = Jsoup.connect(thread.getString("server") + "/api/thread")
                        .header("Content-Type", "application/json")
                        .header("Accept", "application/json")
                        .requestBody(commentJson.toString())
                        .ignoreContentType(true)
                        .post();

                commentDocument.getElementsByTag("chat").forEach(comment -> comments.add(new Comment(comment)));
            }

            final JSONObject session = new JSONObject(element)
                    .getJSONObject("media")
                    .getJSONObject("delivery")
                    .getJSONObject("movie")
                    .getJSONObject("session");

            //todo: ????
            final String videos = session.getJSONArray("videos").getString(0);
            final String audios = session.getJSONArray("audios").getString(0);

            final int lifetime = session.getInt("heartbeatLifetime");
            final String recipeId = session.getString("recipeId");
            final int priority = session.getInt("priority");

            final JSONObject urls = session.getJSONArray("urls").getJSONObject(0);

            final String wellKnownPort = urls.getBoolean("isWellKnownPort") ? "yes" : "no";
            final String SSL = urls.getBoolean("isSsl") ? "yes" : "no";

            final String token = session.getString("token");
            final String signature = session.getString("signature");
            final String contentId = session.getString("contentId");

            //ht2
            final String authType = session.getJSONObject("authTypes").getString("http");
            final int contentKeyTimeout = session.getInt("contentKeyTimeout");
            final String serviceUserId = session.getString("serviceUserId");
            final String playerId = session.getString("playerId");

            final JSONObject content_src_sets = new JSONObject().append("content_src_ids",  new JSONObject().put("src_id_to_mux", new JSONObject()
                    .append("video_src_ids", videos)
                    .append("audio_src_ids", audios)));

            final JSONObject session_operation_auth_by_signature = new JSONObject().put("session_operation_auth_by_signature", new JSONObject()
                    .putOnce("token", token)
                    .put("signature", signature));

            //http_output_download_parameters=http
            //hls_parameters=m3u8
            final JSONObject protocol = new JSONObject()
                    .put("name", "http")
                    .put("parameters", new JSONObject().put("http_parameters", new JSONObject().put("parameters", new JSONObject().put(videoType.getType(), new JSONObject()
                            .put("use_well_known_port", wellKnownPort)
                            .put("use_ssl", SSL)
                            .put("transfer_preset", "")
                            .put("segment_duration", 6000)))));

            final JSONObject content_auth = new JSONObject()
                    .put("auth_type", authType)
                    .put("content_key_timeout", contentKeyTimeout)
                    .put("service_id", "nicovideo")
                    .put("service_user_id", serviceUserId);

            final JSONObject sessions = new JSONObject()
                    .put("recipe_id", recipeId)
                    .put("content_id", contentId)
                    .put("priority", priority)
                    .put("content_type", "movie")
                    .append("content_src_id_sets", content_src_sets)
                    .put("timing_constraint", "unlimited")
                    .put("keep_method", new JSONObject().put("heartbeat", new JSONObject().put("lifetime", lifetime)))
                    .put("client_info", new JSONObject().put("player_id", playerId))
                    .put("content_uri", "")
                    .put("session_operation_auth", session_operation_auth_by_signature)
                    .put("protocol", protocol)
                    .put("content_auth", content_auth);

            this.sentJson = new JSONObject().put("session", sessions);

            init_call();

            if (heartbeat) startHeartBeat();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初回のみ 1~2分のみの動画URLを生成します
     * <br>この機能は現在は必ず実行されます
     *
     * @throws IOException
     *         httpの通信が失敗した場合にエラーが発生します
     */
    private void init_call() throws IOException {
        //TODO: 実行できないようにオプションを追加する？

        Document document = Jsoup.connect("https://api.dmc.nico/api/sessions?_format=json")
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .requestBody(sentJson.toString())
                .ignoreContentType(true)
                .post();

        this.receivedJson = new JSONObject(document.text());
        this.success = receivedJson.getJSONObject("meta").getInt("status") == 201;

        JSONObject session = receivedJson.getJSONObject("data").getJSONObject("session");

        this.sessionId = session.getString("id");
        this.contentURL = session.getString("content_uri");
    }

    /**
     * ニコニコのサーバーにpingします
     *
     * 動画のリンクを維持しない場合は実行しないでください
     * @throws IOException
     *         httpの通信が失敗した場合にエラーが発生します
     */
    public void call() throws IOException {
        if (success) {
            Document document = Jsoup.connect("https://api.dmc.nico/api/sessions/" + sessionId + "?_format=json&_method=PUT")
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .requestBody(receivedJson.toString())
                    .ignoreContentType(true)
                    .post();

            this.receivedJson = new JSONObject(document.text());

            //1回取得された場合200になります
            this.success = receivedJson.getJSONObject("meta").getInt("status") == 200;
        }
    }

    /**
     * ハートビートを停止します
     */
    public void stopHeartBeat() {
        if (thread != null && !thread.isInterrupted() && thread.isAlive()) thread.interrupt();
    }

    /**
     * ハートビートを開始します
     *
     * 動画のリンクを維持しない場合は実行しないでください
     *
     * @see #call()
     */
    private void startHeartBeat() {
        stopHeartBeat();

        thread = new Thread(() -> {
            try {
                while (success) {
                    call();

                    Thread.sleep(delay);
                }
            } catch (Exception e) {
                success = false;

                Thread.currentThread().interrupt();
            }
        });

        thread.start();
    }

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isHeartBeat() {
        return heartbeat;
    }

    public void setHeartBeat(boolean heartbeat) {
        this.heartbeat = heartbeat;
    }

    public JSONObject getSentJson() {
        return sentJson;
    }

    public void setSentJson(JSONObject sentJson) {
        this.sentJson = sentJson;
    }

    public JSONObject getReceivedJson() {
        return receivedJson;
    }

    public void setReceivedJson(JSONObject receivedJson) {
        this.receivedJson = receivedJson;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getContentURL() {
        return contentURL;
    }

    public String getVideoURL() {
        return contentURL;
    }

    public void setContentURL(String contentURL) {
        this.contentURL = contentURL;
    }

    public void setVideoURL(String videoURL) {
        this.contentURL = videoURL;
    }

    public long getDelay() {
        return delay;
    }

    /**
     * ハートビートを実行する時間を設定します
     *
     * @throws IllegalStateException
     *         数値がマイナスだった場合エラーが発生します
     */
    public void setDelay(long delay) {
        if (delay >= 0) this.delay = delay;
        else throw new IllegalStateException("delay is negative (" + delay + ")");
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    /**
     * {@link CommentSettings#setGetComment(boolean)}
     *      を使用しないと必ず空のListが返ってきます
     *
     * @see CommentSettings
     */
    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public CommentSettings getCommentSettings() {
        return commentSettings;
    }

    public VideoType getVideoType() {
        return videoType;
    }

    public void asyncDownload(File file, Consumer<NicoVideoInfo> v) {
        new Thread(() -> {
            FileUtils.downloadFileFromURL(contentURL, file);

            v.accept(this);
        }).start();
    }

    public void syncDownload(File file) {
        FileUtils.downloadFileFromURL(contentURL, file);
    }
}