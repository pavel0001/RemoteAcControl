package by.valtorn.remoteaccontrol.utils

const val DEBUG_TAG = "MyTagForMQTT"
const val MQTT_SERVER_URI = "tcp://test.mosquitto.org:1883"
const val MQTT_CLIENT_ID = ""
const val MQTT_USERNAME = ""
const val MQTT_PWD = ""
const val MQTT_TOPIC_ROOT = "esp32/myowndemo/"

const val MQTT_TOPIC_SENSOR = MQTT_TOPIC_ROOT + "sensor"
const val MQTT_TOPIC_CALLBACK = MQTT_TOPIC_ROOT + "callback"

const val MQTT_TOPIC_JSON_CMD = MQTT_TOPIC_ROOT + "json"