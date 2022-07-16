package me.koutachan.nicoutils.impl.options.enums.live;

public enum Latency {

    /**
     * 低遅延 ネットワークがあまり良くない場合はおすすめできません
     * <br> {@link #LOW} を使用してみてください
     */
    HIGH("high"),
    LOW("low"),
    UNKNOWN("Unavailable Type");

    private final String type;

    Latency(String latency) {
        this.type = latency;
    }

    public String getType() {
        return type;
    }
}
