package by.pzmandroid.mac.repository

import by.pzmandroid.mac.utils.*

object CredRepository {

    private var credits: Credits? = null

    fun getCredits(): Credits {
        credits?.let {
            return it
        }
        return Credits(MQTT_SERVER_URI, MQTT_USERNAME, MQTT_PWD, MQTT_CLIENT_ID, MQTT_TOPIC_ROOT)
    }

    fun initCredits(server: String, login: String, pwd: String, clientId: String, topic: String) {
        credits = Credits(server, login, pwd, clientId, topic)
    }

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

}