package me.koutachan.nicoutils.impl.options.live;

import me.koutachan.nicoutils.impl.util.HardCodeUtils;

public class RequestSettings<T> {

    private final T object;

    private String agent = HardCodeUtils.getUserAgent();

    public RequestSettings(T object) {
        this.object = object;
    }

    public String getAgent() {
        return agent;
    }

    public T setAgent(String agent) {
        this.agent = agent;

        return object;
    }
}
