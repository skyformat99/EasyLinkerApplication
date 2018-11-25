package com.easylinker.proxy.server.app.config.security;

import com.easylinker.proxy.server.app.config.security.filter.CustomUsernamePasswordFilter;
import com.easylinker.proxy.server.app.config.security.handler.AnonymousHandler;
import com.easylinker.proxy.server.app.config.security.handler.LoginFailureHandler;
import com.easylinker.proxy.server.app.config.security.handler.LoginSuccessHandler;
import com.easylinker.proxy.server.app.config.security.user.service.AppUserDetailService;
import com.easylinker.proxy.server.app.utils.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;


/**
 * Created by wwhai on 2018/3/14.
 */
@Configuration
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    LoginSuccessHandler loginSuccessHandler;
    @Autowired
    LoginFailureHandler loginFailureHandler;

    @Autowired
    AnonymousHandler anonymousHandler;
    @Autowired
    LogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    AppUserDetailService appUserDetailService;

    /**
     * WEB资源路径配置器
     *
     * @param web
     * @throws Exception
     */
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/**/*.js", "/lang/*.json", "/**/*.css", "/**/*.js", "/**/*.map", "/**/*.html",
                "/**/*.png");
    }


    /**
     * HTTP资源配置
     * 默认以下地址不检查
     * /register 注册入口
     * /userLogin 登陆入口
     * /index 默认首页
     *
     * @param http
     * @throws Exception
     */

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilter(getCustomUsernamePasswordFilter());
        //配置不用过滤的路由
        http.authorizeRequests()
                .antMatchers(
                        "/",//首页
                        "/userLogin",//登陆
                        "/user/register",//注册
                        "/forgetPassword",//发送忘记密码的邮件
                        "/user/active/**",//激活
                        "/api/v1/**",//这个是Jwt的路径
                        "/web-socket/**",
                        "/pay/**" // TODO 临时放行支付宝支付链接，后期研究具体策略

                )
                .permitAll();


        http.authorizeRequests().anyRequest().authenticated()
                .and().formLogin().disable().httpBasic().disable()
                .logout().permitAll()
                .and().logout().logoutSuccessHandler(logoutSuccessHandler).logoutUrl("/logOut")
                .and().formLogin().successHandler(loginSuccessHandler)
                .and().formLogin().failureHandler(loginFailureHandler)
                .and().rememberMe().alwaysRemember(true).tokenValiditySeconds(2592000)
                // 配置UserDetailsService
                .and().exceptionHandling().authenticationEntryPoint(anonymousHandler)
                .and().csrf().disable().rememberMe().rememberMeServices(rememberMeServices())
                .and().headers()
                .frameOptions();

    }


    @Bean
    public static RememberMeServices rememberMeServices() {

        SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        rememberMeServices.setValiditySeconds(3600000);//过期时间
        return rememberMeServices;
    }

    /**
     * 自定义认证过程
     *
     * @return
     * @throws Exception
     */

    @Bean
    public CustomUsernamePasswordFilter getCustomUsernamePasswordFilter() throws Exception {
        CustomUsernamePasswordFilter customUsernamePasswordFilter = new CustomUsernamePasswordFilter();
        customUsernamePasswordFilter.setAuthenticationManager(super.authenticationManager());
        customUsernamePasswordFilter.setAuthenticationFailureHandler(loginFailureHandler);
        customUsernamePasswordFilter.setAuthenticationSuccessHandler(loginSuccessHandler);

        return customUsernamePasswordFilter;
    }

    /**
     * 认证管理器
     *
     * @return
     * @throws Exception
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    /**
     * 密码认证过程
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(appUserDetailService).passwordEncoder(new PasswordEncoder() {

            @Override
            public String encode(CharSequence rawPassword) {
                return Md5Util.encodingMD5(rawPassword.toString());
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return encodedPassword.equals(Md5Util.encodingMD5(rawPassword.toString()));
            }
        });
    }


}
