package com.bevis.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import quickfix.FieldNotFound;
import quickfix.field.*;
import quickfix.fix44.Message;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * The type Header bo.
 *
 * @author yanghuadong
 * @date 2019 -03-18
 */
@Data
public class HeaderBO implements Serializable {
    /**
     * The Msg type.
     */
    protected String msgType;

    /**
     * The Msg seq num.
     */
    protected Integer msgSeqNum;

    /**
     * The Sender comp id.
     */
    protected String senderCompID;

    /**
     * The Target comp id.
     */
    protected String targetCompID;

    /**
     * The Sending time.
     */
    @JSONField(format = "yyyyMMdd-HH:mm:ss.SSS")
    protected LocalDateTime sendingTime;

    /**
     * Instantiates a new Header bo.
     *
     * @param message the message
     * @throws FieldNotFound the field not found
     */
    public HeaderBO(Message message) throws FieldNotFound {
        this.msgType = message.getHeader().getString(MsgType.FIELD);
        this.msgSeqNum = message.getHeader().getInt(MsgSeqNum.FIELD);
        this.senderCompID = message.getHeader().getString(SenderCompID.FIELD);
        this.targetCompID = message.getHeader().getString(TargetCompID.FIELD);
        if (message.getHeader().isSetField(SendingTime.FIELD)) {
            this.sendingTime = message.getHeader().getUtcTimeStamp(SendingTime.FIELD);
        }
    }
}