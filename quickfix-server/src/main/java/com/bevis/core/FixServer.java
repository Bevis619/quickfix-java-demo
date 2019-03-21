package com.bevis.core;

import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;

import java.util.List;

/**
 * The interface Fix server.
 *
 * @author yanghuadong
 * @date 2019 -03-18
 */
public interface FixServer {
    /**
     * The constant CONFIG_FILE.
     */
    String CONFIG_FILE = "classpath:fix-server.cfg";

    /**
     * Start.
     */
    void start();

    /**
     * Stop.
     */
    void stop();


    /**
     * Stop with slightly.
     */
    void stopWithSlightly();

    /**
     * Is running boolean.
     *
     * @return the boolean
     */
    boolean isRunning();

    /**
     * Gets session i ds.
     *
     * @return the session i ds
     */
    List<SessionID> getSessionIDs();

    /**
     * Gets sessions.
     *
     * @return the sessions
     */
    List<Session> getSessions();

    /**
     * Gets settings.
     *
     * @return the settings
     */
    SessionSettings getSettings();
}