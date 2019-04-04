package com.bevis.controller;

import com.bevis.core.FixClient;
import com.bevis.fields.MyPriceField;
import com.bevis.fields.MyStringField;
import com.bevis.messages.MyMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.field.*;
import quickfix.fix44.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * The type Client controller.
 *
 * @author yanghuadong
 * @date 2019 -03-06
 */
@Validated
@RestController
@RequestMapping("/order")
@Slf4j
public class ClientController {

    /**
     * The Fix client.
     */
    @Autowired
    private FixClient fixClient;

    /**
     * 发送登陆消息.
     * @see <a href="http://localhost:9092/order/logon">按住ctrl+鼠标左键发送请求</a>
     * @return the boolean
     * @throws SessionNotFound the session not found
     */
    @GetMapping("/logon")
    public Boolean logon() {
        SessionID sessionID = fixClient.sessionIds().get(0);
        Session session = Session.lookupSession(sessionID);
        session.logon();
        return true;
    }

    /**
     * 发送注销消息.
     * @see <a href="http://localhost:9092/order/logout">按住ctrl+鼠标左键发送请求</a>
     * @return the boolean
     */
    @GetMapping("/logout")
    public Boolean logout() {
        SessionID sessionID = fixClient.sessionIds().get(0);
        Session session = Session.lookupSession(sessionID);
        session.logout("I miss U");
        return true;
    }

    /**
     * 下单委托消息.
     * @see <a href="http://localhost:9092/order/new">按住ctrl+鼠标左键发送请求</a>
     * @return the string
     * @throws SessionNotFound the session not found
     */
    @GetMapping("/new")
    public Boolean newOrder() throws SessionNotFound {
        SessionID sessionID = fixClient.sessionIds().get(0);
        // 6300 USD限价买入0.1 BTC
        NewOrderSingle order = new NewOrderSingle();
        order.set(new ClOrdID(UUID.randomUUID().toString()));
        order.set(new Symbol("BTC/USD"));
        order.set(new Price(6300));
        order.set(new Side(Side.BUY));
        order.set(new OrdType(OrdType.LIMIT));
        order.set(new OrderQty(0.1));
        order.set(new CashOrderQty(0));
        order.set(new TransactTime(LocalDateTime.now()));
        boolean result = Session.sendToTarget(order, sessionID);
        return result;
    }

    /**
     * 撤单委托消息.
     * @see <a href="http://localhost:9092/order/cancel">按住ctrl+鼠标左键发送请求</a>
     * @return the boolean
     * @throws SessionNotFound the session not found
     */
    @GetMapping("/cancel")
    public Boolean cancelOrder() throws SessionNotFound {
        SessionID sessionID = fixClient.sessionIds().get(0);
        OrderCancelRequest request = new OrderCancelRequest();
        request.set(new ClOrdID(UUID.randomUUID().toString()));
        request.set(new OrderID("123"));
        request.set(new OrigClOrdID("XXX"));
        request.set(new Side(Side.BUY));
        request.set(new Symbol("BTC/USD"));
        request.set(new TransactTime(LocalDateTime.now()));
        boolean result = Session.sendToTarget(request, sessionID);
        return result;
    }

    /**
     * 查询未完成订单消息.
     * @see <a href="http://localhost:9092/order/list/status">按住ctrl+鼠标左键发送请求</a>
     * @return the boolean
     * @throws SessionNotFound the session not found
     */
    @GetMapping("/list/status")
    public Boolean queryOrderListStatus() throws SessionNotFound {
        ListStatusRequest request = new ListStatusRequest();
        SessionID sessionID = fixClient.sessionIds().get(0);
        request.set(new ListID("*"));
        boolean result = Session.sendToTarget(request, sessionID);
        return result;
    }

    /**
     * 查询深度行情数据.
     * @see <a href="http://localhost:9092/order/depth">按住ctrl+鼠标左键发送请求</a>
     * @return the boolean
     * @throws SessionNotFound the session not found
     */
    @GetMapping("/depth")
    public Boolean sendDepthMessage() throws SessionNotFound {
        MarketDataRequest message = new MarketDataRequest();
        message.set(new MDReqID(UUID.randomUUID().toString()));
        message.set(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT));
        message.set(new MarketDepth(10));

