package me.koutachan.nicoutils.impl.util;

import me.koutachan.nicoutils.impl.options.enums.live.LiveQuality;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QualityUtils {

    public static List<LiveQuality> getAllowedQuality(JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("availableQualities");

        final List<LiveQuality> qualities = new ArrayList<>();

        for (Object ob : jsonArray) {
            qualities.add(getQualityEnum(ob.toString()));
        }

        return qualities;
    }

    public static LiveQuality getQualityEnum(String str) {
        try {
            return LiveQuality.valueOf(str.toUpperCase());
        } catch (Exception ex) {
            return LiveQuality.AUTO;
        }
    }
}
