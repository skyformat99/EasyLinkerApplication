package com.easylinker.proxy.server.app.config.alipay;

import org.springframework.stereotype.Component;

/**
 *
 * @Author:   zhaolei
 * @Version   1.0.0
 *
 * @Description: Alipay 配置信息
 * @Date:     2018/11/26 1:28
 * Copyright (C), 2016-2018, EasyLinker V3
 */
@Component
public class AliPayConfig {

    // 支付宝应用ID
    public static String app_id = "2016091400512253";

    // 私钥
    public static String private_key = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCqZSLiaq1YnHhhzBK6HjjlWBecR0Wc7HX+YQANJc1ngsHvGCV92Svw62Ljx9rb6mWBcQ9NsCh8T0e/sgWNlCKzXy5SoANHbd8qAUAZVwyYdiOjWH/2CM4m6q4LtiGwVHeQ/xRu3zGvybkhaEMNB+858HdWmtgse+0xZdeAl578rMqCtuRM8we0GIERq2nb+3/mE1qIrTuC3sX31mT3oqjuXl/2Cw8uniBy3skBmv2dIlGvEgNuewAh6JSLV3ZaP5rqpxs6pyt9sl/5BOF12+GsjxmhHS/U94s2FdnuwjCHUqVwfsX2CNsjd4Nc7MXOzrakitB223o011hw25MLogcxAgMBAAECggEADDOj1NzYvvCteitX5/YEEwBE6I7+ACe6p1Wg4FAlrlThuFJdVO65Q85HTZJhLMBTNnq3ItQLgiNXOvXvidziwz98nLQSqAnJIkPmqCngoAZNtQPAXgjxYj4J6pLHRM43pa1udCYNc84ZMLg7nPJFgjujABOkiULykEf/r5ir7OIAppTriQQs+FVYS4KaR5NFjkWrhTj5amyS8lKnO9Tqqm3rjTTF8UvsaQlxWzrcGcRQnfMyeeco+M0IRVeixVuwHJ5uF39dMb5/QokNiR7Xvs6TMqkVLsO0q34hmI020xG6OqRwuOUXFl1gvF5qZJl4GB5Dal20lZ7a195OlR26BQKBgQDn/xObr+rQBeePgqnkV8tZh2m0cPG4bI4rrjxw67bXalMcId3CVCbHuMaJbfrg00PzTmZKWK6ae+tIYO/vQjKJMzhWjdYTVSOjW5GxNLYf1Qf3Ht+SM3v8ntfg7aJ5KYZcfgNMda4skUgM5l63ulf5WyE75EUldFHki6RzGY9X8wKBgQC8Bmp9zW7hTROB2XirM0LoX0AbbyXnKdgVP1A6D7s+DNJe/MxpmNnFAm9lj5O2M536wY9/a8m7IRtno1hpT3K5OIV6AECcKXJ64Z6bG27qSDWVUn6lqGqffYFyAxkYZOnxifvdJQyDgb2c4TDJ5HfK5F4YjDLH/zqj6fPzMStxSwKBgHaCgLu5A5Q8T+upbavthT04xTCduXziR0Xc/ZcZcg9cixQF4MhSQHUp2JF/kvaiNUXmuK4l2xujTa9thWONR0960L62ObMqfMgIEMabDKX6JtV/+ekvT5jec5y9B5ApPC+nV3wJaW3u0QV18NCd6i7A9JspooDdiK/c/aamWektAoGAUTTwL29XkUUpxK45WK+mGRhyZqblluZSpjKXIsyCkTUWcuH6Iy1Z+KWpmRKeuFWoyJFquZzDHugYnvVEjQ88PFT2CRAs14VrJTlmuBTsHZGcWuarPj7z9fhNvQEriIalJ6XxurxyoCWb0DLOUJQ1TNK+9RkwIYLRP9QegY1d9usCgYEAybrFBq9qRM0kFxTyOz7pNGSKy7SQb7eYWW5xF1fSS+C37of3FBKjPopZXsmoyqOErErlquIGLPhLmZgVU0H4MjQGUGRx+lGrpc9haorZXidh9ACSUnpE17Thj+4AKvRvtOfQaDh5ixFhifpMkbTUky5ZnNx4jkLFJmBAKhO/1+8=";

    // 公钥
    public static String public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnRsVJO7PcNYM+kPIXCnVOQ/wEeQTwk/mtKPWmwgPvQiAll2QNzl0VqhqPMI0uHjXRvYXu6CUN+xA0wWWulZ37Wh2zffF8Vnt3Tr6dxWsvIAtuprFmq6+uiboNSf33OcOT4bZcYvti9DBQiMO8+wY8o6pwcEJU9E0VpzjKFZ9ZsItOP5FEi7l3nEdBAjZA4/CA2iO6sJRsIXYTxxHm86ae590FgZij1wl3u/PbyV4T7yC48UWR9LtY4s1C6EC6sT9QbIHTUqXwbGNXxxJhvuYOJCJY+HLzLAc5HlvO8kJhFtkAuh9al03TFXWGteCl9gnceNykgdVlJyPv3qCt7Rm8QIDAQAB";

    // 异步通知地址
    public static String notify_url = "http://gnikweb.imwork.net/pay/notify";

    // 回调地址
    public static String return_url = "http://gnikweb.imwork.net/pay/returnUrl";

    // 签名方式
    public static String sign_type = "RSA2";

    public static String charset = "UTF-8";

    /**
     * TODO 支付宝网关配置
     * 沙箱环境：https://openapi.alipaydev.com/gateway.do
     * 正式环境：https://openapi.alipay.com/gateway.do
     */
    public static String url = "https://openapi.alipaydev.com/gateway.do";


    public static String format = "json";


}
