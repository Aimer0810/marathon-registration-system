package com.marathon.common;

/**
 * 统一响应结果类 —— 整个项目中所有 API 接口都使用这个类来包装返回数据
 *
 * 什么是泛型 &lt;T&gt;？
 *   泛型就是"类型参数"。写成 Result&lt;User&gt; 时，T 就是 User，
 *   写成 Result&lt;String&gt; 时，T 就是 String。
 *   这样同一个类可以适用于任何返回数据类型，不需要每种类型都写一个 Result 类。
 *
 * 前端收到的 JSON 格式：
 *   { "code": 200, "msg": "success", "data": { ...实际数据... } }
 *
 * code 的含义：
 *   200 — 成功
 *   500 — 通用错误（默认）
 *   401 — 未登录
 */
public class Result<T> {
    private Integer code;    // 状态码，200 表示成功
    private String msg;      // 提示信息，成功时是 "success"，失败时是错误描述
    private T data;          // 实际返回的数据，泛型 T 可以是任何类型

    // 空构造方法（Spring 框架反序列化 JSON 时需要）
    public Result() {}

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // ========== 静态工厂方法：方便快速创建 Result 对象 ==========

    /** 成功响应，没有额外信息时使用 */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    /** 成功响应，需要自定义成功信息时使用 */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    /** 失败响应，默认错误码 500 */
    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

    /** 失败响应，可以自定义错误码（如 401 未登录） */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    // ========== Getter / Setter（Spring 序列化 JSON 时需要） ==========
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
