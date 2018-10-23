package com.easylinker.proxy.server.app.config.securityconfig.filter;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.server.ui.LoginPageGeneratingWebFilter;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 把认证时 ，提交的JSON ，封装成了一个类
 * 默认包含了  用户名  密码  验证码
 */
public class LoginParameterCatcher {
    private static final Logger logger = LoggerFactory.getLogger(LoginPageGeneratingWebFilter.class);


    public static final String LOGIN_PARAM = "loginParam";
    public static final String PASSWORD_PARAM = "password";

    private String loginParam;
    private String password;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LoginParameterCatcher(HttpServletRequest httpServletRequest) {
        try {
            BufferedReader bufferedReader = new BufferedReader(
                    new BufferedReader(
                            new InputStreamReader(httpServletRequest.getInputStream())));
            if (httpServletRequest.getMethod().equals("POST")) {
                String tempLine = "";
                StringBuffer jsonStringBuffer = new StringBuffer();
                while ((tempLine = bufferedReader.readLine()) != null) {
                    jsonStringBuffer.append(tempLine);
                }
                JSONObject jsonObject = JSONObject.parseObject(jsonStringBuffer.toString());
                String loginParam = jsonObject.get(LOGIN_PARAM).toString();
                String password = jsonObject.get(PASSWORD_PARAM).toString();
                if (loginParam == null) {
                    loginParam = "";
                }

                if (password == null) {
                    password = "";
                }
                this.setloginParam(loginParam);
                this.setPassword(password);

            } else {
                logger.info("Only POST method can be support REST!");

            }

        } catch (Exception e) {
            logger.info("RequestBean param error!");
        }

    }

    public String getloginParam() {
        return loginParam;
    }

    public void setloginParam(String loginParam) {
        this.loginParam = loginParam;
    }
}
