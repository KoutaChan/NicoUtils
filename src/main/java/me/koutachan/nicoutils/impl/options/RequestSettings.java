package me.koutachan.nicoutils.impl.options;

import me.koutachan.nicoutils.impl.util.HardCodeUtils;

import java.util.HashMap;
import java.util.Map;

public class RequestSettings<T> {

    private final T object;

    private String agent = HardCodeUtils.getUserAgent();

    private Map<String,String> cookie = new HashMap<>();

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

    //not implemented!
    public T addCookie(String name, String value) {
        cookie.put(name, value);

        return object;
    }

    //not implemented!
    public Map<String, String> getCookie() {
        return cookie;
    }

    //not implemented!
    public T setCookie(Map<String, String> cookie) {
        this.cookie = cookie;

        return object;
    }
}
