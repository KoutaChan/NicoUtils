package me.koutachan.nicoutils.impl.options.enums.live;

public enum Quality {

    /**
     * プレミアムがtrueの場合は通常はプレミアムのみですが、混雑していない場合はプレミアムの機能を使える場合があります
     *
     * <br>通常は {@link #AUTO} を使用するか {@link #NORMAL} を使用してください
     */
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
