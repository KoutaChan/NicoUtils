package me.koutachan.nicoutils.impl.options.enums.video;

public enum VideoAudio {
    AUDIO_AAC_192kbs("archive_aac_192kbps"),
    AUDIO_AAC_64kbps("archive_aac_64kbps");

    private String type;

    VideoAudio(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
