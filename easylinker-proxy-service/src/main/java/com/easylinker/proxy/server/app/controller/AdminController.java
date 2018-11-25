package com.easylinker.proxy.server.app.controller;

import com.easylinker.proxy.server.app.config.jwt.JwtAuthRole;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理员
 * 功能：
 * 管理用户：CURD，授权
 * 配置服务器
 * 查看日志
 * 给用户充值
 *
 * @author wwhai
 */
@RestController
@RequestMapping(value = "/admin")
@JwtAuthRole(roles = {"ROLE_ADMIN"})
public class AdminController {



}
