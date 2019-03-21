package com.bevis.core;

import com.bevis.factory.MyMessageFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import quickfix.*;
import quickfix.mina.acceptor.DynamicAcceptorSessionProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.*;

import static quickfix.Acceptor.*;

/**
 * The type Default fix server.
 *
 * @author yanghuadong
 * @date 2019 -03-18
 */
@Slf4j
public class DefaultFixServer implements FixServer {

    /**
     * The constant acceptor.
     */
    private static ThreadedSocketAcceptor acceptor = null;

    /**
     * The Dynamic session mappings.
     */
    private final Map<InetSocketAddress, List<DynamicAcceptorSessionProvider.TemplateMapping>> dynamicSessionMappings = new HashMap<>();
    /**
     * The Fix acceptor.
     */
    private Application fixAcceptor;
    /**
     * The Is running.
     */
    private volatile boolean isRunning;

    /**
     * Instantiates a new Default fix server.
     *
     * @param fixAcceptor the fix acceptor
     */
    public DefaultFixServer(Application fixAcceptor) {
        this.fixAcceptor = fixAcceptor;
    }

    /**
     * Start.
     */
    @Override
    public synchronized void start() {
        if (isRunning) {
            LOGGER.warn("fix server has been started.");
            return;
        }

        LOGGER.info("fix server is starting...");
        InputStream inputStream = null;
        try {
            ResourceLoader resourceLoader = new DefaultResourceLoader();
            inputStream = resourceLoader.getResource(CONFIG_FILE).getInputStream();
            SessionSettings settings = new SessionSettings(inputStream);
            MessageStoreFactory storeFactory1 = new MemoryStoreFactory();
            LogFactory logFactory = new FileLogFactory(settings);
            MessageFactory messageFactory = new MyMessageFactory();
            acceptor = new ThreadedSocketAcceptor(fixAcceptor, storeFactory1, settings, logFactory, messageFactory);
            configureDynamicSessions(settings, fixAcceptor, storeFactory1, logFactory, messageFactory);
            acceptor.start();
            isRunning = true;
            LOGGER.warn("fix server has started.");
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            closeStream(inputStream);
        }
    }

    /**
     * Stop.
     */
    @Override
    public void stop() {
        if (isRunning && null != acceptor) {
            acceptor.stop(true);
            isRunning = false;
            LOGGER.warn("fix server has been stopped.");
        }
    }

    /**
     * Stop with slightly.
     */
    @Override
    public void stopWithSlightly() {
        if (isRunning && null != acceptor) {
            acceptor.stop();
            isRunning = false;
            LOGGER.warn("fix server has been stopped slightly.");
        }
    }

    /**
     * Is running boolean.
     *
     * @return the boolean
     */
    @Override
    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Gets session i ds.
     *
     * @return the session i ds
     */
    @Override
    public List<SessionID> getSessionIDs() {
        if (null != acceptor) {
            return acceptor.getSessions();
        }

        return null;
    }

    /**
     * Gets sessions.
     *
     * @return the sessions
     */
    @Override
    public List<Session> getSessions() {
        if (null != acceptor) {
            return acceptor.getManagedSessions();
        }

        return null;
    }

    /**
     * Gets settings.
     *
     * @return the settings
     */
    @Override
    public SessionSettings getSettings() {
        if (null != acceptor) {
            acceptor.getSettings();
        }

        return null;
    }

    /**
     * Close stream.
     *
     * @param inputStream the input stream
     */
    private void closeStream(InputStream inputStream) {
        if (null != inputStream) {
            try {
                inputStream.close();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Configure dynamic sessions.
     *
     * @param settings            the settings
     * @param application         the application
     * @param messageStoreFactory the message store factory
     * @param logFactory          the log factory
     * @param messageFactory      the message factory
     * @throws ConfigError       the config error
     * @throws FieldConvertError the field convert error
     */
    private void configureDynamicSessions(SessionSettings settings, Application application, MessageStoreFactory messageStoreFactory, LogFactory logFactory, MessageFactory messageFactory) throws ConfigError, FieldConvertError {
        Iterator<SessionID> sectionIterator = settings.sectionIterator();
        while (sectionIterator.hasNext()) {
            SessionID sessionID = sectionIterator.next();
            if (isSessionTemplate(settings, sessionID)) {
                InetSocketAddress address = getAcceptorSocketAddress(settings, sessionID);
                getMappings(address).add(new DynamicAcceptorSessionProvider.TemplateMapping(sessionID, sessionID));
            }
        }

        for (Map.Entry<InetSocketAddress, List<DynamicAcceptorSessionProvider.TemplateMapping>> entry : dynamicSessionMappings.entrySet()) {
            acceptor.setSessionProvider(entry.getKey(), new DynamicAcceptorSessionProvider(
                    settings, entry.getValue(), application, messageStoreFactory, logFactory,
                    messageFactory));
        }
    }

    /**
     * Gets mappings.
     *
     * @param address the address
     * @return the mappings
     */
    private List<DynamicAcceptorSessionProvider.TemplateMapping> getMappings(InetSocketAddress address) {
        return dynamicSessionMappings.computeIfAbsent(address, k -> new ArrayList<>());
    }

    /**
     * Gets acceptor socket address.
     *
     * @param settings  the settings
     * @param sessionID the session id
     * @return the acceptor socket address
     * @throws ConfigError       the config error
     * @throws FieldConvertError the field convert error
     */
    private InetSocketAddress getAcceptorSocketAddress(SessionSettings settings, SessionID sessionID) throws ConfigError, FieldConvertError {
        String acceptorHost = "0.0.0.0";
        if (settings.isSetting(sessionID, SETTING_SOCKET_ACCEPT_ADDRESS)) {
            acceptorHost = settings.getString(sessionID, SETTING_SOCKET_ACCEPT_ADDRESS);
        }

        int acceptorPort = (int) settings.getLong(sessionID, SETTING_SOCKET_ACCEPT_PORT);
        return new InetSocketAddress(acceptorHost, acceptorPort);
    }

    /**
     * Is session template boolean.
     *
     * @param settings  the settings
     * @param sessionID the session id
     * @return the boolean
     * @throws ConfigError       the config error
     * @throws FieldConvertError the field convert error
     */
    private boolean isSessionTemplate(SessionSettings settings, SessionID sessionID) throws ConfigError, FieldConvertError {
        return settings.isSetting(sessionID, SETTING_ACCEPTOR_TEMPLATE) && settings.getBool(sessionID, SETTING_ACCEPTOR_TEMPLATE);
    }
}