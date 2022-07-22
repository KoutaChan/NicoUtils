package me.koutachan.nicoutils.impl.options;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginSettings<T> {

    private boolean isLogin;

    private final RequestSettings<?> requestSettings;
    private final T object;

    public LoginSettings(T object, RequestSettings<?> requestSettings) {
        this.object = object;
        this.requestSettings = requestSettings;
    }

    private final Pattern pattern = Pattern.compile("message=([A-z]+)");

    private Map<String, String> loginCookie = new HashMap<>();

    public T login(String email, String password) {
        try {
            logout();

            String format = String.format("mail_tel=%s&password=%s", email, password);

            Connection.Response document = Jsoup.connect("https://secure.nicovideo.jp/secure/login?site=nicolive")
                    .ignoreContentType(true)
                    .requestBody(format)
                    .method(Connection.Method.POST)
                    .followRedirects(true)
                    .execute();

            Matcher matches = pattern.matcher(document.url().toString());

            if (matches.find()) {
                throw new IllegalStateException("Login Failed (code: " + matches.group(1) + ")");
            }

            loginCookie.putAll(document.cookies());
            addCookie(loginCookie);

            isLogin = true;
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return object;
    }

    public T logout() {
        isLogin = false;
        loginCookie.clear();

        return object;
    }

    public T release() {
        loginCookie = null;
        isLogin = false;

        return object;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public T setLogin(boolean login) {
        isLogin = login;
        return object;
    }

    public Map<String, String> getLoginCookie() {
        return loginCookie;
    }

    public T setLoginCookie(Map<String, String> headers) {
        this.loginCookie = headers;
        return object;
    }

    public T addCookie(Map<String, String> header) {
        RequestSettings<?> requestSettings = getRequestSettings();

        if (requestSettings != null) {
            requestSettings.getCookie().putAll(header);
        }

        return object;
    }

    public RequestSettings<?> getRequestSettings() {
        return requestSettings;
    }
}
