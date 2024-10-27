package org.potato.core.vision.pipeline.result;

import org.potato.core.util.packet.Packet;
import org.potato.core.util.packet.serde.PacketSerde;

public class VisionPipelineResultSerde implements PacketSerde<VisionPipelineResult> {

    @Override
    public String getTypeName() {
        return "VisionPipelineResult";
    }

    @Override
    public String getUUID() {
        //TODO: uuid
        return "";
    }

    @Override
    public int getMaxByteSize() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getMaxByteSize'");
    }

    @Override
    public void pack(Packet packet, VisionPipelineResult value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pack'");
    }

    @Override
    public VisionPipelineResult unpack(Packet packet) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'unpack'");
    }
    
}
