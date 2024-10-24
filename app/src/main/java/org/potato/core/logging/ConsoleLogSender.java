package org.potato.core.logging;

import org.potato.core.logging.Logger.LogType;

public class ConsoleLogSender implements LogSender {

    @Override
    public void log(String message, LogType type) {
        System.out.println(message);
    }
    
}
