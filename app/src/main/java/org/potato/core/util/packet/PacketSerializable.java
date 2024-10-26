package org.potato.core.util.packet;

import org.potato.core.util.packet.serde.PacketSerde;

public interface PacketSerializable<T> {
    PacketSerde<T> getSerde();
}
