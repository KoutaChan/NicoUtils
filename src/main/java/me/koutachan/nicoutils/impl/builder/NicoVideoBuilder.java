package me.koutachan.nicoutils.impl.builder;

import me.koutachan.nicoutils.impl.NicoVideoInfo;
import me.koutachan.nicoutils.impl.options.video.CommentSettings;
import me.koutachan.nicoutils.impl.options.enums.video.VideoType;

public class NicoVideoBuilder {

    private boolean heartbeat;
    private String url;

    private CommentSettings commentSettings = new CommentSettings(this);
    private VideoType videoType = VideoType.HTTP;

    public boolean isHeartBeat() {
        return heartbeat;
    }

    /**
     * ハートビートをオンにするかを決定します
     * <br>オンにした場合 動画のリンクを維持するために30秒に1回ニコニコのサーバーにpingします
     * <br>
     * <br>必ず終わった場合は 以下のメソッドを実行してください
     * @see NicoVideoInfo#stopHeartBeat()
     */
    public NicoVideoBuilder setHeartBeat(final boolean heartbeat) {
        this.heartbeat = heartbeat;

        return this;
    }

    public String getURL() {
        return this.url;
    }

    /**
     * ニコ動のリンクを設定してください
     * <br>ニコ生などは現在再生できません
     */
    public NicoVideoBuilder setURL(final String url) {
        this.url = url;

        return this;
    }

    public CommentSettings getCommentSettings() {
        return commentSettings;
    }

    public NicoVideoBuilder setCommentSettings(CommentSettings commentSettings) {
        this.commentSettings = commentSettings;

        return this;
    }

    public VideoType getVideoType() {
        return videoType;
    }

    /**
     * 現在はM3U8かHTTPで取得できます
     * <br>
     * <br>M3U8はダウンロード、HTTPはHTTPで視聴できます
     * <br>通常はHTTPで取得されます
     * @see NicoVideoInfo#getVideoURL()
     */
    public NicoVideoBuilder setVideoType(VideoType videoType) {
        this.videoType = videoType;

        return this;
    }

    /**
     * <br>これまで設定した項目で情報を取得します
     *
     * @see NicoVideoInfo
     *
     * @throws Exception
     *         URLがnullかニコニコのURLでない場合エラーが発生します
     */
    public NicoVideoInfo create() {
        return new NicoVideoInfo(this);
    }
}