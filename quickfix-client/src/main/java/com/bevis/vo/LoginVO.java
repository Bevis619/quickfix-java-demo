package com.bevis.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * The type Login vo.
 *
 * @author yanghuadong
 * @date 2019 -04-09
 */
@Data
public class LoginVO implements Serializable {
    /**
     * The Sender comp id.
     */
    private String senderCompID;

    /**
     * The Target comp id.
     */
    private String targetCompID;

    /**
     * The Secretkey.
     */
    private String secretkey;

    /**
     * The Encrypt method.
     */
    private Integer encryptMethod;
    /**
     * The Heart bt int.
     */
    private Integer heartBtInt;
    /**
     * The Raw data.
     */
    private String rawData;
    /**
     * The Raw data length.
     */
    private Integer rawDataLength;
}