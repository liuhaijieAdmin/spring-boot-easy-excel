package com.zhuzi.enums;

public enum ResponseCode {

    SUCCESS(0, "操作成功"),
    ERROR(500, "操作出错"),
    EXCEPTION(501, "服务器出了点小差..."),
    RESOURCE_NOT_EXIST(404, "请求的资源不存在"),

    UNKNOWN_ERROR(-1, "未知异常, 请联系系统管理员"),
    TIME_OUT(504, "请求处理超时"),
    NO_AUTHORITY(403, "您暂无权限操作"),
    SYNC_ERROR(555, "数据同步出错"),
    EXTERNAL_RESOURCE_REQ_ERROR(556, "请求外部资源出错"),

    /*
    * 身份权限相关的错误码
    * */
    LOGIN_FAILURE(1001, "登录认证失败"),
    VALIDATE_CODE_EXPIRED(1002, "验证码已过期"),
    VALIDATE_CODE_ERROR(1003, "验证码不正确"),
    USER_IS_NULL(1004, "用户不存在"),
    USER_PASSWORD_ERROR(1005, "用户名或密码错误"),
    NOT_LOGIN(1006, "用户未登陆或已失效"),
    SESSION_TIMEOUT(1007, "会话超时, 请重新登录"),
    NOT_AUTHORIZED(1008, "未授权的操作"),
    VALIDATE_MESSAGE_ERROR(1009, "短信验证码校验失败, 请重新获取"),
    SEND_MESSAGE_ERROR(1010, "发送验证短信失败, 请稍后重试"),
    COOKIE_NOT_FOUND(1011, "Cookie校验失败，请重新登录"),
    TOKEN_VALIDATE_ERROR(1012, "Token校验出错，请重新登录"),
    USER_VERIFY_ERROR(1013, "身份信息校验出错"),

    /*
    * CRUD操作相关的错误码
    * */
    NOT_UNIQUE(1001, "数据重复"),
    SAVE_ERROR(1002, "数据保存失败"),
    UPDATE_ERROR(1003, "数据更新失败"),
    DELETE_ERROR(1004, "数据删除失败"),
    NOT_EXIST_ERROR(1005, "数据不存在"),
    EXCEEDS_LIMIT(1099, "数据超出限制"),

    /*
    * 文件上传/导出相关的错误码
    * */
    EXPORT_ERROR(1101, "数据导出失败"),
    UPLOAD_FILE_ERROR(1102, "上传文件失败"),
    UPLOAD_FILE_MIME_ERROR(1103, "上传文件格式格式有误"),
    ANALYSIS_EXCEL_ERROR(1104, "解析Excel出错"),
    USE_TEMPLATE(1105, "导入数据有误, 请按模板信息重新导入"),
    FILE_IS_NULL(1106, "您上传的文件为空"),
    FILE_NUMBER_LIMIT(1106, "您上传的文件数超出限制"),
    FILE_REMOVE_ERROR(1107, "删除服务器文件出错"),
    FILE_DOWNLOAD_ERROR(1108, "文件下载出错"),

    /*
     * 数据格式校验相关的错误码
     * */
    REQUEST_PARAM_ERROR(1200, "缺少必传的参数"),
    PARAMS_VALID_ERROR(1201, "参数校验出错"),
    ANALYSIS_ERROR(1202, "数据解析异常"),
    DECRYPT_ERROR(1203, "数据解密失败"),
    TIME_FORMAT_ERROR(1203, "时间格式错误, 请确认格式为【yyyy/MM/dd】或【yyyy-MM-dd】"),
    START_TIME_IS_AFTER_END_TIME(1204, "开始时间不可晚于结束时间"),
    TIME_NOT_NULL(1205, "必填的时间字段不能为空"),
    PHONE_REGEX_ERROR(1251, "手机号码不正确"),
    EMAIL_REGEX_ERROR(1252, "邮箱格式校验未通过"),
    MOBILE_PHONE_ERROR(1253, "身份证号校验未通过"),
    ;


    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static ResponseCode ofCode(int code) {
        for (ResponseCode e : ResponseCode.values()) {
            if (e.getCode() == code) {
                return e;
            }
        }
        return null;
    }

}
