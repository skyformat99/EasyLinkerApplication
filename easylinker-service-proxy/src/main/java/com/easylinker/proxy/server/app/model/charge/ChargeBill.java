package com.easylinker.proxy.server.app.model.charge;

import com.easylinker.proxy.server.app.model.BaseEntity;

/**
 * 充值记录
 *
 * @author wwhai
 */
public class ChargeBill extends BaseEntity {
    private Long userId;
    private String info;
    private String goodsName;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
}
