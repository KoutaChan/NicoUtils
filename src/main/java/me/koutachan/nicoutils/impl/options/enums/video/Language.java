package me.koutachan.nicoutils.impl.options.enums.video;

public enum Language {
    JAPANESE("0"),
    ENGLISH("1"),
    CHINESE("2");

    private final String language;

    Language(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }
}
