package com.bevis.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * The type New order vo.
 *
 * @author yanghuadong
 * @date 2019 -04-09
 */
@Data
public class NewOrderVO implements Serializable {
    /**
     * 客户端自定义的订单ID(不能重复)
     */
    private String clOrdID;

    /**
     * 交易对
     */
    private String symbol;

    /**
     * 成交限价(限价单专用，市价单请传入0)
     */
    private BigDecimal price;

    /**
     * 买卖类型( BUY(1)=买单，SELL(2)=卖单 )
     */
    private Character side;

    /**
     * 委托单类型( MARKET(1)=市价，LIMIT(2)=限价 )
     */
    private Character ordType;

    /**
     * 标的币数量(限价买，限价卖，市价卖时使用。市价买请填0)
     */
    private BigDecimal orderQty;

    /**
     * 计价币数量(市价买时使用。限价买，限价卖，市价卖时请填0)
     */
    private BigDecimal cashOrderQty;
}