        MarketDataRequest.NoMDEntryTypes noMDEntryTypes = new MarketDataRequest.NoMDEntryTypes();
        noMDEntryTypes.set(new MDEntryType(MDEntryType.BID));
        message.addGroup(noMDEntryTypes);
        MarketDataRequest.NoMDEntryTypes noMDEntryTypes1 = new MarketDataRequest.NoMDEntryTypes();
        noMDEntryTypes1.set(new MDEntryType(MDEntryType.OFFER));
        message.addGroup(noMDEntryTypes1);

        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol("BTC/USD"));
        message.addGroup(noRelatedSym);

        SessionID sessionID = fixClient.sessionIds().get(0);
        boolean result = Session.sendToTarget(message, sessionID);
        return result;
    }

    /**
     * 查询实时行情数据.
     * @see <a href="http://localhost:9092/order/live">按住ctrl+鼠标左键发送请求</a>
     * @return the boolean
     * @throws SessionNotFound the session not found
     */
    @GetMapping("/live")
    public Boolean sendLiveMessage() throws SessionNotFound {
        MarketDataRequest message = new MarketDataRequest();
        message.set(new MDReqID(UUID.randomUUID().toString()));
        message.set(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT));
        message.set(new MarketDepth(10));

        MarketDataRequest.NoMDEntryTypes noMDEntryTypes = new MarketDataRequest.NoMDEntryTypes();
        noMDEntryTypes.set(new MDEntryType(MDEntryType.TRADE));
        message.addGroup(noMDEntryTypes);

        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol("BTC/USD"));
        message.addGroup(noRelatedSym);

        SessionID sessionID = fixClient.sessionIds().get(0);
        boolean result = Session.sendToTarget(message, sessionID);
        return result;
    }

    /**
     * 查询实时行情数据.
     * @see <a href="http://localhost:9092/order/k">按住ctrl+鼠标左键发送请求</a>
     * @return the boolean
     * @throws SessionNotFound the session not found
     */
    @GetMapping("/k")
    public Boolean sendKMessage() throws SessionNotFound {
        MarketDataRequest message = new MarketDataRequest();
        message.set(new MDReqID(UUID.randomUUID().toString()));
        message.set(new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT));
        message.set(new MarketDepth(5));

        MarketDataRequest.NoMDEntryTypes noMDEntryTypes = new MarketDataRequest.NoMDEntryTypes();
        noMDEntryTypes.set(new MDEntryType(MDEntryType.OPENING_PRICE));
        message.addGroup(noMDEntryTypes);
        MarketDataRequest.NoMDEntryTypes noMDEntryTypes1 = new MarketDataRequest.NoMDEntryTypes();
        noMDEntryTypes1.set(new MDEntryType(MDEntryType.CLOSING_PRICE));
        message.addGroup(noMDEntryTypes1);
        MarketDataRequest.NoMDEntryTypes noMDEntryTypes2 = new MarketDataRequest.NoMDEntryTypes();
        noMDEntryTypes2.set(new MDEntryType(MDEntryType.TRADING_SESSION_HIGH_PRICE));
        message.addGroup(noMDEntryTypes2);
        MarketDataRequest.NoMDEntryTypes noMDEntryTypes3 = new MarketDataRequest.NoMDEntryTypes();
        noMDEntryTypes3.set(new MDEntryType(MDEntryType.TRADING_SESSION_LOW_PRICE));
        message.addGroup(noMDEntryTypes3);
        MarketDataRequest.NoMDEntryTypes noMDEntryTypes4 = new MarketDataRequest.NoMDEntryTypes();
        noMDEntryTypes4.set(new MDEntryType(MDEntryType.TRADE_VOLUME));
        message.addGroup(noMDEntryTypes4);

        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol("BTC/USD"));
        message.addGroup(noRelatedSym);

        SessionID sessionID = fixClient.sessionIds().get(0);
        boolean result = Session.sendToTarget(message, sessionID);
        return result;
    }

    /**
     * 发送自定义消息.
     *
     * @return the boolean
     * @throws SessionNotFound the session not found
     */
    @GetMapping("/myg")
    public Boolean sendMyMessage() throws SessionNotFound {
        MyMessage message = new MyMessage();
        message.set(new MyStringField("Hello Fix"));
        message.set(new MyPriceField(2.2));
        SessionID sessionID = fixClient.sessionIds().get(0);
        boolean result = Session.sendToTarget(message, sessionID);
        return result;
    }

    @GetMapping("/test")
    public Boolean test() throws SessionNotFound {
        ListStatus message = new ListStatus();
        message.set(new ListID("123"));
        message.set(new ListStatusType(ListStatusType.RESPONSE));
        message.set(new NoRpts(0));
        message.set(new ListOrderStatus(ListOrderStatus.ALERT));
        message.set(new RptSeq(0));
        message.set(new TotNoOrders(1));
        message.set(new ListStatusText("fdhafda"));
        ListStatus.NoOrders orders = new ListStatus.NoOrders();
        orders.set(new ClOrdID("*"));
        orders.set(new CumQty(0));
        orders.set(new OrdStatus(OrdStatus.STOPPED));
        orders.set(new LeavesQty(0));
        orders.set(new CxlQty(0));
        orders.set(new AvgPx(0));
        message.addGroup(orders);
        message.set(new TransactTime(LocalDateTime.now()));
        SessionID sessionID = fixClient.sessionIds().get(0);
        boolean result = Session.sendToTarget(message, sessionID);
        return result;
    }
}