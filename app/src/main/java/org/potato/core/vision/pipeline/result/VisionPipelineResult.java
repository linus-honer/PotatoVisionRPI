package org.potato.core.vision.pipeline.result;

import org.potato.core.util.packet.PacketSerializable;
import org.potato.core.util.packet.serde.PacketSerde;

public class VisionPipelineResult implements PacketSerializable<VisionPipelineResult> {
    public static final VisionPipelineResultSerde serde = new VisionPipelineResultSerde();

    @Override
    public PacketSerde<VisionPipelineResult> getSerde() {
        return serde;
    }
}
