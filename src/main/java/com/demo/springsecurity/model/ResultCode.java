package com.demo.springsecurity.model;

public interface ResultCode {
    public static Integer SUCCESS = 20000;
    public static Integer ERROR = 20001;
    public static Integer FORBIDDEN = 20003;
    public static Integer UNAUTHORIZED = 20004;
    public static Integer AUTHENTICATION_ERROR = 401;
}
