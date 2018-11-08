package com.easylinker.proxy.server.app.config.security.filter;

import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 关于这个Filter的理解
 * 框架在处理登录的时候 如果你自定义认证过程 一般都是直接在attemptAuthentication 中实现
 * 如果你想用子类的loadUserByUsername 来实现
 * 就在下面加上代码:setDetails(request, usernamePasswordAuthenticationToken);
 */
public class CustomUsernamePasswordFilter extends UsernamePasswordAuthenticationFilter {

    private static final String DEFAULT_LOGIN_URL = "/userLogin";
    private static final String DEFAULT_LOGIN_METHOD = "POST";


    public CustomUsernamePasswordFilter() {
        setAuthenticationManager(super.getAuthenticationManager());

        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(DEFAULT_LOGIN_URL, DEFAULT_LOGIN_METHOD));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(DEFAULT_LOGIN_URL, DEFAULT_LOGIN_METHOD));
        LoginParameterCatcher loginParameterCatcher = new LoginParameterCatcher(request);

        String loginParam = loginParameterCatcher.getloginParam();
        String password = loginParameterCatcher.getPassword();


        Authentication authentication;

        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Only support http post method!");
        } else {

            try {
                UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(loginParam, password);
                this.setDetails(request, authRequest);
                authentication = this.getAuthenticationManager().authenticate(authRequest);

            } catch (Exception e) {
                throw  e;
            }
        }
        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);


    }


}
