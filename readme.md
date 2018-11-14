# EasyLinkerApplication
EasyLinkerApplication V3
##异常代码 (后续会有表统计整理出来)
```
用户:1XX
安全:2XX
API:3XX
Token:4XX
业务不相干服务器错误:5xx

```
# 代码设计规则
## 控制器层
```java
//一个模板

/**
 * C
 * U
 * R
 * D
 */
public interface EasyBaseControllerTemplate {
    /**
     * 添加一个
     *
     * @param
     * @return
     */

    JSONObject add(HttpServletRequest httpServletRequest, @RequestBody JSONObject requestBody);

    /**
     * 不同的删除和¥重载
     *
     * @param
     */

    JSONObject deleteById(HttpServletRequest httpServletRequest, @PathVariable Long id);

    /*
     * 更新的各种重载
     **/

    JSONObject update(HttpServletRequest httpServletRequest, @RequestBody JSONObject requestBody);

    /*
     * 列出所有
     * */
    JSONObject list(HttpServletRequest httpServletRequest, JSONObject requestBody);

}
```