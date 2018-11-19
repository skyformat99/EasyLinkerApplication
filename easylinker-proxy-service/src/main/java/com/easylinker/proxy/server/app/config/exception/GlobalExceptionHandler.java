package com.easylinker.proxy.server.app.config.exception;

import com.alibaba.fastjson.JSONObject;
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
        logger.error("出现了异常:" + e.getMessage());
        logger.error("------------------------------------------------------");
        e.printStackTrace();
        logger.error("------------------------------------------------------");
        resultJson.put("501",e.getMessage());

        return resultJson;
    }


}