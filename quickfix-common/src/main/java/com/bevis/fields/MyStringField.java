package com.bevis.fields;

import quickfix.StringField;

/**
 * The type My string.
 *
 * @author yanghuadong
 * @date 2019 -03-20
 */
public class MyStringField extends StringField {

    public static final int FIELD = 5000;

    /**
     * Instantiates a new My string.
     */
    public MyStringField() {
        super(FIELD);
    }


    /**
     * Instantiates a new My string.
     *
     * @param data the data
     */
    public MyStringField(String data) {
        super(FIELD, data);
    }
}
