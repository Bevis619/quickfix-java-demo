package com.bevis.core;

import com.bevis.enums.BizExceptionCodeEnum;
import com.bevis.exceptions.BizException;
import com.bevis.messages.MyMessage;
import com.bevis.model.FixUserBO;
import com.bevis.model.LogonBO;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import quickfix.*;
import quickfix.Message;
import quickfix.MessageCracker;
import quickfix.field.*;
import quickfix.fix44.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * The type Fix acceptor.
 *
 * @author yanghuadong
 * @date 2019 -03-18
 */
@Component
@Slf4j
public class FixAcceptor extends MessageCracker implements Application {

    /**
     * The Fix api user map.
     * key：accesskey   value:secretKey
     */
    private static Map<String, FixUserBO> FIX_API_USER_MAP = Maps.newHashMap();

    static {
        FIX_API_USER_MAP.put("7d8f8655-ce10-428d-b10a-b9dcc25b352d",
                new FixUserBO("7d8f8655-ce10-428d-b10a-b9dcc25b352d", "d741bc45-d53a-4343-b97b-0f5f179ce8fe", "U001", true));
    }

    /**
     * On logon message.
     *
     * @param message   the message
     * @param sessionID the session id
     */
    @Handler
    public void onLogonMessage(Logon message, SessionID sessionID) {
        LOGGER.info("receive logon message,sessionId={}, message:{}", sessionID, message);
        Session session = Session.lookupSession(sessionID);
        String accessKey = sessionID.getTargetCompID();
        try {
            // 1.判断accessKey是否合法，模拟从db查询
            LogonBO bo = new LogonBO(message);
            FixUserBO fixUserBO = FIX_API_USER_MAP.get(accessKey);
            if (null == fixUserBO) {
                throw new BizException(BizExceptionCodeEnum.API_FIX_USER_NOT_EXIST);
            }

            if (!fixUserBO.getIsValid()) {
                throw new BizException(BizExceptionCodeEnum.API_FIX_USER_NO_PERMISSIONS);
            }

            // 2.验签签名
            String sign = bo.getSign(fixUserBO.getSecretkey());
            boolean result = StringUtils.equals(sign, bo.getRawData());
            if (!result) {
                throw new BizException(BizExceptionCodeEnum.API_FIX_USER_LOGON_SIGN_ERROR);
            }

            // 3.允许登陆
            session.logon();
            session.sentLogon();
        } catch (FieldNotFound fieldNotFound) {
            LOGGER.error("logon message error:" + fieldNotFound.getMessage(), fieldNotFound);
            session.logout(fieldNotFound.getMessage());
            session.sentLogout();
        } catch (BizException e) {
            LOGGER.error(String.format("fix logon failed:accesskey=%s,msg=%s", accessKey, e.getDesc()), e);
            session.logout(String.valueOf(e.getDesc()));
            session.sentLogout();
        } catch (Exception e) {
            LOGGER.error(String.format("fix logon error:accesskey=%s,msg=%s", accessKey, e.getMessage()), e);
            session.logout(String.valueOf(BizExceptionCodeEnum.SYS_ERROR.getMsg()));
            session.sentLogout();
        } finally {
            LOGGER.info("fix logon:accesskey={},message={},sessionId={}", accessKey, message, sessionID);
        }
    }

    /**
     * On my message.  接收自定义消息
     *
     * @param message   the message
     * @param sessionID the session id
     */
    @Handler
    public void onMyMessage(MyMessage message, SessionID sessionID) {
        LOGGER.warn("receive my diy message,sessionId={}, message:{}", sessionID, message);
    }

    /**
     * On new order message.
     *
     * @param message   the message
     * @param sessionID the session id
     * @throws FieldNotFound the field not found
     */
    @Handler
    public void onNewOrderMessage(NewOrderSingle message, SessionID sessionID) throws FieldNotFound {
        LOGGER.warn("receive new order:{}", message.toString());
        // 提取字段
        String clOrdId = message.getClOrdID().getValue();
        String symbol = message.getSymbol().getValue();
        // TODO 具体下单逻辑

        // 发送响应给客户端
        ExecutionReport report = new ExecutionReport();
        report.set(new ClOrdID(clOrdId));
        report.set(new OrderID(UUID.randomUUID().toString()));
        report.set(new ExecType(ExecType.NEW));
        report.set(new OrdStatus(OrdStatus.NEW));
        report.set(new TransactTime(LocalDateTime.now()));
        report.set(new ExecID("dddd"));
        report.set(new Side(Side.BUY));
        report.set(new LeavesQty(1));
        report.set(new CumQty(1));
        report.set(new AvgPx(1.00D));
        report.set(new Symbol(symbol));
        report.set(new Text("jjj"));

        try {
            boolean result = Session.sendToTarget(report, sessionID);
            LOGGER.warn("send new order res message:{}", result);
        } catch (SessionNotFound sessionNotFound) {
            LOGGER.error(sessionNotFound.getMessage(), sessionNotFound);
        }
    }

