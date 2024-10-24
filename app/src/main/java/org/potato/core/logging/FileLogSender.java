package org.potato.core.logging;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

import org.potato.core.logging.Logger.LogType;
import org.potato.core.util.task.TimedTaskManager;

public class FileLogSender implements LogSender {

    private OutputStream out;
    private boolean flush;

    public FileLogSender(Path logFilePath) {
        try {
            this.out = new FileOutputStream(logFilePath.toFile());
            TimedTaskManager.getInstance().addTask(
                "FileLogSender",
                () -> {
                    try {
                        if(flush) {
                            out.flush();
                            flush = false;
                        }
                    } catch (IOException e) {

                    }
                }, 3000L
            );
        } catch (FileNotFoundException e) {
            out = null;
            System.err.println("Unable to send a log to the file at: " + logFilePath);
        }
    }

    @Override
    public void log(String message, LogType type) {
        message += "\n";
        try {
            out.write(message.getBytes());
            flush = true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("No stream available for writing to a log file");
        }
    }
    
}
