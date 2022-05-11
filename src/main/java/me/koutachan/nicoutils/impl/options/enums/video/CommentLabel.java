package me.koutachan.nicoutils.impl.options.enums.video;

public enum CommentLabel {
    DEFAULT_COMMENT("0"),
    OWNER_COMMENT("1"),
    ONE_CLICK_COMMENT("2");

    private final String fork;

    CommentLabel(String fork) {
        this.fork = fork;
    }

    public String getFork() {
        return fork;
    }
}
