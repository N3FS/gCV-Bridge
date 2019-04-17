package uk.co.n3fs.mc.gcvbridge.discord;

import org.javacord.api.event.connection.LostConnectionEvent;
import org.javacord.api.event.connection.ReconnectEvent;
import org.javacord.api.event.connection.ResumeEvent;
import org.slf4j.Logger;

public class ConnectionListener {

    private final Logger logger;

    public ConnectionListener(Logger logger) {
        this.logger = logger;
    }

    public void onConnectionLost(LostConnectionEvent event) {
        logger.warn("Lost connection to Discord!");
    }

    public void onReconnect(ReconnectEvent event) {
        logger.info("Reconnected to Discord.");
    }

    public void onResume(ResumeEvent event) {
        logger.info("Resumed connection to Discord.");
    }
}
