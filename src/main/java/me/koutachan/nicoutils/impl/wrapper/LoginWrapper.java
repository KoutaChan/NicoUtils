package me.koutachan.nicoutils.impl.wrapper;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class LoginWrapper {

    private boolean isLogin;

    private Map<String, String> headers;

    public static void main(String[] args) {
    }

    public void login(String email, String password) throws IOException {
        logout();

        String format = String.format("mail_tel=%s&password=%s", email, password);

        Connection.Response document = Jsoup.connect("https://secure.nicovideo.jp/secure/login?site=nicolive")
                .ignoreContentType(true)
                .requestBody(format)
                .method(Connection.Method.POST)
                .followRedirects(true)
                .execute();

        System.out.println(document.cookies());

        headers.putAll(document.cookies());

        for (String header : document.cookies().values()) {

        }

        isLogin = true;
    }

    public void logout() {
        isLogin = false;

        headers.clear();
    }

    public void release() {
        logout();
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
