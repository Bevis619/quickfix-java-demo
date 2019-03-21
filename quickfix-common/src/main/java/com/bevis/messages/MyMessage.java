package com.bevis.messages;

import com.bevis.fields.MyPriceField;
import com.bevis.fields.MyStringField;
import quickfix.field.MsgType;
import quickfix.fix44.Message;

/**
 * The type My message.
 *
 * @author yanghuadong
 * @date 2019 -03-20
 */
public class MyMessage extends Message {
    /**
     * The constant MSGTYPE.
     */
    public static final String MSGTYPE = "MYG";
    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = 7922839466982400795L;

    /**
     * Instantiates a new My message.
     */
    public MyMessage() {
        super();
        getHeader().setField(new MsgType(MSGTYPE));
    }

    public void set(MyStringField field){
        setField(field);
    }

    public void set(MyPriceField field){
        setField(field);
    }
}