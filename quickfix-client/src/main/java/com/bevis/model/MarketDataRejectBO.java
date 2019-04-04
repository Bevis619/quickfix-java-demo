package com.bevis.model;

import lombok.Data;
import quickfix.FieldNotFound;
import quickfix.field.Text;
import quickfix.fix44.MarketDataRequestReject;

import java.io.Serializable;

/**
 * The type Market data reject bo.
 *
 * @author yanghuadong
 * @date 2019 -04-04
 */
@Data
public class MarketDataRejectBO implements Serializable {
    /**
     * The Md req id.
     */
    private String mdReqID;

    /**
     * The Md req rej reason.
     */
    private Character mdReqRejReason;

    /**
     * The Text.
     */
    private String text;

    /**
     * Instantiates a new Market data reject bo.
     *
     * @param message the message
     * @throws FieldNotFound the field not found
     */
    public MarketDataRejectBO(MarketDataRequestReject message) throws FieldNotFound {
        this.mdReqID = message.getMDReqID().getValue();
        this.mdReqRejReason = message.getMDReqRejReason().getValue();
        if (message.isSetField(Text.FIELD)) {
            this.text = message.getText().getValue();
        }
    }
}