    /**
     * On cancel order message.
     *
     * @param message   the message
     * @param sessionID the session id
     */
    @Handler
    public void onCancelOrderMessage(OrderCancelRequest message, SessionID sessionID) {
        LOGGER.warn("receive new cancel order message:{}", message.toString());
    }

    /**
     * On order state message.
     *
     * @param message   the message
     * @param sessionID the session id
     * @throws FieldNotFound the field not found
     */
    @Handler
    public void onOrderStateMessage(OrderStatusRequest message, SessionID sessionID) throws FieldNotFound {
        LOGGER.warn("receive new query order state:OrderID={}, message:{}", message.getOrderID().getValue(), message.toString());
    }

    /**
     * On order list state message.
     *
     * @param message   the message
     * @param sessionID the session id
     * @throws FieldNotFound the field not found
     */
    @Handler
    public void onOrderListStateMessage(ListStatusRequest message, SessionID sessionID) throws FieldNotFound {
        LOGGER.warn("receive new order list:OrderIDs={}, message:{}", message.getListID().getValue(), message.toString());
    }

    /**
     * On market request message.
     *
     * @param message   the message
     * @param sessionID the session id
     * @throws FieldNotFound the field not found
     */
    @Handler
    public void onMarketRequestMessage(MarketDataRequest message, SessionID sessionID) throws FieldNotFound, SessionNotFound {
        LOGGER.warn("receive market data:reqId={}, message:{}", message.getMDReqID().getValue(), message.toString());
        Character subType = message.getSubscriptionRequestType().getValue();
        Integer depth = message.getMarketDepth().getValue();
        int noMDEntryField = new MarketDataRequest.NoMDEntryTypes().getFieldTag();
        List<Group> groups = message.getGroups(noMDEntryField);

        int noRelatedField = new MarketDataRequest.NoRelatedSym().getFieldTag();
        List<Group> groups1 = message.getGroups(noRelatedField);

        if (subType.compareTo(SubscriptionRequestType.SNAPSHOT) != 0) {
            MarketDataRequestReject reject = new MarketDataRequestReject();
            reject.set(new MDReqID(message.getMDReqID().getValue()));
            reject.set(new MDReqRejReason(MDReqRejReason.UNSUPPORTED_SUBSCRIPTIONREQUESTTYPE));
            reject.set(new Text("UNSUPPORTED SUBSCRIPTION REQUEST TYPE"));
            Boolean result = Session.sendToTarget(reject, sessionID);
            LOGGER.info("send market data request reject message:result={},reject={}", result, reject);
        } else {
            MarketDataSnapshotFullRefresh response = new MarketDataSnapshotFullRefresh();
            response.set(new MDReqID(message.getMDReqID().getValue()));
            response.set(new Symbol(groups1.get(0).getString(Symbol.FIELD)));
            MarketDataSnapshotFullRefresh.NoMDEntries mdEntries = new MarketDataSnapshotFullRefresh.NoMDEntries();
            mdEntries.set(new MDEntryType(MDEntryType.OFFER));
            mdEntries.set(new MDEntryPx(0.000000568));
            mdEntries.set(new MDEntrySize(999));
            mdEntries.set(new MDEntryPositionNo(1));
            response.addGroup(mdEntries);

            MarketDataSnapshotFullRefresh.NoMDEntries mdEntries1 = new MarketDataSnapshotFullRefresh.NoMDEntries();
            mdEntries1.set(new MDEntryType(MDEntryType.OFFER));
            mdEntries1.set(new MDEntryPx(0.000000578));
            mdEntries1.set(new MDEntrySize(888));
            mdEntries1.set(new MDEntryPositionNo(2));
            response.addGroup(mdEntries1);

            Boolean result = Session.sendToTarget(response, sessionID);
            LOGGER.info("send market data snapshot response message:result={},response={}", result, response);
        }
    }

