# EasyLinkerV3 开发者手册
## 1.EasyLinkerV3概述
>EasyLinkerV3是继V2版本以后发布的全新版本，和V2相比，使用了Activemq作为消息中间件，全部采用了全新的Java生态体系，很适合Java系列的应用使用。同时Activemq和V2的EMQ相比，社区活跃，资料众多，对开发者很友好，研发成本大大下降。因为核心业务层用了SpringBoot开发，所以附带了与生俱来的分布式特点，可以根据不同的需求灵活的选择部署环境，是否集群或者分布式。同时数据库采用了MongoDB，支持Redis缓存，环境支持Docker一键部署，开箱即用，简化了安装成本，非常适合快速搭建物联网应用平台。
V3是一个全新的尝试，希望给各位物联网开发者，创客，和爱好者带来不一样的理念和体验。
## 2.EasyLinker设计架构
>SpringBoot负责所有的核心业务逻辑，包括用户系统，权限管理等等。Activemq则相当于是消息路由器，负责终端和服务器的消息交互。
```
                                           +------------------+
                                           |                  |
                 +-------------------------+ 客 户 端          +-------------------------------+
                 |                         |                  |                               |
                 |                         +-----------+------+                               |
                 |                                     |                                      |  Mqtt协 议
                 | POST/GET data                       |                                      |
                 |                                     |                                      |
                 |                                     |                                      |
                 |                                     |                                      |
                 |                                     | WEB实 时 推 送                        |
                 |                                     |                                      |
                 |                                     |                                      |
                 |                                     |                                      |
                 |                                     |                                      |
                 ^                                     ^                                      ^
         +-------+----------+              +-----------+----------+                +----------+---------+
         |  HTTP API        |              |                      |                |                    |
         |                  |              | WebSocketAPI         |                |  MQTT Service      |
         |                  |              |                      |                |         ^          |
         +--------+---------+              +----------+-----------+                +--------------------+
                  |                                   |                                      |
                  |                                   |                                      |
                  |                                   |                                      |
                  |                                   |                                      |
                  |                                   |                                      |
                  |                                   |                                      |
                  |                                   |                                      |
                  |                                   |                                      |
                  |                                   |                                      |
                  |                                   |                                      |
+-----------------+-----------------------------------+--------------------------------------+-------------------------+
|                                     EasyLinkerV3                                                                     |
|                                                                                                                      |
|                                                                                                                      |
|                                                                                                                      |
|                                                                                                                      |
|      +------------------------------------------------------------+                  +------------------+            |
|      |  用 户 管 理           || 认 证            ||  客 户 端 管 理 |                 |                   |            |
|      |                       ||                 ||                |                 |                   |            |
|      +------------------------------------------------------------+  <-------------+ |   Redis          |            |
|      |------------------------------------------------------------|   +----------->  |                  |            |
|      |------------------------------------------------------------|                  |                  |            |
|      ||                                                          ||                  +------------------+            |
|      ||                                                          ||                                                  |
|      ||                Activemq                                  ||                                                  |
|      ||                                                          ||                                                  |
|      ||                                                          ||                                                  |
|      +------------------------------------------------------------+                   +-----------------+            |
|      |                                                            |                   |                 |            |
|      |                                                            | +-------------->  |                 |            |
|      |                  SpringBoot                                |                   |    MongoDB      |            |
|      |                                                            |  <--------------+ |                 |            |
|      +------------------------------------------------------------+                   +-----------------+            |
|                                                                                                                      |
+----------------------------------------------------------------------------------------------------------------------+

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
>回显消息,Topic:/system/echo
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
>特殊命令消息,Topic:/system/cmd
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
## 8.常见的端口
 ```
 mqtt :1884
 tcp:61613
 websocket:2500/web-socket/
 ```
 
## 9.异常代码
```
用户:1XX
安全:2XX
API:3XX
Token:4XX
Mqtt:6XX
业务不相干服务器错误:5xx
```
