package me.koutachan.nicoutils;

import me.koutachan.nicoutils.impl.builder.NicoLiveBuilder;
import me.koutachan.nicoutils.impl.builder.NicoVideoBuilder;
import me.koutachan.nicoutils.impl.event.Listener;
import me.koutachan.nicoutils.impl.event.tests.LiveEventListener;

public class NicoUtils {

    public static NicoVideoBuilder getVideoBuilder() {
        return new NicoVideoBuilder();
    }

    /**
     * API本文から:
     *
     * 利用制限
     * 本APIは、解析を目的とした負荷の高い利用法が想定されているため、
     *
     * 同時接続数の制限、および 同一ユーザーによる過度な利用に制限 などの利用制限を設けております。
     *
     * 制限を超えてAPIリクエストを行った場合、正常に検索結果が返されません。 繰り返しAPIリクエストを行う場合は、前回のAPIレスポンス時間と同じだけ待機時間を設けてご利用ください。
     */
    //public static NicoVideoSearchBuilder getSearchBuilder() {
    //    return new NicoVideoSearchBuilder();
    //}

    public static NicoLiveBuilder getLiveBuilder() {
        return new NicoLiveBuilder();
    }

    /**
     * @param obj イベントのオブジェクトを入れてください
     * <br> 現在登録可能なイベントのオブジェクトは
     * <br> {@link LiveEventListener} のみです
     */
    public static void addListener(Object obj) {
        Listener.addListener(obj);
    }
}