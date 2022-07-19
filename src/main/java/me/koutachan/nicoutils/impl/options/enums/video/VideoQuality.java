package me.koutachan.nicoutils.impl.options.enums.video;

public enum VideoQuality {
    H264_720p("archive_h264_720p"),
    H264_480p("archive_h264_480p"),
    H264_360p_HIGH("archive_h264_360p"),
    H264_360p_LOW("archive_h264_360p_low");

    private String type;

    VideoQuality(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
