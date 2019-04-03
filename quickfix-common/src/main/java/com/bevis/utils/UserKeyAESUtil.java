package com.bevis.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * 用户Key加解密工具类（AES加密）
 *
 * @author Li Yong
 */
@Slf4j
public class UserKeyAESUtil {
    /**
     * The constant PASSWORD.
     */
    private static final String PASSWORD = "XtWQvzFUeZXWFLK5dx+yaukJ72XXuk8axmPTfUwsyNa6Ea3j+GMWBxnk4TfmYAhnCgSVbqmNJHf3OCuQUPIF/a3UADtoqUtChNGcNGgKh2MTfmYAhnWxOupaxHeTmiL=";

    /**
     * 加密<br/>
     * 1.如果入参为空，返回null
     *
     * @param source 明文
     * @return 密文 string
     */
    public static String encrypt(String source) {
        try {
            if (StringUtils.isEmpty(source)) {
                return null;
            }
            return AESUtil.encrypt(source, PASSWORD);
        } catch (Exception e) {
            LOGGER.error("encrypt user key has error:{}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 解密<br/>
     * 1.如果入参为空，返回null
     *
     * @param encped 密文
     * @return 明文 string
     */
    public static String decrypt(String encped) {
        try {
            if (StringUtils.isEmpty(encped)) {
                return null;
            }
            return AESUtil.decrypt(encped, PASSWORD);
        } catch (Exception e) {
            LOGGER.error("decrypt user key has error:{}", e.getMessage(), e);
            return null;
        }
    }
}
