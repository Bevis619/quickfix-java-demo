package com.bevis.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.bevis.utils.Md5Util;
import lombok.Data;
import quickfix.FieldNotFound;
import quickfix.fix44.Logon;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * The type Logon bo.
 *
 * @author yanghuadong
 * @date 2019 -03-18
 */
@Data
public class LogonBO extends HeaderBO implements Serializable {
    /**
     * The Encrypt method.
     */
    @JSONField(serialize = false)
    private Integer encryptMethod;

    /**
     * The Heart bt int.
     */
    @JSONField(serialize = false)
    private Integer heartBtInt;

    /**
     * The Raw data.
     */
    @JSONField(serialize = false)
    private String rawData;

    /**
     * The Raw data length.
     */
    @JSONField(serialize = false)
    private Integer rawDataLength;

    /**
     * Instantiates a new Logon bo.
     *
     * @param message the message
     * @throws FieldNotFound the field not found
     */
    public LogonBO(Logon message) throws FieldNotFound {
        super(message);
        this.encryptMethod = message.getEncryptMethod().getValue();
        this.heartBtInt = message.getHeartBtInt().getValue();
        this.rawData = message.getRawData().getValue();
        this.rawDataLength = message.getRawDataLength().getValue();
    }

    /**
     * Gets sorted string.
     *
     * @return the sorted string
     */
    public String getSign(String secretKey) {
        String splicer = ",";
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(this));
        jsonObject.put("$secretKey", secretKey);
        SortedSet<String> sortedSet = new TreeSet(jsonObject.keySet());
        StringBuilder sortedStr = new StringBuilder();
        sortedSet.forEach(item -> sortedStr.append(jsonObject.getString(item)).append(splicer));
        if (sortedStr.lastIndexOf(splicer) > 0) {
            sortedStr.deleteCharAt(sortedStr.length() - 1);
        }

        String md5 = Md5Util.getMD5(sortedStr.toString());
        return md5;
    }
}