package com.seckill.model.entity;

/**
 * @author Dell
 * @create 2019-07-14 14:11
 */
public class CodeMsg {
    private int code;
    private String msg;

    CodeMsg(){}

    // 通用的错误码
    public static CodeMsg SUCCESS = new CodeMsg(0, "success");
    public static CodeMsg SERVER_SERROR = new CodeMsg(500100, "服务端异常");
    public static CodeMsg BIND_ERROR = new CodeMsg(500101, "参数校验异常:%s");
    public static CodeMsg VERITY_CODE_ERROR = new CodeMsg(500102, "验证码错误");
    public static CodeMsg ACCESS_LIMIT_REACHED= new CodeMsg(500104, "访问太频繁！");

    // 登录模块 5002xx
    public static CodeMsg SESSION_ERROR = new CodeMsg(500210, "请登录！");
    public static CodeMsg MOBILE_NOT_EXIST = new CodeMsg(500214, "手机号码不存在");
    public static CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "密码错误");
    // 订单模块 5004xx
    public static CodeMsg ORDER_NOT_EXIST = new CodeMsg(500400, "订单不存在");

    // 秒杀模块 5005xx
    public static CodeMsg GOODS_COUNT_OVER = new CodeMsg(500500, "商品库存为空！");
    public static CodeMsg REPEATE_SECKILL = new CodeMsg(500501, "不能重复秒杀");
    public static CodeMsg SECKILL_FAIL = new CodeMsg(500502, "秒杀失败");

    private CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public CodeMsg fillArgs(Object... args) {
        int code = this.code;
        String message = String.format(this.msg, args);
        return new CodeMsg(code, message);
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "CodeMsg [code=" + code + ", msg=" + msg + "]";
    }
}
