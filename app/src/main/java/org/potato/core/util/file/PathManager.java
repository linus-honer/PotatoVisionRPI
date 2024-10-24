package org.potato.core.util.file;

import java.io.File;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PathManager {
    private static PathManager instance;

    public static PathManager getInstance() {
        if(instance == null) {
            instance = new PathManager();
        }
        return instance;
    }

    public static final String LOG_PREFIX = "potatovision-";
    public static final String LOG_SUFFIX = ".log";
    public static final String LOG_TIME = "yyyy-MM-dd_hh-mm-ss";

    final File configDirectory;

    private PathManager() {
        this.configDirectory = new File(getRootPath().toUri());
    }

    public Path getRootPath() {
        return Path.of("config");
    }

    public Path getLogsPath() {
        return Path.of(configDirectory.toString(), "logs");
    }

    public Date logFileNameToDate(String name) throws ParseException {
        name = name.replace(LOG_PREFIX, "").replace(LOG_SUFFIX, "");
        DateFormat out = new SimpleDateFormat(LOG_TIME);
        return out.parse(name);
    }
}
