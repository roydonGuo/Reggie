package com.roydon.reggie.utils;

/**
 * Created by Intellij IDEA
 * Author: yi cheng
 * Date: 2022/10/3
 * Time: 16:20
 **/
public class RedisConstants {

    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final String LOGIN_USER_KEY = "login:token:";
    public static final String DISH_KEY = "dish:";
    public static final Long DISH_TTL = 60L; // 60分钟


    public static final Long LOGIN_CODE_TTL = 5L; // 验证码存活 5 分钟
    public static final Long LOGIN_USER_TTL = 60*24*7L;
}
