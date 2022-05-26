package me.koutachan.nicoutils.impl.options.live;

import me.koutachan.nicoutils.impl.builder.NicoLiveBuilder;

public class RequestSettings {

    private String agent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.67 Safari/537.36";

    private final NicoLiveBuilder builder;

    public RequestSettings(NicoLiveBuilder builder) {
        this.builder = builder;
    }

    public String getAgent() {
        return agent;
    }

    public NicoLiveBuilder setAgent(String agent) {
        this.agent = agent;

        return builder;
    }
}
