package com.xj.utils;

/**
 * 功能描述： 静态变量
 *
 * @author:xj
 * @date: 2019/12/26 15:22
 */
public interface InitConstants {
    //用户id
    public static String USER_ID = "id";
    //用户名
    public static String USER_NAME = "username";
    //密码
    public static String Password = "password";
    //用户登陆ip
    public static String IP = "ip";
    //错误信息
    public static String ERR_MSG = "errMsg";


    //状态码：
    int SUCCESS_CODE =200;//处理成功
    int FAIL_CODE =400;//处理失败
    int EXCEPTION_CODE=199;//异常
    int OBJECT_NOT_EXIST=301;//返回，但是对象不存在
}
