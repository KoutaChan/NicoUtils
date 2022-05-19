package me.koutachan.nicoutils.impl.event.tests;

import me.koutachan.nicoutils.impl.data.Comment;
import me.koutachan.nicoutils.impl.event.Listener;
import me.koutachan.nicoutils.impl.event.LiveEvent;

public class LiveEventListener extends LiveEvent {

    @Override
    public void onChatEvent(Comment comment) {
        System.out.println(comment.getComment());
    }

    @Override
    public void onJsonEvent(String json) {
        System.out.println(json);
    }
}
