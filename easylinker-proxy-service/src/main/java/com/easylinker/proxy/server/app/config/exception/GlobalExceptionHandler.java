package com.easylinker.proxy.server.app.config.exception;

import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.ExpiredJwtException;
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
        JSONObject resultJson = new JSONObject();
        if (e instanceof ExpiredJwtException) {

            logger.error("出现了异常:" + e.getClass());
            resultJson.put("state", 402);
            resultJson.put("message", "Token已经过期!");
        }
        return resultJson;


    }


}