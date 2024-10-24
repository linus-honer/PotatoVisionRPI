package org.potato.core.logging;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.potato.core.util.TextColors;
import org.potato.core.util.file.PathManager;

public class Logger {
    public static enum LogType {
        ERROR(0, TextColors.RED),
        WARNING(0, TextColors.YELLOW),
        INFO(0, TextColors.LIME),
        DEBUG(0, TextColors.WHITE);

        public final int code;
        public final String color;

        LogType(int code, String color) {
            this.code = code;
            this.color = color;
        }
    }
    public static enum LogClassType {
        Master,
        Generic,
        Hardware,
        WebServer,
        Camera,
        Pipeline,
        NetworkTables,
        Config,
    }

    public static final int MAX_LOGS = 250;

    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

    private final String className;
    private final LogClassType logType;

    private static final List<LogSender> currentSenders = new ArrayList<>();

    public Logger(Class<?> thisClass, LogClassType logType) {
        this.className = thisClass.getSimpleName();
        this.logType = logType;
        currentSenders.add(new ConsoleLogSender());
    }

    public static String getDate() {
        return simpleDateFormat.format(new Date());
    }

    public static void addFileSender(Path logFilePath) {
        File file = logFilePath.toFile();
        if(!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        currentSenders.add(new FileLogSender(logFilePath));
    }

    public static void clearLogFiles(Path folder) {
        File[] logs = folder.toFile().listFiles();
        if(logs == null) {
            return;
        }
        LinkedList<File> logFiles = new LinkedList<>(Arrays.asList(logs));
        HashMap<File, Date> logFileDateMap = new HashMap<>();

        logFiles.removeIf(
            (File f) -> {
                try {
                    logFileDateMap.put(f, PathManager.getInstance().logFileNameToDate(f.getName()));
                    return false;
                } catch (ParseException e) {
                    return true;
                }
            }
        );

        logFiles.sort(
            (File f1, File f2) -> {
                Date date1 = logFileDateMap.get(f1);
                Date date2 = logFileDateMap.get(f2);
                return date1.compareTo(date2);
            }
        );

        int num = 0;
        for(File file : logFiles) {
            if(num < MAX_LOGS) {
                num++;
            } else {
                file.delete();
            }
        }
    }

    public static String format(String logMessage, LogType type, LogClassType classType, String className, boolean color) {
        String date = getDate();
        StringBuilder builder = new StringBuilder();
        if(color) {
            builder.append(type.color);
        }
        builder.append("[")
            .append(date)
            .append("] [")
            .append(classType)
            .append(" - ")
            .append(className)
            .append("] [")
            .append(type.name())
            .append("] ")
            .append(logMessage);
        if(color) {
            builder.append(TextColors.DEFAULT);
        }
        return builder.toString();
    }

    public void log(String message, LogType type) {
        log(message, type, logType, className);
    }

    private static void log(String message, LogType type, LogClassType classType, String className) {
        for(LogSender s : currentSenders) {
            boolean color = s instanceof ConsoleLogSender;
            var formatted = format(message, type, classType, className, color);
            s.log(formatted, type);
        }
    }

    public void error(String message) {
        log(message, LogType.ERROR);
    }

    public void error(String message, Throwable t) {
        log(message + ": " + t.getMessage(), LogType.ERROR);
        t.printStackTrace();
    }

    public void warn(String message) {
        log(message, LogType.WARNING);
    }

    public void info(String message) {
        log(message, LogType.INFO);
    }

    public void debug(String message) {
        log(message, LogType.DEBUG);
    }
}
