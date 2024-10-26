package org.potato.core.network.networktables;

import java.util.EnumSet;
import java.util.function.Consumer;

import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.Subscriber;

public class NTDataListener {
    private final NetworkTableInstance instance;
    @SuppressWarnings("unused")
    private final Subscriber subscriber;
    private final int listener;

    public NTDataListener(NetworkTableInstance instance, Subscriber subscriber, Consumer<NetworkTableEvent> dataConsumer) {
        this.instance = instance;
        this.subscriber = subscriber;
        listener = this.instance.addListener(subscriber, EnumSet.of(NetworkTableEvent.Kind.kValueAll), dataConsumer);
    }

    public void remove() {
        this.instance.removeListener(listener);
    }
}
