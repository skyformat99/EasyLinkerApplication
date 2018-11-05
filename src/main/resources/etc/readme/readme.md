# 本项目的二次开发标准
## 1 基本原则
```$xslt
1.保持主干的完整
二次开发的时候,切记不要删除模块,发欧泽会引发异常;
2.增而不改
当发现某个功能满足不了的时候,不要常识去在 原来的代码上更新,应该自己新建文件或者分支进行迭代;
3.随时更新
遵守以上原则以后,就可以随时更新代码了.
```

## 2 模块划分
```$xslt
模块名字写的很通俗易懂,这里不做赘述.
```

## 3 命名规则
```$xslt
1.规则
见名知意:看到名字就知道含义,格式采用驼峰法
2.特殊组件
DAO:一般就是和Model对应的DAO模块,比如UserDAO.
Service:和DAO对应起来,UserService
Service的方法规则:查询单个对象用findXXX,而获取UI显示的数据集用getXXX;
例如:目前需要判断一个use是否存在：
User user=UserService.findById(0);
if(!user)--->>>操作;
如果需要生成JSON，用getXXX这种形式.
获取当前用户的数据:
Jsonobject json=UserService.getById(0)
此时是JSON数据,可以直接传给前端.

```
## 4 代码目录结构
```$xslt
1.main:该包下是全部源码
2.test：test
3.resource：资源
该目录下的文件不要删除
4.etc:一些附带的资源文件，供参考
```