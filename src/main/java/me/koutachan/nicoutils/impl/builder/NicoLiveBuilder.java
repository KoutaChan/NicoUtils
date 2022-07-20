package me.koutachan.nicoutils.impl.builder;

import me.koutachan.nicoutils.NicoUtils;
import me.koutachan.nicoutils.impl.NicoLiveInfo;
import me.koutachan.nicoutils.impl.options.enums.live.Latency;
import me.koutachan.nicoutils.impl.options.enums.live.LiveQuality;
import me.koutachan.nicoutils.impl.options.RequestSettings;

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

    /**
     * 動画の遅延を決めます
     *
     * <br>{@link Latency#LOW} 低遅延
     * <br>{@link Latency#HIGH} 普通の遅延
     *
     * @param latency - 動画の遅延
     * @return - this
     */
    public NicoLiveBuilder setLatency(Latency latency) {
        this.latency = latency;

        return this;
    }

    public boolean isOpenChatSocket() {
        return openChatSocket;
    }

    /**
     * 通常チャットを取得する目的以外は必要ありません
     *
     * @param openChatSocket チャットソケットを開くか
     * @return - this
     * @see NicoUtils#addListener(Object)
     */
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