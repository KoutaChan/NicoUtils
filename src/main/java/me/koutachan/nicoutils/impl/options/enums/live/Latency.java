package me.koutachan.nicoutils.impl.options.enums.live;

public enum Latency {

    /**
     * high - 低遅延
     * ネットワークがあまり良くない場合はおすすめできません {@link #LOW} を使用してみてください
     */
    HIGH("high"),
    LOW("low");

    private final String type;

    Latency(String latency) {
        this.type = latency;
    }

    public String getType() {
        return type;
    }
}
