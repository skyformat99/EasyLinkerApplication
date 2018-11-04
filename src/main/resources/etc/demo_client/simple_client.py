import threading
import paho.mqtt.client as mqtt
import time
import subprocess
import json


class SDK(threading.Thread):

    # SDK 1.2 直接用一个Key连接
    def __init__(self, host, port, key, on_message, add_group=False):
        super(SDK, self).__init__()
        self.host = host  # 服务器的IP
        self.port = port  # 服务器的MQTT端口
        self.add_group = add_group  # 是否监听分组
        self.key = key  # 连接KEY
        self.client = mqtt.Client(self.key.split("-")[2])
        self.client.username_pw_set(
            self.key.split("-")[2],
            self.key.split("-")[2])
        self.client.on_message = on_message
        self.client.on_connect = self.on_connect
        self.client.on_disconnect = self.on_disconnect

    def run(self):
        self.client.connect(self.host, self.port, 60)
        self.client.loop_forever()

    # 消息回显
    def echo(self, data):
        self.client.publish(
            "IN/ECHO/" + self.key.split("-")[0] + "/" +
            self.key.split("-")[1] + "/" + self.key.split("-")[2], str(data))

    # 发布消息
    def publish(self, data):
        self.client.publish(
            "IN/DEVICE/" + self.key.split("-")[0] + "/" +
            self.key.split("-")[1] + "/" + self.key.split("-")[2], str(data))

    def updateLocation(self, data):
        self.client.publish("IN/LOCATION/" + self.key.split("-")[2], str(data))

    def on_disconnect(self, a, b, c):
        print("已断开连接,状态码:", c)

    def on_connect(self, c, userdata, flags, rc):
        if rc == 0:
            self.client.subscribe(
                "OUT/DEVICE/" + self.key.split("-")[0] + "/" +
                self.key.split("-")[1] + "/" + self.key.split("-")[2])
            if (self.add_group):
                self.client.subscribe("OUT/DEVICE/" + self.key.split("-")[0] +
                                      "/" + self.key.split("-")[1])
                print("开启分组监听", "OUT/DEVICE/" + self.key.split("-")[0] + "/" +
                      self.key.split("-")[1])
            print("连接成功!")
        elif rc == 1:
            print("连接失败!MQTT协议错误!")
            self.client.disconnect()
            exit(1)
        elif rc == 2:
            print("连接失败!非法客户端标识!")
            self.client.disconnect()
            exit(1)
        elif rc == 3:
            print("连接失败!服务器访问失败!")
            self.client.disconnect()
        elif rc == 4:
            print("连接失败!账户或者密码错误!")
            self.client.disconnect()
            exit(1)
        elif rc == 5:
            print("连接失败!认证失败!")
            self.client.disconnect()
            exit(1)
        else:
            self.client.disconnect()
            exit(1)


'''
配置SDK
'''


def on_message(client, userdata, msg):
    print("Received Data:", msg.payload)


if __name__ == "__main__":
    sdk = SDK("localhost", 1883, "1527344369841-1527344393672-1527344358402",
              on_message, True)
    sdk.start()
    # 数据上传
    while 1:
        time.sleep(10)
        # echo表示 给web控制台返回一个值
        sdk.echo("Echo text")
        # publish 发布数据
        sdk.publish(data={"k": "2"})
        # 更新位置
        sdk.updateLocation({
            "latitude": "N39°54′6.74″",
            "longitude": "E116°23′29.52″",
            "locationDescribe": "我到河北省",
            "mode":"persistent"
        })
