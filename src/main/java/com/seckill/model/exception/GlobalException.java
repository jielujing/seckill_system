package com.seckill.model.exception;

import com.seckill.model.entity.CodeMsg;

/**
 * @author Dell
 * @create 2019-07-19 1:26
 */
public class GlobalException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg) {
        super(codeMsg.toString());
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
