package org.potato.core.network.networktables;

import java.util.EnumSet;
import java.io.IOException;

import org.potato.VersionData;
import org.potato.core.logging.Logger;
import org.potato.core.logging.Logger.LogClassType;
import org.potato.core.util.task.TimedTaskManager;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.StringSubscriber;
import edu.wpi.first.networktables.NetworkTableEvent.Kind;

public class NTManager {
    private static NTManager instance;

    public static NTManager getInstance() {
        if(instance == null) {
            instance = new NTManager();
        }
        return instance;
    }

    Logger logger = new Logger(NTManager.class, LogClassType.NetworkTables);

    private final NetworkTableInstance ntInstance = NetworkTableInstance.getDefault();
    public final NetworkTable rootTable = ntInstance.getTable(NTConstants.ROOT_TABLE_NAME);

    private boolean isTryingConnection = false;

    private StringSubscriber fieldLayoutSubscriber = rootTable.getStringTopic(NTConstants.FIELD_LAYOUT_KEY).subscribe("");

    private final NTConnectionListener connectionListener = new NTConnectionListener();

    private NTManager() {
        ntInstance.addLogger(255, 255, (event -> {}));
        ntInstance.addConnectionListener(true, connectionListener);

        ntInstance.addListener(fieldLayoutSubscriber, EnumSet.of(Kind.kValueAll), this::onFieldLayoutChanged);

        TimedTaskManager.getInstance().addTask("NTManager", this::tick, 5000);

        broadcastStatus();
    }

    private void tick() {
        if(!ntInstance.isConnected() && !isTryingConnection) {
            isTryingConnection = true;
            logger.error("Could not connect to the robot, retrying...");
        }
    }

    private void onFieldLayoutChanged(NetworkTableEvent event) {
        String newValue = event.valueData.value.getString();
        try {
            logger.debug("Received a new field layout: " + newValue);
            //TODO: set the field layout in another class or smth
            throw new IOException();
        } catch (IOException e) {
            logger.error("Error setting field layout");
        }
    }

    public void broadcastStatus() {
        //TODO: broadcast the networktables status
    }

    public void broadcastVersion() {
        rootTable.getEntry("version").setString(VersionData.VERISON_NAME);
        rootTable.getEntry("versionDate").setString(VersionData.VERSION_DATE);
        rootTable.getEntry("versionDetails").setString(VersionData.VERSION_DETAILS);
    }

    public void setConfig(NTConfig config) {
        if(config.hostServer) {
            setServerMode();
        } else {
            setClientMode(config.serverAddress);
        }
        broadcastVersion();
    }

    private boolean isTeamNumber(String in) {
        return in.length() < 5;
    }

    private void setClientMode(String serverAddress) {
        ntInstance.stopServer();
        ntInstance.startClient4("potatovision");
        if(isTeamNumber(serverAddress)) {
            int teamNumber = Integer.parseInt(serverAddress);
            if(!isTryingConnection) {
                logger.info("Starting NetworkTables client with team number " + teamNumber);
            }
            ntInstance.setServerTeam(teamNumber);
        } else {
            if(!isTryingConnection) {
                logger.info("Starting NetworkTables client with IP" + serverAddress);
            }
            ntInstance.setServer(serverAddress);
        }
        ntInstance.startDSClient();
        broadcastVersion();
    }

    private void setServerMode() {
        logger.info("Starting NetworkTables server");
        ntInstance.stopClient();
        ntInstance.startServer();
        broadcastVersion();
    }
}
