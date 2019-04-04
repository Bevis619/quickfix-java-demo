package com.bevis.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import quickfix.FieldNotFound;
import quickfix.field.*;
import quickfix.fix44.ExecutionReport;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * The type Execution report bo.
 *
 * @author yanghuadong
 * @date 2019 -04-04
 */
@Data
public class ExecutionReportBO implements Serializable {
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
     * The Side.
     */
    private Character side;
    /**
     * The Symbol.
     */
    private String symbol;
    /**
     * The Transact time.
     */
    private LocalDateTime transactTime;
    /**
     * The Exec type.
     */
    private Character execType;
    /**
     * The Exec id.
     */
    private String execID;
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
    private Double cumQty;
    /**
     * The Avg px.
     */
    private Double avgPx;
    /**
     * The Text.
     */
    private String text;

    /**
     * Instantiates a new Execution report bo.
     *
     * @param message the message
     * @throws FieldNotFound the field not found
     */
    public ExecutionReportBO(ExecutionReport message) throws FieldNotFound {
        this.clOrdID = message.getString(ClOrdID.FIELD);
        this.orderID = message.getString(OrderID.FIELD);
        this.origClOrdID = message.getString(OrigClOrdID.FIELD);
        this.side = message.getChar(Side.FIELD);
        this.symbol = message.getString(Symbol.FIELD);
        this.transactTime = message.getUtcTimeStamp(TransactTime.FIELD);
        this.execType = message.getChar(ExecType.FIELD);
        this.execID = message.getString(ExecID.FIELD);
        this.ordStatus = message.getChar(OrdStatus.FIELD);
        this.leavesQty = message.getDouble(LeavesQty.FIELD);
        this.cumQty = message.getDouble(CumQty.FIELD);
        this.avgPx = message.getDouble(AvgPx.FIELD);
        this.text = StringUtils.EMPTY;
        if (message.isSetField(Text.FIELD)) {
            this.text = message.getString(Text.FIELD);
        }
    }
}
