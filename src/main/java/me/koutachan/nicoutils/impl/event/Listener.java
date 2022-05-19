package me.koutachan.nicoutils.impl.event;

import javax.annotation.processing.Generated;
import java.util.ArrayList;
import java.util.List;

public class Listener {

    private static final List<LiveEvent> liveListener = new ArrayList<>();

    public static void addListener(Object obj) {
        if (obj instanceof LiveEvent) {
            liveListener.add((LiveEvent) obj);
        }
    }

    public static List<LiveEvent> getLiveListener() {
        return liveListener;
    }
}
