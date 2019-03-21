package com.bevis.fields;

import quickfix.DoubleField;

/**
 * The type My price field.
 *
 * @author yanghuadong
 * @date 2019 -03-20
 */
public class MyPriceField extends DoubleField {
    public static final int FIELD = 5001;
    /**
     * Instantiates a new My price field.
     */
    public MyPriceField() {
        super(FIELD);
    }

    /**
     * Instantiates a new My price field.
     *
     * @param data the data
     */
    public MyPriceField(double data) {
        super(FIELD, data);
    }
}
