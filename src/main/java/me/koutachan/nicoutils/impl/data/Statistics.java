package me.koutachan.nicoutils.impl.data;

import org.json.JSONObject;

public class Statistics {

    private long viewers, comments, adPoints;

    public Statistics(JSONObject object) {
        viewers = object.getLong("viewers");
        comments = object.getLong("comments");
        if (object.has("adPoints")) adPoints = object.getLong("adPoints");
    }

    public long getViewers() {
        return viewers;
    }

    public void setViewers(long viewers) {
        this.viewers = viewers;
    }

    public long getComments() {
        return comments;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }

    public long getAdPoints() {
        return adPoints;
    }

    public void setAdPoints(long adPoints) {
        this.adPoints = adPoints;
    }
}