    @Handler
    public void onTestMessage(ListStatus message, SessionID sessionID) {
        LOGGER.warn("test message:{}", message);
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
            LOGGER.info("服务器接收到用户信息订阅msgType={}", msgType);
        } catch (FieldNotFound e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    /**
     * On logout message.
     *
     * @param message   the message
     * @param sessionID the session id
     */
    @Handler
    public void onLogoutMessage(Logout message, SessionID sessionID) {
        LOGGER.warn("receive logout message,sessionId={}, message:{}", sessionID, message);
    }

    /**
     * On heart beat.
     *
     * @param message   the message
     * @param sessionID the session id
     */
    @Handler
    public void onHeartBeatMessage(Heartbeat message, SessionID sessionID) {
        LOGGER.info("receive heartbeat message,sessionId={}, message:{}", sessionID, message);
    }

    /**
     * On create.
     *
     * @param sessionId the session id
     */
    @Override
    public void onCreate(SessionID sessionId) {
        LOGGER.info("a new session has been created:{}", sessionId);
    }

    /**
     * On logon.
     *
     * @param sessionId the session id
     */
    @Override
    public void onLogon(SessionID sessionId) {
        LOGGER.info("a valid logon has been established:{}", sessionId);
    }

    /**
     * On logout.
     *
     * @param sessionId the session id
     */
    @Override
    public void onLogout(SessionID sessionId) {
        LOGGER.info("an fix session is no longer online:{}", sessionId);
        try {
            Session.lookupSession(sessionId).reset();
        } catch (IOException e) {
            e.printStackTrace();
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
    }

    /**
     * From admin.
     *
     * @param message   the message
     * @param sessionId the session id
     * @throws FieldNotFound       the field not found
     * @throws IncorrectDataFormat the incorrect data format
     * @throws IncorrectTagValue   the incorrect tag value
     * @throws RejectLogon         the reject logon
     */
    @Override
    public void fromAdmin(Message message, SessionID sessionId) {
        String log = String.format(",sessionId=%s, message=%s", sessionId, message);
        try {
            crack(message, sessionId);
        } catch (FieldNotFound fieldNotFound) {
            LOGGER.error("session message error:" + fieldNotFound.getMessage() + log, fieldNotFound);
        } catch (UnsupportedMessageType unsupportedMessageType) {
            LOGGER.error("session message error: unsupported message type" + log, unsupportedMessageType);
        } catch (IncorrectTagValue incorrectTagValue) {
            LOGGER.error("session message error:" + incorrectTagValue.getMessage() + log, incorrectTagValue);
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
        LOGGER.warn("send an application message: sessionId={}, message={}", sessionId, message);
    }

    /**
     * From app.
     *
     * @param message   the message
     * @param sessionId the session id
     * @throws FieldNotFound          the field not found
     * @throws IncorrectDataFormat    the incorrect data format
     * @throws IncorrectTagValue      the incorrect tag value
     * @throws UnsupportedMessageType the unsupported message type
     */
    @Override
    public void fromApp(Message message, SessionID sessionId) {
        String log = String.format(",sessionId=%s, message=%s", sessionId, message);
        String accessKey = StringUtils.EMPTY;
        try {
            //1.校验
            accessKey = message.getHeader().getString(SenderCompID.FIELD);
            FixUserBO fixUserBO = FIX_API_USER_MAP.get(accessKey);
            if (null == fixUserBO) {
                throw new BizException(BizExceptionCodeEnum.API_FIX_USER_NOT_EXIST);
            }

            if (!fixUserBO.getIsValid()) {
                throw new BizException(BizExceptionCodeEnum.API_FIX_USER_NO_PERMISSIONS);
            }

            //2.设置userNO到header
            message.getHeader().setField(new SenderSubID(String.valueOf(fixUserBO.getUserNo())));
            // 3.业务操作
            crack(message, sessionId);
        } catch (BizException e) {
            Session session = Session.lookupSession(sessionId);
            LOGGER.error(String.format("fix user valid failed:accesskey=%s,msg=%s", accessKey, e.getDesc()), e);
            session.logout(String.valueOf(e.getDesc()));
            session.sentLogout();
        } catch (UnsupportedMessageType unsupportedMessageType) {
            LOGGER.error("application message error: unsupported message type" + log, unsupportedMessageType);
        } catch (FieldNotFound fieldNotFound) {
            LOGGER.error("application message error:" + fieldNotFound.getMessage() + log, fieldNotFound);
        } catch (IncorrectTagValue incorrectTagValue) {
            LOGGER.error("application message error:" + incorrectTagValue.getMessage() + log, incorrectTagValue);
        }
    }
}