package org.potato.core.network.networktables;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.potato.core.logging.Logger;
import org.potato.core.logging.Logger.LogClassType;
import org.potato.core.vision.pipeline.result.VisionPipelineResult;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;

public class NTPublisher implements Consumer<VisionPipelineResult> {
    private final Logger logger = new Logger(NTPublisher.class, LogClassType.NetworkTables);

    private final NetworkTable rootTable = NTManager.getInstance().rootTable;

    private final NTTopics topics = new NTTopics();

    NTDataListener pipelineIndexListener;
    private final Supplier<Integer> pipelineIndexSupplier;
    private final Consumer<Integer> pipelineIndexConsumer;

    public NTPublisher(String cameraName, Supplier<Integer> pipelineIndexSupplier, Consumer<Integer> pipelineIndexConsumer) {
        this.pipelineIndexSupplier = pipelineIndexSupplier;
        this.pipelineIndexConsumer = pipelineIndexConsumer;

        updateCameraName(cameraName);
        updateEntries();
    }

    private void updateCameraName(String name) {
        removeEntries();
        topics.subTable = rootTable.getSubTable(name);
        updateEntries();
    }

    private void updateEntries() {
        if(pipelineIndexListener != null) {
            pipelineIndexListener.remove();
        }
        
        topics.updateEntries();

        pipelineIndexListener = new NTDataListener(topics.subTable.getInstance(), topics.pipelineIndexSubscriber, this::onPipelineIndexChange);
    }

    private void removeEntries() {
        if(pipelineIndexListener != null) {
            pipelineIndexListener.remove();
        }
        topics.removeEntries();
    }

    private void onPipelineIndexChange(NetworkTableEvent event) {
        int newIndex = (int) event.valueData.value.getInteger();
        int oldIndex = pipelineIndexSupplier.get();

        if(newIndex < 0) {
            topics.pipelineIndexPublisher.set(oldIndex);
            return;
        }

        if(newIndex == oldIndex) {
            logger.debug("Attempted to change pipeline index to the value it already was, >:(");
            return;
        }

        pipelineIndexConsumer.accept(newIndex);
        int setIndex = pipelineIndexSupplier.get();
        if(newIndex != setIndex) {
            topics.pipelineIndexPublisher.set(setIndex);
            logger.error("Pipeline index change failed >:(");
            return;
        }
        logger.debug("Set pipeline index to " + newIndex);
    }

    @Override
    public void accept(VisionPipelineResult result) {
        //TODO: accepting stuff
    }
}
