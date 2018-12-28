package com.easyiot.easylinker.service.proxy.config.exception;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.util.JSONParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice

public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 系统异常处理，比如：404,500
     *
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JSONObject defaultErrorHandler(Exception e) {
        logger.error("出现了异常:" + e.getClass() + "=====" + e.getMessage());
        //e.printStackTrace();
        JSONObject resultJson = new JSONObject(true);
        if (e instanceof io.jsonwebtoken.ExpiredJwtException) {

            resultJson.put("state", 402);
            resultJson.put("message", "Token已经过期!");
        }
        if (e instanceof JSONParseException) {

            resultJson.put("state", 0);
            resultJson.put("message", "JSON格式错误!");
        } else {
            resultJson.put("state", 500);
            resultJson.put("message", "未知错误!");
        }
        return resultJson;


    }


}