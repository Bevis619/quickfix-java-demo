package com.bevis.model;

import com.google.common.collect.Lists;
import lombok.Data;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.field.*;
import quickfix.fix44.MarketDataSnapshotFullRefresh;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * The type Market data bo.
 *
 * @author yanghuadong
 * @date 2019 -04-04
 */
@Data
public class MarketDataBO implements Serializable {
    /**
     * The Md req id.
     */
    private String mdReqID;
    /**
     * The Symbol.
     */
    private String symbol;

    /**
     * The No md entry types.
     */
    private List<NoMDEntryType> noMDEntryTypes;

    /**
     * Instantiates a new Market data bo.
     *
     * @param message the message
     * @throws FieldNotFound the field not found
     */
    public MarketDataBO(MarketDataSnapshotFullRefresh message) throws FieldNotFound {
        this.mdReqID = message.getMDReqID().getValue();
        this.symbol = message.getSymbol().getValue();
        this.noMDEntryTypes = Lists.newArrayList();
        List<Group> groups = message.getGroups(NoMDEntryTypes.FIELD);
        NoMDEntryType item;
        for (int i = 0; i < groups.size(); i++) {
            item = new NoMDEntryType(groups.get(i));
            this.noMDEntryTypes.add(item);
        }
    }

    /**
     * The type No md entry type.
     */
    @Data
    public static class NoMDEntryType implements Serializable {
        /**
         * The Md entry type.
         */
        private Character mdEntryType;
        /**
         * The Md entry px.
         */
        private Double mdEntryPx;
        /**
         * The Md entry size.
         */
        private Integer mdEntrySize;
        /**
         * The Md entry position no.
         */
        private Integer mdEntryPositionNo;
        /**
         * The Md entry date.
         */
        private LocalDate mdEntryDate;
        /**
         * The Md entry time.
         */
        private LocalTime mdEntryTime;

        /**
         * Instantiates a new No md entry type.
         *
         * @param group the group
         * @throws FieldNotFound the field not found
         */
        public NoMDEntryType(Group group) throws FieldNotFound {
            this.mdEntryType = group.getChar(MDEntryType.FIELD);
            this.mdEntryPx = group.getDouble(MDEntryPx.FIELD);
            this.mdEntryPositionNo = group.getInt(MDEntryPositionNo.FIELD);
            if (group.isSetField(MDEntrySize.FIELD)) {
                this.mdEntrySize = group.getInt(MDEntrySize.FIELD);
            }

            if (group.isSetField(MDEntryDate.FIELD)) {
                this.mdEntryDate = group.getUtcDateOnly(MDEntryDate.FIELD);
            }

            if (group.isSetField(MDEntryTime.FIELD)) {
                this.mdEntryTime = group.getUtcTimeOnly(MDEntryTime.FIELD);
            }
        }
    }
}