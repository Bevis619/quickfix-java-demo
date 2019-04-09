package com.bevis.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * The type Cancel order vo.
 *
 * @author yanghuadong
 * @date 2019 -04-09
 */
@Data
public class CancelOrderVO implements Serializable {
    /**
     * The Order id.
     * 服务端订单号
     */
    private String orderID;

    /**
     * Unique identifier of replacement order as assigned by institution or by the intermediary with closest association with the investor..
     * 客户端订单号
     */
    private String clOrdID;

    /**
     * The previous non rejected order (NOT the initial order of the day) when canceling or replacing an order..
     */
    private String origClOrdID;

    /**
     * The Symbol.
     * 交易对
     */
    private String symbol;

    /**
     * The Side.
     * "1"-买入  “2" 卖出
     */
    private Character side;
}