package me.koutachan.nicoutils.impl.data;

import org.json.JSONObject;
import org.jsoup.nodes.Element;

public class Comment {

    private String thread, user_id, mail, device, comment;

    private long no, vpos, date, date_uses, anonymity;

    private int premium;

    public Comment(Element element) {
        this.thread = element.attr("thread");
        this.no = Long.parseLong(element.attr("no"));
        this.vpos = Long.parseLong(element.attr("vpos"));
        this.date = Long.parseLong(element.attr("date"));
        this.date_uses = Long.parseLong(element.attr("date_usec"));
        this.anonymity = Long.parseLong(element.attr("anonymity"));
        this.user_id = element.attr("user_id");
        this.mail = element.attr("mail");

        this.comment = element.text();
    }

    public Comment(JSONObject jsonObject) {
        this.thread = jsonObject.getString("thread");
        this.no = jsonObject.getLong("no");
        this.vpos = jsonObject.getLong("vpos");
        this.date = jsonObject.getLong("date");
        this.date_uses = jsonObject.getLong("date_usec");

        if (jsonObject.has("anonymity")) {
            this.anonymity = jsonObject.getLong("anonymity");
        }

        this.user_id = jsonObject.getString("user_id");

        if (jsonObject.has("mail")) {
            this.mail = jsonObject.getString("mail");
        }

        if (jsonObject.has("premium")) {
            this.premium = jsonObject.getInt("premium");
        }

        this.comment = jsonObject.getString("content");
    }

    public String getThread() {
        return thread;
    }

    public void setThread(String thread) {
        this.thread = thread;
    }

    /**
     * コメントの発言順
     */
    public long getNo() {
        return no;
    }

    public void setNo(long no) {
        this.no = no;
    }

    public long getVpos() {
        return vpos;
    }

    public void setVpos(long vpos) {
        this.vpos = vpos;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public long getDateUses() {
        return date_uses;
    }

    public void setDateUses(long dateUses) {
        this.date_uses = dateUses;
    }

    /**
     * ニコ生限定、既にサイトを開く前からコメントが送信されている場合は 0になります
     */
    public long getAnonymity() {
        return anonymity;
    }

    public void setAnonymity(long anonymity) {
        this.anonymity = anonymity;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String userId) {
        this.user_id = userId;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public int getPremium() {
        return premium;
    }

    public void setPremium(int premium) {
        this.premium = premium;
    }

    /**
     * @return 現在は確定でnullです
     * TODO: デバイスを取得する
     */
    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    /**
     * @return 発言したコメント
     */
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
