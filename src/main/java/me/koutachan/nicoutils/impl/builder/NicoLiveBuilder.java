package me.koutachan.nicoutils.impl.builder;

import me.koutachan.nicoutils.impl.options.enums.live.Latency;
import me.koutachan.nicoutils.impl.options.enums.live.Quality;

public class NicoLiveBuilder {

    private Quality quality = Quality.NORMAL;
    private Latency latency = Latency.HIGH;

    public Quality getQuality() {
        return quality;
    }

    public void setQuality(Quality quality) {
        this.quality = quality;
    }

    public Latency getLatency() {
        return latency;
    }

    public void setLatency(Latency latency) {
        this.latency = latency;
    }
}
