package me.koutachan.nicoutils.impl.data;

import org.jsoup.nodes.Element;

public class Comment {

    private String thread, no, vpos, date, date_uses, anonymity, user_id, mail, device, comment;


    public Comment(Element element) {
        this.thread = element.attr("thread");
        this.no = element.attr("no");
        this.vpos = element.attr("vpos");
        this.date = element.attr("date");
        this.date_uses = element.attr("date_usec");
        this.anonymity = element.attr("anonymity");
        this.user_id = element.attr("user_id");
        this.mail = element.attr("mail");

        this.comment = element.text();
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
    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getVpos() {
        return vpos;
    }

    public void setVpos(String vpos) {
        this.vpos = vpos;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDateUses() {
        return date_uses;
    }

    public void setDateUses(String dateUses) {
        this.date_uses = dateUses;
    }

    public String getAnonymity() {
        return anonymity;
    }

    public void setAnonymity(String anonymity) {
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
