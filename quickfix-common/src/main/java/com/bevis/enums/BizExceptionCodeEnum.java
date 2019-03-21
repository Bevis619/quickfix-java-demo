package com.bevis.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The enum Service business exception code enum.
 *
 * @author yanghuadong
 * @date 2019 -03-18
 */
@Getter
@AllArgsConstructor
public enum BizExceptionCodeEnum {

    /**
     * 异常码由9位数字构成：一级模块名（三位） + 二级模块名（三位） + 具体错误码（三位）
     * 由于当前模块都比较小，可能没有分更细的二级模块。如果没有二级模块则默认二级模块代码为 001
     */
    SUCCESS(1000, "success"),

    /**
     * The Sys error.
     */
    SYS_ERROR(2001, "sys error"),
    /**
     * The Illegal argument.
     */
    ILLEGAL_ARGUMENT(2002, "illegal argument"),
    /**
     * The File type error.
     */
    FILE_TYPE_ERROR(2003, "file type is wrong"),
    /**
     * The File already exist.
     */
    FILE_ALREADY_EXIST(2004, "file is existed"),
    /**
     * The Login timeout.
     */
    LOGIN_TIMEOUT(2005, "login timeout"),
    /**
     * The Permission denied.
     */
    PERMISSION_DENIED(2006, "permission denied"),
    /**
     * The Missing parameter.
     */
    MISSING_PARAMETER(2007, "required parameter is missing"),
    /**
     * The File not exist.
     */
    FILE_NOT_EXIST(2008, "file not exist"),
    /**
     * The Activity not open.
     */
    ACTIVITY_NOT_OPEN(2010, "activity not open"),
    /**
     * The File format not supported.
     */
    FILE_FORMAT_NOT_SUPPORTED(2011, "file's format is not supported"),
    /**
     * The Method notsupport.
     */
    METHOD_NOTSUPPORT(2012, "request method is not supported"),
    /**
     * The Sign failure.
     */
    SIGN_FAILURE(2013, "fail to check sign"),

    /**
     * The Method uncallable.
     */
    METHOD_UNCALLABLE(2014, "method cannot be called"),

    /**
     * The Api fix user not exist.
     */
    API_FIX_USER_NOT_EXIST(205001, "user not exist", "错误的accesskey"),
    /**
     * The Api fix user no permissions.
     */
    API_FIX_USER_NO_PERMISSIONS(205002, "no permissions", "未开通权限"),
    /**
     * The Api fix user logon sign error.
     */
    API_FIX_USER_LOGON_SIGN_ERROR(205003, "failed to verify signature", "验证登陆消息的签名失败");

    /**
     * The Code.
     */
    private int code;
    /**
     * The Msg.
     */
    private String msg;
    /**
     * The Chines desc.
     */
    private String chinesDesc;

    /**
     * Instantiates a new Service business exception code enum.
     *
     * @param code the code
     * @param msg  the msg
     */
    BizExceptionCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    /**
     * Gets by code.
     *
     * @param code the code
     * @return the by code
     */
    public static BizExceptionCodeEnum getByCode(int code) {
        for (BizExceptionCodeEnum item : values()) {
            if (item.getCode() == code) {
                return item;
            }
        }
        return null;
    }
}