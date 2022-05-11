package me.koutachan.nicoutils.impl.options;

import me.koutachan.nicoutils.impl.builder.NicoVideoBuilder;
import me.koutachan.nicoutils.impl.options.enums.video.CommentLabel;
import me.koutachan.nicoutils.impl.options.enums.video.Language;

public class CommentSettings {
    private boolean getComment, addNGScoreInfo;

    private Language language = Language.JAPANESE;
    private CommentLabel label = CommentLabel.DEFAULT_COMMENT;

    private long getCommentFrom = -1000, unixTime;

    private final NicoVideoBuilder builder;

    public CommentSettings(NicoVideoBuilder builder) {
        this.builder = builder;
    }

    public boolean isGetComment() {
        return getComment;
    }

    /**
     * コメントを取得するか
     */
    public NicoVideoBuilder setGetComment(boolean getComment) {
        this.getComment = getComment;

        return builder;
    }

    public boolean isAddNGScoreInfo() {
        return addNGScoreInfo;
    }

    /**
     * NGスコアの情報を追加するか
     */
    public NicoVideoBuilder setAddNGScoreInfo(boolean addNGScoreInfo) {
        this.addNGScoreInfo = addNGScoreInfo;

        return builder;
    }

    public Language getLanguage() {
        return language;
    }

    /**
     * コメントの言語サーバー
     *
     * 設定しなかった場合 日本語になります
     */
    public NicoVideoBuilder setLanguage(Language language) {
        this.language = language;

        return builder;
    }

    public long getGetCommentFrom() {
        return getCommentFrom;
    }

    /**
     * どのコメントNoから取得するか (最大1000件)
     */
    public NicoVideoBuilder setGetCommentFrom(int getCommentFrom) {
        this.getCommentFrom = getCommentFrom;

        return builder;
    }

    public long getUnixTime() {
        return unixTime;
    }

    /**
     * 過去のコメントを遡ります
     *
     * @param unixTime
     *        Unixタイムで設定してください
     */
    public void setUnixTime(long unixTime) {
        this.unixTime = unixTime;
    }

    public CommentLabel getLabel() {
        return label;
    }

    /**
     * 取得するコメントタイプ
     *      DEFAULT_COMMENT - 通常コメント
     *      OWNER_COMMENT - オーナーコメント
     *      ONE_CLICK_COMMENT - ワンクリックコメント
     */
    public NicoVideoBuilder setLabel(CommentLabel label) {
        this.label = label;

        return builder;
    }
}
