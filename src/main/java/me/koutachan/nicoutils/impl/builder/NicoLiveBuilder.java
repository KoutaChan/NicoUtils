package me.koutachan.nicoutils.impl.builder;

import me.koutachan.nicoutils.impl.NicoLiveInfo;
import me.koutachan.nicoutils.impl.options.enums.live.Latency;
import me.koutachan.nicoutils.impl.options.enums.live.LiveQuality;
import me.koutachan.nicoutils.impl.options.live.RequestSettings;

public class NicoLiveBuilder {

    private LiveQuality liveQuality = LiveQuality.NORMAL;
    private Latency latency = Latency.HIGH;

    private RequestSettings<NicoLiveBuilder> requestSettings = new RequestSettings<>(this);

    private boolean openChatSocket = true;

    private String url;

    public String getURL() {
        return this.url;
    }

    public NicoLiveBuilder setURL(final String url) {
        this.url = url;

        return this;
    }

    public LiveQuality getQuality() {
        return liveQuality;
    }

    public NicoLiveBuilder setQuality(LiveQuality liveQuality) {
        this.liveQuality = liveQuality;

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

    /**
     * リクエスト関係の設定
     */
    public RequestSettings<NicoLiveBuilder> getRequestSettings() {
        return requestSettings;
    }

    public NicoLiveBuilder setRequestSettings(RequestSettings<NicoLiveBuilder> requestSettings) {
        this.requestSettings = requestSettings;

        return this;
    }

    public NicoLiveInfo create() {
        return new NicoLiveInfo(this);
    }
}