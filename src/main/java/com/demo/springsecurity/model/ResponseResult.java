package com.demo.springsecurity.model;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
@Data
public class ResponseResult<t> {
    private int status;
    private String message;
    private t data;
    private long timestamp ;

    public ResponseResult (){
        this.timestamp = System.currentTimeMillis();
    }

    public static <t> ResponseResult<t> success(t data) {
        ResponseResult<t> resultData = new ResponseResult<>();
        resultData.setStatus(ReturnCode.RC200.getCode());
        resultData.setMessage(ReturnCode.RC200.getMessage());
        resultData.setData( data);
        return resultData;
    }

    public static <t> ResponseResult<t> fail(int code, String message) {
        ResponseResult<t> resultData = new ResponseResult<>();
        resultData.setStatus(code);
        resultData.setMessage(message);
        return resultData;
    }
}
