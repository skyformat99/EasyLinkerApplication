# EasyLinkerV3 开发者手册
## 1.EasyLinkerV3概述
>EasyLinkerV3是继V2版本以后发布的全新版本，和V2相比，使用了Activemq作为消息中间件，全部采用了全新的Java生态体系，很适合Java系列的应用使用。同时Activemq和V2的EMQ相比，社区活跃，资料众多，对开发者很友好，研发成本大大下降。因为核心业务层用了SpringBoot开发，所以附带了与生俱来的分布式特点，可以根据不同的需求灵活的选择部署环境，是否集群或者分布式。同时数据库采用了MongoDB，支持Redis缓存，环境支持Docker一键部署，开箱即用，简化了安装成本，非常适合快速搭建物联网应用平台。
V3是一个全新的尝试，希望给各位物联网开发者，创客，和爱好者带来不一样的理念和体验。
## 2.EasyLinker设计架构
>SpringBoot负责所有的核心业务逻辑，包括用户系统，权限管理等等。Activemq则相当于是消息路由器，负责终端和服务器的消息交互。
```
                                   +------------------+
                                   |                  |
             +---------------------+ 客 户 端          +--------------------+
             |                     |                  |                    |
             |                     +---------+--------+                    |
             |                               |                             |  Mqtt协 议
             | POST/GET data                 |                             |
             |                               |                             |
             |                               |                             |
             |                               | WEB实 时 推 送               |
             |                               |                             |
             ^                               ^                             ^
     +-------+----------+        +-----------+----------+       +----------+---------+
     |  HTTP API        |        |                      |       |                    |
     |                  |        | WebSocketAPI         |       |  MQTT Service      |
     |                  |        |                      |       |         ^          |
     +--------+---------+        +----------+-----------+       +--------------------+
              |                             |                             |
              |                             |                             |
+---------+-----------------------------+-------------------------------------------------------+
|                             EasyLinkerV3                                                      |
|                                                                                               |
|                                                                                               |
|                                                                                               |
|                                                                                               |
|  +------------------------------------------------------------+         +------------------+  |
|  |  用 户 管 理           || 认 证            ||  客 户 端 管 理 |         |                  |  |
|  |                       ||                 ||                |         |                  |  |
|  +------------------------------------------------------------+  <----+ |   Redis          |  |
|  |------------------------------------------------------------|   +-->  |                  |  |
|  |------------------------------------------------------------|         |                  |  |
|  ||                                                          ||         +------------------+  |
|  ||                                                          ||                               |
|  ||                Activemq                                  ||                               |
|  ||                                                          ||                               |
|  ||                                                          ||                               |
|  +------------------------------------------------------------+          +-----------------+  |
|  |                                                            |          |                 |  |
|  |                                                            | +----->  |                 |  |
|  |                  SpringBoot                                |          |    MongoDB      |  |
|  |                                                            |  <-----+ |                 |  |
|  +------------------------------------------------------------+          +-----------------+  |
|                                                                                               |
+-----------------------------------------------------------------------------------------------+
```
## 3.EasyLinker技术体系
### EasyLinker技术架构可以分为两部分：业务逻辑层和MQ消息处理层
#### 1.业务逻辑层
>业务逻辑层使用了Java WEB框架生态体系：SpringFrameWork 包含了SpringBoot，SpringMVC，SpringSession，SpringSecurity，SpringDataJPA。其中缓存用的Redis，数据库使用了MongoDB。
#### 2.MQ消息处理层
>消息处理器抛弃了V2的EMQ，使用了Activemq，Activemq的好处是社区庞大，资料众多而且开源免费，很容易和JavaWEB体系的系统结合起来使用。
## 4.EasyLinker业务层二次开发指导
>核心业务全部在Proxy这个项目里面，如果需要集成新的配置或者组件进去
## 5.Activemq二次开发指导
>业务层我们遵守MVP模式，和MVC模式不一样，P指的是数据呈现层而不是数据展示层，P层只关注数据结果而不关心数据怎么去展示出来，展示直接给前段就可以了。
所有的逻辑控制层全部在controller包里面，需要的时候直接新增加就可以了。下面给出一个controller的模板：

>controller

```java
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
>如果需要Service和DAO层，只需要在对应的包内放入自己的数据就可以了，Model则直接放入Model ,不同的是，Model则需要继承BaseModel。下面给出两个模板：
>DAO层 
```java
 //DAO层，需要继承：MongoRepository
 public interface ClientDataEntryRepository extends MongoRepository<ClientDataEntry, Long> {
     Page<ClientDataEntry> findAllByClientId(Long clientId, Pageable pageable);
 }
