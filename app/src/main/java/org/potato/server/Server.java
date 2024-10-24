package org.potato.server;

import org.potato.core.logging.Logger;
import org.potato.core.logging.Logger.LogClassType;

public class Server {
    private Logger logger = new Logger(getClass(), LogClassType.Master);

    public Server() {

    }

    public void testLog() {
        logger.error("error test");
    }
}
