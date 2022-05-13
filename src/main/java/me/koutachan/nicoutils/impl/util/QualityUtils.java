package me.koutachan.nicoutils.impl.util;

import me.koutachan.nicoutils.impl.options.enums.live.Quality;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QualityUtils {

    public static List<Quality> getAllowedQuality(JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("availableQualities");

        final List<Quality> qualities = new ArrayList<>();

        for (Object ob : jsonArray) {
            qualities.add(getQualityEnum(ob.toString()));
        }

        return qualities;
    }

    public static Quality getQualityEnum(String str) {
        try {
            return Quality.valueOf(str.toUpperCase());
        } catch (Exception ex) {
            return Quality.AUTO;
        }
    }
}