```
>Service层 

```java
//BaseService 接口不是必须实现的，这个是本人开发过程中自己写的一个公共模板
@Service("ClientDataEntryService")
public class ClientDataEntryService implements BaseService<ClientDataEntry> {
    @Autowired
    ClientDataEntryRepository clientDataEntryRepository;
    @Override
    public void save(ClientDataEntry clientDataEntry) {
        clientDataEntryRepository.save(clientDataEntry);
    }
    @Override
    public void delete(ClientDataEntry clientDataEntry) {
        clientDataEntryRepository.delete(clientDataEntry);
    }
    @Override
    public Page<ClientDataEntry> getAll(Pageable pageable) {
        return clientDataEntryRepository.findAll(pageable);
    }
    /**
     * 根据客户端的ID查找数据
     *
     * @param clientId
     * @param pageable
     * @return
     */
    public Page<ClientDataEntry> getByClientId(Long clientId, Pageable pageable) {
        return clientDataEntryRepository.findAllByClientId(clientId, pageable);
    }
}
```
## 6.Activemq在EasyLinker的特殊通道
- WebSocket:host:2500/web-socket/
- Mqtt:host:1884
## 7.客户端消息格式

>数据消息:persistent控制是否持久化的字段

```json
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
>回显消息,Topic:/system/echo

```json
{
    "data":{
        "data":{
            "cmd":"ls"
        }
    },
    "type":"cmd"
}
```
>特殊命令消息,Topic:/system/cmd

```json
{
    "data":{
        "data":{
            "echo":"echo"
        }
    },
    "type":"echo"
}
```
## 8.常见的端口
 ```
 mqtt :1884
 tcp:61613
 websocket:2500/web-socket/
 COAP:5683/api_coap_1_0
 ```
 
