package org.potato.core.util.packet;

public class PacketResult<T> {
    public final T value;
    public final long timestamp;

    public PacketResult(T value, long timestamp) {
        this.value = value;
        this.timestamp = timestamp;
    }

    public PacketResult() {
        this(null, 0);
    }
}
