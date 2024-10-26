package org.potato.core.util.packet.serde;

import org.potato.core.util.packet.Packet;

/*
 * This interface contains methods for converting raw packet data to usable data
 */

 public interface PacketSerde<T> {
    String getTypeName();

    String getUUID();

    default String getTypeID() {
        return "millerbots:" + getTypeName() + ":" + getUUID();
    }

    int getMaxByteSize();

    void pack(Packet packet, T value);

    T unpack(Packet packet);
}
