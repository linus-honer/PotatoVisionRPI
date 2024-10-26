package org.potato.core.util.packet.nt;

import java.util.List;
import java.util.ArrayList;

import org.potato.core.util.packet.Packet;
import org.potato.core.util.packet.PacketResult;
import org.potato.core.util.packet.serde.PacketSerde;

import edu.wpi.first.networktables.RawSubscriber;
import edu.wpi.first.networktables.TimestampedRaw;

public class PacketSubscriber<T> {
    public final RawSubscriber subscriber;
    private final PacketSerde<T> serde;

    private final Packet packet = new Packet(1);

    public PacketSubscriber(RawSubscriber subscriber, PacketSerde<T> serde) {
        this.subscriber = subscriber;
        this.serde = serde;
    }

    private PacketResult<T> parse(byte[] data, long timestamp) {
        packet.clear();
        packet.setData(data);
        if(packet.getLength() < 1) {
            return new PacketResult<T>();
        }

        return new PacketResult<T>(serde.unpack(packet), timestamp);
    }

    public PacketResult<T> get() {
        var data = subscriber.getAtomic();

        if(data.timestamp == 0) {
            return new PacketResult<T>();
        }

        return parse(data.value, data.timestamp);
    }

    public List<PacketResult<T>> getChanges() {
        List<PacketResult<T>> out = new ArrayList<>();

        TimestampedRaw[] changes = subscriber.readQueue();

        for(TimestampedRaw change : changes) {
            out.add(parse(change.value, change.timestamp));
        }

        return out;
    }

    public String getUUID() {
        var uuid = subscriber.getTopic().getProperty("message_uuid");
        return uuid.replace("\"", "");
    }
}
