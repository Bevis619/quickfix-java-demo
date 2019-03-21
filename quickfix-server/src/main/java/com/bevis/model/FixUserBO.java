package com.bevis.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

/**
 * The type Fix user bo.
 *
 * @author yanghuadong
 * @date 2019 -03-21
 */
@Data
@AllArgsConstructor
public class FixUserBO implements Serializable {
    /**
     * The Accesskey.
     */
    private String accesskey;
    /**
     * The Secretkey.
     */
    private String secretkey;
    /**
     * The User no.
     */
    private String userNo;
    /**
     * The Is valid.
     */
    private Boolean isValid;
}