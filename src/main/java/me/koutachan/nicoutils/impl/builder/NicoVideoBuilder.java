package me.koutachan.nicoutils.impl.builder;

import me.koutachan.nicoutils.impl.NicoVideoInfo;
import me.koutachan.nicoutils.impl.options.enums.video.VideoQuality;
import me.koutachan.nicoutils.impl.options.enums.video.VideoType;
import me.koutachan.nicoutils.impl.options.RequestSettings;
import me.koutachan.nicoutils.impl.options.video.CommentSettings;
import me.koutachan.nicoutils.impl.options.LoginSettings;

public class NicoVideoBuilder {

    private boolean heartbeat;
    private String url;

    private RequestSettings<NicoVideoBuilder> requestSettings = new RequestSettings<>(this);
    private LoginSettings<NicoVideoBuilder> loginSettings = new LoginSettings<>(this, requestSettings);

    private CommentSettings commentSettings = new CommentSettings(this);
    private VideoType videoType = VideoType.HTTP;

    public boolean isHeartBeat() {
        return heartbeat;
    }

    /**
     * ハートビートをオンにするかを決定します
     * <br>オンにした場合 動画のリンクを維持するために40秒に1回ニコニコのサーバーにpingします
     * <br>
     * <br>必ず終わった場合は 以下のメソッドを実行してください
     * <br>{@link NicoVideoInfo#stopHeartBeat()}
     *
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
     * @deprecated
     * この方式はおすすめできません
     * <br>ニコニコサーバーへアクセスしてから可能な画質が分かるため
     * <br>予想だけで画質を決定するのはいいことではないと思います
     */
    @Deprecated
    private NicoVideoBuilder setVideoQuality(VideoQuality quality) {
        return this;
    }

    /**
     * ユーザーエージェントを設定できます
     * @see RequestSettings#setAgent(String)
     */
    public RequestSettings<NicoVideoBuilder> getRequestSettings() {
        return requestSettings;
    }

    public NicoVideoBuilder setRequestSettings(RequestSettings<NicoVideoBuilder> requestSettings) {
        this.requestSettings = requestSettings;

        return this;
    }

    /**
     * ニコニコにログインするために必要
     * <br>ログインしたい場合は {@link LoginSettings#login(String, String)} を使ってログインしてください
     */
    public LoginSettings<NicoVideoBuilder> getLoginSettings() {
        return loginSettings;
    }

    public NicoVideoBuilder setLoginSettings(LoginSettings<NicoVideoBuilder> loginSettings) {
        this.loginSettings = loginSettings;

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