package org.potato.core.network.networktables;

import org.potato.core.util.packet.nt.PacketPublisher;
import org.potato.core.vision.pipeline.result.VisionPipelineResult;

import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.networktables.BooleanPublisher;
import edu.wpi.first.networktables.DoubleArrayPublisher;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.IntegerPublisher;
import edu.wpi.first.networktables.IntegerSubscriber;
import edu.wpi.first.networktables.IntegerTopic;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.PubSubOption;
import edu.wpi.first.networktables.RawPublisher;
import edu.wpi.first.networktables.StructPublisher;

public class NTTopics {
    public NetworkTable subTable;
    
    public IntegerPublisher pipelineIndexPublisher;
    public IntegerSubscriber pipelineIndexSubscriber;

    public DoublePublisher latencyPublisher;

    public BooleanPublisher hasTargetPublisher;
    public DoublePublisher targetPitchPublisher;
    public DoublePublisher targetYawPublisher;
    public DoublePublisher targetAreaPublisher;
    public DoublePublisher targetSkewPublisher;
    public StructPublisher<Transform3d> targetPosePublisher;
    public DoublePublisher bestTargetScreenXPublisher;
    public DoublePublisher bestTargetScreenYPublisher;

    public IntegerTopic heartbeatTopic;
    public IntegerPublisher heartbeatPublisher;

    public DoubleArrayPublisher cameraIntrinsicsPublisher;
    public DoubleArrayPublisher cameraDistortionPublisher;
    public DoublePublisher cameraFOVPublisher;

    //packets yay!!!
    PacketPublisher<VisionPipelineResult> pipelineResultPublisher;

    public void updateEntries() {
        pipelineIndexPublisher = subTable.getIntegerTopic("pipelineIndex").publish();
        pipelineIndexSubscriber = subTable.getIntegerTopic("pipelineIndex").subscribe(0);

        latencyPublisher = subTable.getDoubleTopic("latency").publish();

        hasTargetPublisher = subTable.getBooleanTopic("hasTarget").publish();
        targetPitchPublisher = subTable.getDoubleTopic("targetPitch").publish();
        targetYawPublisher = subTable.getDoubleTopic("targetYaw").publish();
        targetAreaPublisher = subTable.getDoubleTopic("targetArea").publish();
        targetSkewPublisher = subTable.getDoubleTopic("targetSkew").publish();
        targetPosePublisher = subTable.getStructTopic("targetPose", Transform3d.struct).publish();
        bestTargetScreenXPublisher = subTable.getDoubleTopic("bestTargetScreenX").publish();
        bestTargetScreenYPublisher = subTable.getDoubleTopic("bestTargetScreenY").publish();

        heartbeatTopic = subTable.getIntegerTopic("heartbeat");
        heartbeatPublisher = heartbeatTopic.publish();

        cameraIntrinsicsPublisher = subTable.getDoubleArrayTopic("cameraIntrinsics").publish();
        cameraDistortionPublisher = subTable.getDoubleArrayTopic("cameraDistortion").publish();
        cameraFOVPublisher = subTable.getDoubleTopic("cameraFOV").publish();

        pipelineResultPublisher = new PacketPublisher<VisionPipelineResult>(getPacketPublisher(), VisionPipelineResult.serde);
    }

    public void removeEntries() {
        if(pipelineIndexPublisher != null) pipelineIndexPublisher.close();
        if (pipelineIndexSubscriber != null) pipelineIndexSubscriber.close();

        if (latencyPublisher != null) latencyPublisher.close();
        if (hasTargetPublisher != null) hasTargetPublisher.close();
        if (targetPitchPublisher != null) targetPitchPublisher.close();
        if (targetAreaPublisher != null) targetAreaPublisher.close();
        if (targetYawPublisher != null) targetYawPublisher.close();
        if (targetPosePublisher != null) targetPosePublisher.close();
        if (targetSkewPublisher != null) targetSkewPublisher.close();
        if (bestTargetScreenXPublisher != null) bestTargetScreenXPublisher.close();
        if (bestTargetScreenYPublisher != null) bestTargetScreenYPublisher.close();

        if (heartbeatPublisher != null) heartbeatPublisher.close();

        if (cameraIntrinsicsPublisher != null) cameraIntrinsicsPublisher.close();
        if (cameraDistortionPublisher != null) cameraDistortionPublisher.close();

        if(pipelineResultPublisher != null) pipelineResultPublisher.close();
    }

    private RawPublisher getPacketPublisher() {
        return subTable.getRawTopic("rawBytes").publish(VisionPipelineResult.serde.getTypeString(), PubSubOption.periodic(0.01), PubSubOption.sendAll(true));
    }
}
