package com.bevis.model;

import com.google.common.collect.Lists;
import lombok.Data;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.field.*;
import quickfix.fix44.ListStatus;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The type List status bo.
 *
 * @author yanghuadong
 * @date 2019 -04-04
 */
@Data
public class ListStatusBO implements Serializable {
    /**
     * The List id.
     */
    private String listID;
    /**
     * The List status type.
     */
    private Character listStatusType;
    /**
     * The No rpts.
     */
    private Integer noRpts;
    /**
     * The List order status.
     */
    private Character listOrderStatus;
    /**
     * The Rpt seq.
     */
    private Integer rptSeq;
    /**
     * The Tot no orders.
     */
    private Integer totNoOrders;

    /**
     * The No orders.
     */
    private List<NoOrder> noOrders;

    /**
     * The Transact time.
     */
    private LocalDateTime transactTime;

    /**
     * Instantiates a new List status bo.
     *
     * @param message the message
     * @throws FieldNotFound the field not found
     */
    public ListStatusBO(ListStatus message) throws FieldNotFound {
        this.listID = message.getString(ListID.FIELD);
        this.listStatusType = message.getChar(ListStatusType.FIELD);
        this.noRpts = message.getInt(NoRpts.FIELD);
        this.listOrderStatus = message.getChar(ListOrderStatus.FIELD);
        this.rptSeq = message.getInt(RptSeq.FIELD);
        this.totNoOrders = message.getInt(TotNoOrders.FIELD);
        this.transactTime = message.getUtcTimeStamp(TransactTime.FIELD);
        this.noOrders = Lists.newArrayList();
        List<Group> orders = message.getGroups(NoOrders.FIELD);
        NoOrder order;
        for (int i = 0; i < orders.size(); i++) {
            order = new NoOrder(orders.get(i));
            this.noOrders.add(order);
        }
    }

    /**
     * The type No order.
     */
    @Data
    public static class NoOrder implements Serializable {
        /**
         * The Cl ord id.
         */
        private String clOrdID;
        /**
         * The Cum qty.
         */
        private Double cumQty;
        /**
         * The Ord status.
         */
        private Character ordStatus;
        /**
         * The Leaves qty.
         */
        private Double leavesQty;
        /**
         * The Cum qty.
         */
        private Double cxlQty;
        /**
         * The Avg px.
         */
        private Double avgPx;
        /**
         * The Text.
         */
        private String text;

        /**
         * Instantiates a new No order.
         *
         * @param group the group
         */
        public NoOrder(Group group) throws FieldNotFound {
            this.clOrdID = group.getString(ClOrdID.FIELD);
            this.cumQty = group.getDouble(CumQty.FIELD);
            this.ordStatus = group.getChar(OrdStatus.FIELD);
            this.leavesQty = group.getDouble(LeavesQty.FIELD);
            this.cxlQty = group.getDouble(CxlQty.FIELD);
            this.avgPx = group.getDouble(AvgPx.FIELD);
            if (group.isSetField(Text.FIELD)) {
                this.text = group.getString(Text.FIELD);
            }
        }
    }
}