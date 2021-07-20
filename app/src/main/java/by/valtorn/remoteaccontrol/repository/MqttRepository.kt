package by.valtorn.remoteaccontrol.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.valtorn.remoteaccontrol.MQTTClient
import by.valtorn.remoteaccontrol.utils.DEBUG_TAG
import by.valtorn.remoteaccontrol.utils.MQTT_TOPIC_JSON_CMD
import by.valtorn.remoteaccontrol.utils.MQTT_TOPIC_ROOT
import org.eclipse.paho.client.mqttv3.*

object MqttRepository {

    private var mqttClient: MQTTClient? = null

    private val mMqttProgress = MutableLiveData(false)
    val mqttProgress: LiveData<Boolean> = mMqttProgress

    private val mPublishResult = MutableLiveData<RequestResult>()
    val publishResult: LiveData<RequestResult> = mPublishResult

    private val mReceivedMessage = MutableLiveData<MessageMqtt>()
    val receivedMessage: LiveData<MessageMqtt> = mReceivedMessage

    fun initializeAndConnect(context: Context) {
        mMqttProgress.value = true
        MQTTClient(context).let {
            mqttClient = it
            it.connect(cbConnect = connectCallback, cbClient = clientCallback)
        }
        mMqttProgress.value = false
    }

    private fun publishCmd(topic: String, cmd: String) {
        mMqttProgress.value = true
        mqttClient?.publish(
            topic = topic,
            msg = cmd,
            qos = 1,
            cbPublish = publishCallback
        )
        Log.i(DEBUG_TAG, "publishCmd topic $topic cmd = $cmd")
    }

    fun sendJsonCmd(json: String) {
        Log.i(DEBUG_TAG, "sending json $json")
        //publishCmd(MQTT_TOPIC_JSON_CMD, json)
    }

    private fun subscribe() {
        mqttClient?.subscribe(topic = MQTT_TOPIC_ROOT, qos = 1)
    }

    fun reconnect() {
        mqttClient?.let {
            Log.d(DEBUG_TAG, "reconnect it.isConnected() = ${it.isConnected()}")
            if (it.isConnected())
                it.connect()
        }
    }

    private val connectCallback = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            subscribe()
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            reconnect()
        }
    }

    private val clientCallback = object : MqttCallback {
        override fun messageArrived(topic: String?, message: MqttMessage?) {
            mReceivedMessage.value = MessageMqtt(topic, message)
        }

        override fun connectionLost(cause: Throwable?) {
            reconnect()
            Log.d(DEBUG_TAG, "Connection lost ${cause.toString()}")
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {
            Log.d(DEBUG_TAG, "Delivery completed")
            mMqttProgress.value = false
        }
    }

    private val publishCallback = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            Log.d(DEBUG_TAG, "Message published to topic")
            mPublishResult.value = RequestResult.SUCCES

        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            Log.d(DEBUG_TAG, "Failed to publish message to topic")
            mPublishResult.value = RequestResult.FAIL
        }
    }

    enum class RequestResult(val str: String) {
        SUCCES("Доставлено"),
        FAIL("Ошибка")
    }

    data class MessageMqtt(val topic: String?, val message: MqttMessage?) {

        fun getTemperatureFloat() = this.message?.payload?.decodeToString()?.toFloat()

        fun getPressureInt() = this.message?.payload?.decodeToString()?.toInt()
    }
}