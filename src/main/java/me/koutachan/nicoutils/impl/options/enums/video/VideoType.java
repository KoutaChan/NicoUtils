package me.koutachan.nicoutils.impl.options.enums.video;

public enum VideoType {
    /**
     * @deprecated 変換をかけないと使えません、通常は {@link #HTTP} を使用してください
     */
    @Deprecated(since = "1.0", forRemoval = false)
    M3U8("hls_parameters"),
    HTTP("http_output_download_parameters");

    private final String type;

    VideoType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
