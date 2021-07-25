package by.pzmandroid.mac.utils

const val DEBUG_TAG = "MyTagForMQTT"
const val MQTT_SERVER_URI = "tcp://test.mosquitto.org:1883"
const val MQTT_CLIENT_ID = ""
const val MQTT_USERNAME = ""
const val MQTT_PWD = ""
const val MQTT_TOPIC_ROOT = "esp32/myowndemo/"

const val MQTT_TOPIC_SENSOR = MQTT_TOPIC_ROOT + "sensor"
const val MQTT_TOPIC_CALLBACK = MQTT_TOPIC_ROOT + "callback"

const val MQTT_TOPIC_JSON_CMD = MQTT_TOPIC_ROOT + "json"

const val PREFERENCE_KEY_SERVER = "server"
const val PREFERENCE_KEY_USER = "login"
const val PREFERENCE_KEY_PWD = "pwd"
const val PREFERENCE_KEY_CLIENT = "clientId"
const val PREFERENCE_KEY_TOPIC = "topic"