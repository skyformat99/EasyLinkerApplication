import paho.mqtt.client as mqtt
import json

def on_connect(client, userdata, flags, rc):
    client.subscribe("/1542359901679/fe6f6b2081994054978586c5eb42b71f/test")
    print("Connected with result code "+str(rc))
    client.publish("/1542359901679/fe6f6b2081994054978586c5eb42b71f/test","123123123")


def on_message(client, userdata, msg):
    print("topic:",msg.topic,"msg:",msg.payload)

def on_subscribe(client, userdata, mid, granted_qos):
    print("granted_qos:",granted_qos)


if __name__ == '__main__':

    client = mqtt.Client("fe6f6b2081994054978586c5eb42b71f")
    client.username_pw_set("6532460fe1734a5e9b7b86eb6deb5a91", "d6a449b35f214d15b3980a7e157edcb5")
    client.on_connect = on_connect
    client.on_message = on_message
    client.on_subscribe=on_subscribe
    client.connect("127.0.0.1", 1884, 60)
    client.loop_forever()

