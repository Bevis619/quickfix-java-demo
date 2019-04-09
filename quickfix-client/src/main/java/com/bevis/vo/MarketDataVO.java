package com.bevis.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author yanghuadong
 * @date 2019-04-09
 */
@Data
public class MarketDataVO implements Serializable {
    /**
     * Unique identifier for request.
     * 请求编号
     */
    private String mdReqID;

    /**
     * Subscription Request Type.
     * "0" = Snapshot
     * "1" = Snapshot + Updates (Subscribe)
     * "2" = Disable previous Snapshot + Update Request (Unsubscribe)
     * <p>
     * 请求类型：只支持 "0" 和 "2"
     */
    private Character subscriptionRequestType;

    /**
     * The Depth of market for Book Snapshot.
     * 0 = Full book, unlimited depth
     * 1 = Top of Book only
     * N = Number of layers to deliver
     * 交易软件中 Buy 和 Sell的没有成交的不同价格的档数
     */
    private Integer marketDepth;

    /**
     * 行情数据类型集合.
     */
    private List<NoMdEntryType> noMdEntryTypes;

    /**
     * 其它详细信息.
     */
    private List<NoRelatedSymbol> noRelatedSymbols;

    /**
     * Gets symbol.
     *
     * @return the symbol
     */
    public String getSymbol() {
        if (CollectionUtils.isEmpty(noRelatedSymbols)) {
            return StringUtils.EMPTY;
        }

        return noRelatedSymbols.get(0).getSymbol();
    }

    /**
     * Number of MDEntryType <269> fields requested.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NoMdEntryType implements Serializable {
        /**
         * Type Market Data entry.
         * 0 = Bid  投标：买入价
         * 1 = Offer 报盘:也叫卖价
         * 2 = Trade
         * 3 = Index Value  指数值
         * 4 = Opening Price <44> 开盘价
         * 5 = Closing Price <44> 收盘价
         * 6 = Settlement Price <44> 结算价
         * 7 = Trading Session High Price <44> 交易日高价
         * 8 = Trading Session Low Price <44> 交易日低价
         * 9 = Trading Session VWAP Price <44> 交易日成交量加权平均价
         * A = Imbalance
         * B = Trade Volume 交易量
         * C = Open Interest 未平仓合约
         * more ...
         * 行情数据类型
         *
         * @link quickfix.CharField.MDEntryType
         */
        private Character mdEntryType;
    }

    /**
     * Number of symbols (instruments) requested.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NoRelatedSymbol implements Serializable {
        /**
         * The Symbol.
         * 交易对
         */
        private String symbol;
    }
}