## 9.异常代码(X00表示成功)
```
用户:1XX
安全:2XX
API:3XX
Token:4XX
Mqtt:6XX
COAP:7XX
业务不相干服务器错误:5xx
```
#10.COAP支持
>[COAP 可参考这个文章](https://blog.csdn.net/QQ576494799/article/details/77865415?utm_source=blogxgwz2)

>新版本增加了COAP支持,入口地址:`coap://host:5683/api_coap_1_0 `下面是一个Java客户端POST demo：

```java
class TestClient {
    public static void main(String[] args) throws URISyntaxException {
        URI uri = new URI("localhost:5683/api_coap_1_0");
        CoapClient client = new CoapClient(uri);
        CoapResponse response1 = client.post("{\"data\":{\"data\":{\"V1\":\"1\",\"V2\":\"2\"},\"persistent\":\"true\",\"info\":\"V\"},\"clientId\":\"clientID\",\"type\":\"data\"}", MediaTypeRegistry.APPLICATION_JSON);
        System.out.println(Utils.prettyPrint(response)); 
    }
}
```
>注意：COAP目前支持POST且数据格式必须是JSON格式：
>其中：clientID就是客户端的ClientID字段,data是自定义数据键值对，persistent是否开启持久化，info为额外信息

```json
{
    "data":{
        "data":{
            "V1":"1",
            "V2":"2"
        },
        "persistent":"true",
        "info":"V"
    },
    "clientId":"clientID",
    "type":"data"
}
```
 >GET 通过URL传递参数:```coap://localhost:5683/api_coap_1_0?clientId=XXX&data={JSON格式}.....``` Demo:
 
 ```JAVA
class TestClient {
    public static void main(String[] args) throws URISyntaxException {
        //GET 通过URL传递参数
        URI uri = new URI("coap://localhost:5683/api_coap_1_0?clientId=XXX");
        CoapClient client = new CoapClient(uri);
        CoapResponse response2 = client.get();
        System.out.println(Utils.prettyPrint(response2));
        
    }
}
```
>注意：推荐POST形式提交数据，因为GET的时候，无法提交特殊符号会引起异常

>上述是Java的一个Demo，如果有硬件需求请自行百度资料，这里有个ESP8266的库以供参考:[ESP8266 COAP 库](https://github.com/automote/ESP-CoAP)

# 11.SDK开发指南
>考虑到实际项目中需要开发SDK，在这里给出几个简单的DEMO.SDK开发核心思路就是不同的语言对MQTT客户端的封装。
>只要连接进服务器，就可以随意实现消息响应部分的代码。

## 1.Arduino demo 
>[库在这里](https://github.com/knolleary/pubsubclient)

```cfml
#include <SPI.h>
#include <Ethernet.h>
#include <PubSubClient.h>
//配置网络网卡信息，请上官网去看相关资料
byte mac[]    = {  0xDE, 0xED, 0xBA, 0xFE, 0xFE, 0xED };
byte server[] = { 192, 168, 168, 78 }; // MQTT服务地址
byte ip[]     = { 192, 168, 168, 250 }; // 设备IP，通常是动态获取
//核心响应函数，所有收到消息的处理都在这里完成
void callback(char* topic, byte* payload, unsigned int length) {
  // 在这里处理消息回调
}
EthernetClient ethClient;
PubSubClient client(server, 1883, callback, ethClient);
void setup()
{
  Ethernet.begin(mac, ip);
  if (client.connect("客户端的clientID")) {
    //或者client.connect (clientID, username, password)
    client.subscribe("/1542359901679/fe6f6b2081994054978586c5eb42b71f/test");
    client.publish("客户端的Topic","JSON格式的数据串");
  }
}
void loop()
{
  client.loop();
}
```
## 2.Python demo
>[库在这里](https://pypi.org/project/paho-mqtt/1.1/)，或者直接运行:`pip3 install paho-mqtt`.

```play
import paho.mqtt.client as mqtt
import time
import json
def on_connect(client, userdata, flags, rc):
    print("Connected with result code "+str(rc))
    client.subscribe("/1542359901679/fe6f6b2081994054978586c5eb42b71f/test")
    for i in range(3):
        print("Insert:",i)
        client.publish("/1542359901679/fe6f6b2081994054978586c5eb42b71f/test","{\"data\":{\"data\":{\"cmd\":\"time\"}},\"type\":\"cmd\"}")
#消息处理
def on_message(client, userdata, msg):
    print(msg.topic+" "+str(msg.payload))
#
client = mqtt.Client("fe6f6b2081994054978586c5eb42b71f")
client.username_pw_set("6532460fe1734a5e9b7b86eb6deb5a91", "d6a449b35f214d15b3980a7e157edcb5")
client.on_connect = on_connect
client.on_message = on_message
client.connect("localhost", 1884, 60)
client.loop_forever()
```
## 3.mqtt.js Demo
>库在这里<script src="https://cdnjs.cloudflare.com/ajax/libs/paho-mqtt/1.0.1/mqttws31.min.js" type="text/javascript"></script>  
>文档在这里:[Mqtt.js](https://www.eclipse.org/paho/clients/js/#)  
>总结：SDK开发的核心思想就是实现MQTT客户端连接和消息响应函数。

# 12.EasyWebFrameWork简介
>EasyWebFrameWork是我在开发EasyLinker的时候，积累的一些经验，包括SpringSecurity，JWT，SpringBoot等技术的业务层的封装.
>下面大致讲一下开发准则和基本的约束，常见API等等。

## 1.缓存系统
>缓存其实就是对Redis做了业务层封装
```
    /**
     * 使用redis缓存连接信息
     *
     * @param key
     * @throws Exception
     */

    public void set(String key, String value) ;

    /**
     * 删除redis缓存
     *
     * @param key
     */
    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    /**
     * 获取Redis缓存的Info
     *
     * @param key
     * @return
     * @throws Exception
     */
    public String get(String key) ;

    /**
     * 设置过期时间的K V
     *
     * @param key
     * @param value
     * @param time  过期时间，后面的参数是单位
     */

    public void setExpires(String key, String value, Long time, TimeUnit timeUnit);
```
## 2.安全认证
>安全认证是基于Security和JWT做的 

>`@JwtAuthRole`:该注解作用于类上，检查当前请求是否是注解标记的权限：@JwtAuthRole(roles = {"ROLE_ADMIN","ROLE_XXXXXX"})


```
@JwtAuthRole(roles = {"ROLE_ADMIN"})
@RestController
@RequestMapping(value = "/api/v_1_0/client")
public class ClientController {
.....
}

```

>`@JwtAuthRole`跟用户的授权方法是相关的：


```
    public Collection<? extends GrantedAuthority> getAuthorities() {
        /**
         * 在這裏進行授权
         * 默认给了一个普通用户
         */
        List<SimpleGrantedAuthority> simpleGrantedAuthorities = new ArrayList<>();
        for (String role : getRoles())
            simpleGrantedAuthorities.add(new SimpleGrantedAuthority(role));
        return simpleGrantedAuthorities;

    }
```