package me.koutachan.nicoutils.impl.options.enums.live;

public enum Quality {

    SUPER_HIGH("super_high", true),
    HIGH("high", true),
    NORMAL("normal", false),
    LOW("low", false),
    SUPER_LOW("super_low", false),
    AUTO("abr", false);

    private final String type;

    private final boolean premium;

    Quality(String quality, boolean premium) {
        this.type = quality;

        this.premium = premium;
    }

    public String getType() {
        return type;
    }

    public boolean isPremium() {
        return premium;
    }
}
