package me.koutachan.nicoutils.impl.util;

import me.koutachan.nicoutils.NicoUtils;

public class HardCodeUtils {

    public static String getUserAgent() {
        return "NicoUtils/" + NicoUtils.getVersion() + " (https://github.com/KoutaChan/NicoUtils)";
    }
}
