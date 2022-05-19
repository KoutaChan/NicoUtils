package me.koutachan.nicoutils.impl.event;

import me.koutachan.nicoutils.impl.data.Comment;

public abstract class LiveEvent {

    public void onJsonEvent(String json) {}
    public void onChatEvent(Comment comment) {}
}