package org.potato.core.util.packet.nt;

import org.potato.core.util.packet.Packet;
import org.potato.core.util.packet.serde.PacketSerde;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.wpi.first.networktables.RawPublisher;

public class PacketPublisher<T> {
    public final RawPublisher publisher;
    private final PacketSerde<T> serde;

    public PacketPublisher(RawPublisher publisher, PacketSerde<T> serde) {
        this.publisher = publisher;
        this.serde = serde;

        ObjectMapper mapper = new ObjectMapper();
        try {
            this.publisher.getTopic().setProperty("message_uuid", mapper.writeValueAsString(serde.getUUID()));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void set(T value) {
        set(value, serde.getMaxByteSize());
    }

    public void set(T value, int byteSize) {
        Packet packet = new Packet(byteSize);
        serde.pack(packet, value);
        publisher.set(packet.getWrittenDataCopy());
    }

    //TODO: schema
}
