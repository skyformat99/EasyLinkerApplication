package com.easyiot.easylinker.service.proxy.model.client;

import lombok.Data;

import java.io.Serializable;


/**
 *
 * @Author:   zhaolei
 * @Version   1.0.0
 *
 * @Description: 设备模糊查询实体
 * @Date:     2018/12/25 18:26
 * Copyright (C), 2016-2018, EasyLinker V3
 */

@Data
public class DeviceSearchEntry implements Serializable {

    /**
     * 查询类型
     * all 查询所有，mqtt 查询mqtt设备...
     */
    private String type;

    /**
     * 查询模式
     * name 根据设备名称查询，info 根据设备描述查询
     */
    private String mode;

    /**
     * 查询关键字
     */
    private String keyword;

    /**
     * 第几页
     */
    private int pageNum;

    /**
     * 每页显示数量
     */
    private int pageSize;
}
