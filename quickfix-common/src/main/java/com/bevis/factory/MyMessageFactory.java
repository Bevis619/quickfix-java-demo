package com.bevis.factory;

import com.bevis.messages.MyMessage;
import quickfix.Group;
import quickfix.Message;
import quickfix.fix44.MessageFactory;

/**
 * The type My message factory.
 *
 * @author yanghuadong
 * @date 2019-03-20
 */
public class MyMessageFactory extends MessageFactory {
    /**
     * Create message.
     *
     * @param beginString the begin string
     * @param msgType     the msg type
     * @return the message
     */
    @Override
    public Message create(String beginString, String msgType) {
        switch (msgType) {
            case MyMessage.MSGTYPE:
                return new MyMessage();
            default:
                return super.create(beginString, msgType);
        }
    }

    /**
     * Create group.
     *
     * @param beginString          the begin string
     * @param msgType              the msg type
     * @param correspondingFieldID the corresponding field id
     * @return the group
     */
    @Override
    public Group create(String beginString, String msgType, int correspondingFieldID) {
        return super.create(beginString, msgType, correspondingFieldID);
    }
}