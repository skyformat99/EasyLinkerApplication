package com.easylinker.quartz.config;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.Getter;
import lombok.Setter;
import org.quartz.SchedulerException;
import org.quartz.utils.ConnectionProvider;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @Author:   zhaolei
 * @Version   1.0.0
 *
 * @Description: 自定义Quartz数据库连接池，使用Druid
 * @Date:     2018/11/29 1:59
 * Copyright (C), 2016-2018, EasyLinker V3
 */

@Getter
@Setter
public class DruidConnectionProvider implements ConnectionProvider {

    //JDBC驱动
    public String driver;
    //JDBC连接串
    public String URL;
    //数据库用户名
    public String user;
    //数据库用户密码
    public String password;
    //数据库最大连接数
    public int maxConnections;
    //数据库SQL查询每次连接返回执行到连接池，以确保它仍然是有效的。
    public String validationQuery;
    private boolean validateOnCheckout;
    private int idleConnectionValidationSeconds;
    public String maxCachedStatementsPerConnection;
    private String discardIdleConnectionsSeconds;
    public static final int DEFAULT_DB_MAX_CONNECTIONS = 10;
    public static final int DEFAULT_DB_MAX_CACHED_STATEMENTS_PER_CONNECTION = 120;
    //Druid连接池
    private DruidDataSource dataSource = new DruidDataSource();

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void shutdown() throws SQLException {
        dataSource.close();
    }

    @Override
    public void initialize() throws SQLException {
        if (this.URL == null) {
            throw new SQLException("DBPool could not be created: DB URL cannot be null");
        }
        if (this.driver == null) {
            throw new SQLException("DBPool driver could not be created: DB driver class name cannot be null!");
        }
        if (this.maxConnections < 0) {
            throw new SQLException("DBPool maxConnectins could not be created: Max connections must be greater than zero!");
        }
        try{
            dataSource.setDriverClassName(this.driver);
        } catch (Exception e) {
            try {
                throw new SchedulerException("Problem setting driver class name on datasource: " + e.getMessage(), e);
            } catch (SchedulerException e1) {
            }
        }
        dataSource.setUrl(this.URL);
        dataSource.setUsername(this.user);
        dataSource.setPassword(this.password);
        dataSource.setMaxActive(this.maxConnections);
        dataSource.setMinIdle(1);
        dataSource.setMaxWait(0);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(DEFAULT_DB_MAX_CONNECTIONS);
        if (this.validationQuery != null) {
            dataSource.setValidationQuery(this.validationQuery);
            if(!this.validateOnCheckout)
                dataSource.setTestOnReturn(true);
            else
                dataSource.setTestOnBorrow(true);
            dataSource.setValidationQueryTimeout(this.idleConnectionValidationSeconds);
        }
    }
}
