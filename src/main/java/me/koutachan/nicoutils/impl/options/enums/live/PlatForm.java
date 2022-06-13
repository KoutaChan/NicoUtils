package me.koutachan.nicoutils.impl.options.enums.live;

public enum PlatForm {
    WINDOWS("\"WINDOWS\"");
    //??????

    private final String platform;

    PlatForm(String platform) {
        this.platform = platform;
    }

    public String getPlatform() {
        return platform;
    }
}
