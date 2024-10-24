package org.potato.core.logging;

public interface LogSender {
    void log(String message, Logger.LogType type);
}
