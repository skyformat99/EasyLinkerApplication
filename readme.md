# EasyLinkerApplication
EasyLinkerApplication V3
##异常代码 (后续会有表统计整理出来)
```
用户:1XX
安全:2XX
API:3XX
Token:4XX
Mqtt:6XX
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
# 常见的端口
```
mqtt :1884
tcp:61613
websocket:2500/web-socket/
```
# 内部的三种消息格式
## 数据消息
```
{
    "data":{
        "data":{
            "V1":"1",
            "V2":"2"
        },
        "persistent":"true",
        "info":"V"
    },
    "type":"data"
}
```
## 回显消息
### Topic:/system/echo
```
{
    "data":{
        "data":{
            "cmd":"ls"
        }
    },
    "type":"cmd"
}
```
## 特殊命令消息
### Topic:/system/cmd
```
{
    "data":{
        "data":{
            "echo":"echo"
        }
    },
    "type":"echo"
}
```