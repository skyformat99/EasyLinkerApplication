import paho.mqtt.client as mqtt
import json

def on_connect(client, userdata, flags, rc):
    client.subscribe("/1541429280945/2312098c6dce4b8ca43382a2775a7289/test")
    print("Connected with result code "+str(rc))
    client.publish("/1541429280945/2312098c6dce4b8ca43382a2775a7289/test","123123123")


def on_message(client, userdata, msg):
    print("topic:",msg.topic,"msg:",msg.payload)

def on_subscribe(client, userdata, mid, granted_qos):
    print("granted_qos:",granted_qos)


if __name__ == '__main__':

    client = mqtt.Client("1231")
    client.username_pw_set("24cf9099ffcd4fa78d21d54345e712a5", "9ea9445d273446eebdd5810fe8d04b73")
    client.on_connect = on_connect
    client.on_message = on_message
    client.on_subscribe=on_subscribe
    client.connect("127.0.0.1", 1884, 60)
    client.loop_forever()

