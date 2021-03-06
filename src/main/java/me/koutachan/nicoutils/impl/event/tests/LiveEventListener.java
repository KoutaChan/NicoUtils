package me.koutachan.nicoutils.impl.event.tests;

import jakarta.websocket.Session;
import me.koutachan.nicoutils.impl.data.Comment;
import me.koutachan.nicoutils.impl.event.LiveEvent;
import me.koutachan.nicoutils.impl.options.enums.live.Disconnect;

public class LiveEventListener extends LiveEvent {

    @Override
    public void onChatEvent(Comment comment) {
        //System.out.println(comment.getComment());
    }

    @Override
    public void onLiveJsonEvent(String json) {
        System.out.println(json);
    }

    @Override
    public void onChatJsonEvent(String json) {
        //System.out.println(json);
    }

    @Override
    public void onEndEvent(Session live, Session chat, Disconnect reason) {
        System.out.println("Ended=" + reason);
    }
}
