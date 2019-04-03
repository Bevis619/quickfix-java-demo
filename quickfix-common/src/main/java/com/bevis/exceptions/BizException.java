package com.bevis.exceptions;


import com.bevis.enums.BizExceptionCodeEnum;
import lombok.Data;

/**
 * The type Service business exception.
 *
 * @author yanghuadong
 * @date 2019-03-18
 */
@Data
public class BizException extends RuntimeException {

    /**
     * The Code.
     */
    private int code;

    /**
     * The Desc.
     */
    private String desc;

    /**
     * The Chines desc.
     */
    private String chinesDesc;

    /**
     * The Data.
     */
    private Object data;

    /**
     * Instantiates a new Service business exception.
     *
     * @param bizExceptionCodeEnum the service business exception code enum
     */
    public BizException(BizExceptionCodeEnum bizExceptionCodeEnum) {
        super(bizExceptionCodeEnum.getMsg());
        this.code = bizExceptionCodeEnum.getCode();
        this.desc = bizExceptionCodeEnum.getMsg();
        this.chinesDesc = bizExceptionCodeEnum.getChinesDesc();
    }

    /**
     * Instantiates a new Service business exception.
     *
     * @param bizExceptionCodeEnum the service business exception code enum
     * @param data                             the data
     */
    public BizException(BizExceptionCodeEnum bizExceptionCodeEnum, Object data) {
        super(bizExceptionCodeEnum.getMsg());
        this.code = bizExceptionCodeEnum.getCode();
        this.desc = bizExceptionCodeEnum.getMsg();
        this.chinesDesc = bizExceptionCodeEnum.getChinesDesc();
        this.data = data;
    }

    /**
     * Instantiates a new Service business exception.
     *
     * @param bizExceptionCodeEnum the service business exception code enum
     * @param data                             the data
     * @param appendMsg                        the append msg
     */
    public BizException(BizExceptionCodeEnum bizExceptionCodeEnum, Object data, String appendMsg) {
        super(bizExceptionCodeEnum.getMsg());
        this.code = bizExceptionCodeEnum.getCode();
        this.desc = bizExceptionCodeEnum.getMsg();
        this.chinesDesc = appendMsg + bizExceptionCodeEnum.getChinesDesc();
    }
}
