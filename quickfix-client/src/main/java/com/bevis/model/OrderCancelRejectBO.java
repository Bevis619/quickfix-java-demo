package com.bevis.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import quickfix.FieldNotFound;
import quickfix.field.*;
import quickfix.fix44.OrderCancelReject;

import java.io.Serializable;

/**
 * The type Order cancel reject bo.
 *
 * @author yanghuadong
 * @date 2019 -04-04
 */
@Data
public class OrderCancelRejectBO implements Serializable {
    /**
     * The Cl ord id.
     */
    private String clOrdID;
    /**
     * The Order id.
     */
    private String orderID;
    /**
     * The Orig cl ord id.
     */
    private String origClOrdID;
    /**
     * The Ord status.
     */
    private Character ordStatus;
    /**
     * The Cxl rej response to.
     */
    private Character cxlRejResponseTo;
    /**
     * The Cxl rej reason.
     */
    private Integer cxlRejReason;
    /**
     * The Text.
     */
    private String text;

    /**
     * Instantiates a new Order cancel reject bo.
     *
     * @param message the message
     */
    public OrderCancelRejectBO(OrderCancelReject message) throws FieldNotFound {
        this.clOrdID = message.getString(ClOrdID.FIELD);
        this.orderID = message.getString(OrderID.FIELD);
        this.origClOrdID = message.getString(OrigClOrdID.FIELD);
        this.ordStatus = message.getChar(OrdStatus.FIELD);
        this.cxlRejResponseTo = message.getChar(CxlRejResponseTo.FIELD);
        this.cxlRejReason = message.getInt(CxlRejReason.FIELD);
        this.text = StringUtils.EMPTY;
        if (message.isSetField(Text.FIELD)) {
            this.text = message.getString(Text.FIELD);
        }
    }
}