package me.koutachan.nicoutils.impl.builder;

import me.koutachan.nicoutils.impl.NicoLiveInfo;
import me.koutachan.nicoutils.impl.options.enums.live.Latency;
import me.koutachan.nicoutils.impl.options.enums.live.Quality;

public class NicoLiveBuilder {

    private Quality quality = Quality.NORMAL;
    private Latency latency = Latency.HIGH;

    private boolean openChatSocket = true;

    private String url;

    public String getURL() {
        return this.url;
    }

    public NicoLiveBuilder setURL(final String url) {
        this.url = url;

        return this;
    }

    public Quality getQuality() {
        return quality;
    }

    public NicoLiveBuilder setQuality(Quality quality) {
        this.quality = quality;

        return this;
    }

    public Latency getLatency() {
        return latency;
    }

    public NicoLiveBuilder setLatency(Latency latency) {
        this.latency = latency;

        return this;
    }

    public boolean isOpenChatSocket() {
        return openChatSocket;
    }

    public NicoLiveBuilder setOpenChatSocket(boolean openChatSocket) {
        this.openChatSocket = openChatSocket;

        return this;
    }

    public NicoLiveInfo create() {
        return new NicoLiveInfo(this);
    }

}