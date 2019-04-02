package com.bevis.core;

import com.bevis.utils.Md5Util;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import quickfix.*;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.field.*;
import quickfix.fix44.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The type Fix client application.
 *
 * @author yanghuadong
 * @date 2019 -03-13
 */
@Service
@Slf4j
public class FixClientApplication extends MessageCracker implements Application {
    /**
     * On create.
     *
     * @param sessionId the session id
     */
    @Override
    public void onCreate(SessionID sessionId) {
        LOGGER.warn("启动时候调用此方法创建:{}", sessionId);
    }

    /**
     * On logon.
     *
     * @param sessionId the session id
     */
    @Override
    public void onLogon(SessionID sessionId) {
        LOGGER.warn("客户端登陆成功时候调用此方法:{}", sessionId);
    }

    /**
     * On logout.
     *
     * @param sessionId the session id
     */
    @Override
    public void onLogout(SessionID sessionId) {
        LOGGER.warn("客户端断开连接时候调用此方法:{}", sessionId);
        try {
            Session.lookupSession(sessionId).reset();
        } catch (IOException e) {
            LOGGER.error(sessionId + ":" + e.getMessage(), e);
        }

    }

    /**
     * To admin.
     *
     * @param message   the message
     * @param sessionId the session id
     */
    @Override
    public void toAdmin(Message message, SessionID sessionId) {
        if (isMessageOfType(message, MsgType.LOGON)) {
            message.setField(new EncryptMethod(EncryptMethod.NONE_OTHER));
            message.getHeader().setField(new SendingTime(LocalDateTime.now()));
            try {
                String sign = geLogonSign((Logon) message);
                message.setField(new RawData(sign));
                message.setField(new RawDataLength(sign.length()));
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        LOGGER.warn("发送会话消息时候调用此方法,sessionId={}, message={}", sessionId, message);

    }

    /**
     * From admin.
     *
     * @param message   the message
     * @param sessionId the session id
     * @throws FieldNotFound     the field not found
     * @throws IncorrectTagValue the incorrect tag value
     */
    @Override
    public void fromAdmin(Message message, SessionID sessionId) throws FieldNotFound, IncorrectTagValue {
        LOGGER.warn("接收会话类型消息时调用此方法,sessionId={}, message={}", sessionId, message);
        try {
            crack(message, sessionId);
        } catch (UnsupportedMessageType unsupportedMessageType) {
            unsupportedMessageType.printStackTrace();
        }
    }

    /**
     * To app.
     *
     * @param message   the message
     * @param sessionId the session id
     * @throws DoNotSend the do not send
     */
    @Override
    public void toApp(Message message, SessionID sessionId) {
        LOGGER.warn("发送业务消息时候调用此方法,sessionId={}, message={}", sessionId, message);
    }

    /**
     * From app.
     *
     * @param message   the message
     * @param sessionId the session id
     * @throws FieldNotFound          the field not found
     * @throws IncorrectTagValue      the incorrect tag value
     * @throws UnsupportedMessageType the unsupported message type
     */
    @Override
    public void fromApp(Message message, SessionID sessionId) throws FieldNotFound, IncorrectTagValue, UnsupportedMessageType {
        LOGGER.warn("接收业务消息时调用此方法:sessionId={},message={}", sessionId, message);
        crack(message, sessionId);
    }

    /**
     * Ge logon sign string.
     *
     * @param message the message
     * @return the string
     * @throws Exception the exception
     */
    private String geLogonSign(Logon message) throws Exception {
        Map<String, String> map = Maps.newHashMap();
        map.put(MsgType.class.getSimpleName(), message.getHeader().getString(MsgType.FIELD));
        map.put(MsgSeqNum.class.getSimpleName(), message.getHeader().getString(MsgSeqNum.FIELD));
        map.put(SenderCompID.class.getSimpleName(), message.getHeader().getString(SenderCompID.FIELD));
        map.put(TargetCompID.class.getSimpleName(), message.getHeader().getString(TargetCompID.FIELD));
        map.put(SendingTime.class.getSimpleName(), message.getHeader().getString(SendingTime.FIELD));
        map.put("$secretKey", "d741bc45-d53a-4343-b97b-0f5f179ce8fe");
        String splicer = ",";
        SortedSet<String> sortedSet = new TreeSet(map.keySet());
        StringBuilder sortedStr = new StringBuilder();
        sortedSet.forEach(item -> sortedStr.append(map.get(item)).append(splicer));
        if (sortedStr.lastIndexOf(splicer) > 0) {
            sortedStr.deleteCharAt(sortedStr.length() - 1);
        }

        String sign = Md5Util.getMD5(sortedStr.toString());
        LOGGER.warn("sorted string=" + sortedStr.toString());
        return sign;
    }

    /**
     * Is message of type boolean.
     *
     * @param message the message
     * @param type    the type
     * @return the boolean
     */
    private boolean isMessageOfType(Message message, String type) {
        try {
            return type.equals(message.getHeader().getField(new MsgType()).getValue());
        } catch (FieldNotFound e) {
            return false;
        }
    }

    /**
     * On message.
     *
     * @param message   the message
     * @param sessionID the session id
     */
    @Override
    protected void onMessage(Message message, SessionID sessionID) {
        try {
            String msgType = message.getHeader().getString(35);
            LOGGER.info("客户端接收到用户信息订阅msgType={}", msgType);

        } catch (FieldNotFound e) {
            e.printStackTrace();
        }
    }

    /**
     * On heart beat.
     *
     * @param message   the message
     * @param sessionID the session id
     */
    @Handler
    public void onHeartBeat(Heartbeat message, SessionID sessionID) {
        LOGGER.warn("heartbeat message,sessionId={}, message:{}", sessionID, message);
    }

    /**
     * On log out message.
     *
     * @param message   the message
     * @param sessionID the session id
     */
    @Handler
    public void onLogOutMessage(Logout message, SessionID sessionID) {
        LOGGER.warn("logout message,sessionId={}, message:{}", sessionID, message);
    }

    /**
     * On logon message.
     *
     * @param message   the message
     * @param sessionID the session id
     */
    @Handler
    public void onLogonMessage(Logon message, SessionID sessionID) {
        LOGGER.info("logon message,sessionId={}, message:{}", sessionID, message);
    }

    /**
     * On order cancel message.
     *
     * @param message   the message
     * @param sessionId the session id
     * @throws FieldNotFound the field not found
     */
    @Handler
    public void onOrderCancelMessage(OrderCancelRequest message, SessionID sessionId) throws FieldNotFound {
        String orderId = message.getString(OrderID.FIELD);
        LOGGER.warn("receive order cancel msg, orderId={}", orderId);
    }

    /**
     * On execution report message.
     *
     * @param message   the message
     * @param sessionID the session id
     * @throws FieldNotFound the field not found
     */
    @Handler
    public void onExecutionReportMessage(ExecutionReport message, SessionID sessionID) throws FieldNotFound {
        String orderId = message.getString(OrderID.FIELD);
        String cOrdId = message.getString(ClOrdID.FIELD);
        char ordStatus = message.getChar(OrdStatus.FIELD);
        String msg = StringUtils.EMPTY;
        if (message.isSetField(Text.FIELD)) {
            msg = message.getString(Text.FIELD);
        }

        LOGGER.warn("receive order execution report msg, orderId={},msg={}", orderId, msg);
        switch (ordStatus) {
            case OrdStatus.NEW:
                LOGGER.warn("create new order:{}", orderId);
                break;
            case OrdStatus.CANCELED:
                LOGGER.warn("cancel order:{},msg={}", orderId, msg);
                break;
        }
    }

    @Handler
    public void onOrderCancelRejectMessage(OrderCancelReject message, SessionID sessionID) throws FieldNotFound {
        LOGGER.warn("receive order cancel reject message,orderId={},msg={},message={}", message.getOrderID().getValue(), message.getText().getValue(), message);
    }

    /**
     * On reject message.
     *
     * @param message   the message
     * @param sessionID the session id
     * @throws FieldNotFound the field not found
     */
    @Handler
    public void onRejectMessage(Reject message, SessionID sessionID) throws FieldNotFound {
        String msgType = message.getRefMsgType().getValue();
        LOGGER.warn("receive reject msg={}, msgType={}", message.getText().getValue(), msgType);
    }

    /**
     * On market date request reject message.
     *
     * @param message   the message
     * @param sessionID the session id
     * @throws FieldNotFound the field not found
     */
    @Handler
    public void onMarketDateRequestRejectMessage(MarketDataRequestReject message, SessionID sessionID) throws FieldNotFound {
        LOGGER.warn("receive market data reuquest reject:reqId={}, message:{}", message.getMDReqID().getValue(), message.toString());
    }

    /**
     * On market data snapshot full refresh message.
     *
     * @param message   the message
     * @param sessionID the session id
     * @throws FieldNotFound the field not found
     */
    @Handler
    public void onMarketDataSnapshotFullRefreshMessage(MarketDataSnapshotFullRefresh message, SessionID sessionID) throws FieldNotFound {
        LOGGER.warn("receive market data snapshot response:reqId={}, message:{}", message.getMDReqID().getValue(), message.toString());
    }
}