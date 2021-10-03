package by.pzmandroid.mac.model

data class Credits(
    val server: String,
    val login: String,
    val pwd: String,
    val clientId: String,
    val topic: String
) {
    val MQTT_TOPIC_SENSOR = topic + "sensor"
    val MQTT_TOPIC_CALLBACK = topic + "callback"
    val MQTT_TOPIC_JSON_CMD = topic + "json"
}
