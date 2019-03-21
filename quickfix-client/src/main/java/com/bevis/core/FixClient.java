package com.bevis.core;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import quickfix.*;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * The type Fix client.
 * @author yanghuadong
 * @date 2019 -03-06
 */
@Service
@Slf4j
public class FixClient {
    /**
     * The Initiator.
     */
    private Initiator initiator = null;
    /**
     * The Initiator started.
     */
    private boolean initiatorStarted = false;

    /**
     * The Client application.
     */
    @Resource
    private FixClientApplication clientApplication;

    /**
     * Start.
     */
    @PostConstruct
    public void start() {
        InputStream inputStream = null;
        try {
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            inputStream = resourceLoader.getResource("classpath:fix-client.cfg").getInputStream();
            SessionSettings settings = new SessionSettings(inputStream);
            MessageStoreFactory storeFactory = new FileStoreFactory(settings);
            MessageStoreFactory storeFactory1 = new MemoryStoreFactory();
            LogFactory logFactory = new FileLogFactory(settings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            initiator = new SocketInitiator(clientApplication, storeFactory1, settings, logFactory, messageFactory);
            logon();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Logon.
     */
    public synchronized void logon() {
        if (!initiatorStarted) {
            try {
                initiator.start();
                initiatorStarted = true;
                LOGGER.warn("fix client started!");
            } catch (Exception e) {
                LOGGER.error("logon failed", e);
            }
        } else {
            for (SessionID sessionId : initiator.getSessions()) {
                Session.lookupSession(sessionId).logon();
            }
        }
    }

    /**
     * Logout.
     */
    public void logout() {
        for (SessionID sessionId : initiator.getSessions()) {
            Session.lookupSession(sessionId).logout("user requested");
        }
    }

    /**
     * Session ids list.
     *
     * @return the list
     */
    public List<SessionID> sessionIds() {
        return initiator.getSessions();
    }
}