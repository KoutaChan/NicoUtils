package me.koutachan.nicoutils.util;

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
            try {
                qualities.add(Quality.valueOf(ob.toString().toUpperCase()));
            } catch (Exception ex) {
                qualities.add(Quality.AUTO);
            }
        }

        return qualities;
    }
}
