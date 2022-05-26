package me.koutachan.nicoutils.impl.event;

import jakarta.websocket.Session;
import me.koutachan.nicoutils.impl.data.Comment;
import me.koutachan.nicoutils.impl.options.enums.live.Disconnect;

public abstract class LiveEvent {

    public void onLiveJsonEvent(String json) {}

    public void onChatJsonEvent(String json) {}
    public void onChatEvent(Comment comment) {}

    public void onEndEvent(Session live, Session chat, Disconnect reason) {}
}