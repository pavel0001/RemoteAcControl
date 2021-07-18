package by.valtorn.remoteaccontrol.utils

const val DEBUG_TAG = "MyTagForMQTT"
const val MQTT_SERVER_URI = "tcp://test.mosquitto.org:1883"
const val MQTT_CLIENT_ID = ""
const val MQTT_USERNAME = ""
const val MQTT_PWD = ""

const val MQTT_TOPIC_ROOT = "esp32/myowndemo/+"
const val MQTT_TOPIC_TEMPERATURE = "esp32/myowndemo/temperature"
const val MQTT_TOPIC_PRESSURE = "esp32/myowndemo/pressure"
const val MQTT_TOPIC_ALTITUDE = "esp32/myowndemo/altitude"

const val MQTT_TOPIC_AC_RUN = "esp32/myowndemo/ac/run"
const val MQTT_TOPIC_AC_MODE = "esp32/myowndemo/ac/mode"
const val MQTT_TOPIC_AC_TEMP_HEAT = "esp32/myowndemo/ac/temp/heat"
const val MQTT_TOPIC_AC_TEMP_COOL = "esp32/myowndemo/ac/temp/cool"

const val AC_COMMAND_ON = "on"
const val AC_COMMAND_OFF = "off"

const val AC_MODE_COOL = "cool"
const val AC_MODE_HEAT = "heat"
const val AC_MODE_AVTO = "avto"
const val AC_MODE_DRY = "dry"

enum class AcMode(val str: String) {
    AC_MODE_COOL("cool"),
    AC_MODE_HEAT("heat"),
    AC_MODE_AVTO("avto"),
    AC_MODE_DRY("dry")
}

val tempForAc = arrayOf(19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30)