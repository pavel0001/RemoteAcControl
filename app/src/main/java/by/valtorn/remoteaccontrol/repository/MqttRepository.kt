package by.valtorn.remoteaccontrol.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.valtorn.remoteaccontrol.MQTTClient
import by.valtorn.remoteaccontrol.model.AcState
import by.valtorn.remoteaccontrol.model.SensorResponse
import by.valtorn.remoteaccontrol.repository.CmdRepository.jsonToModel
import by.valtorn.remoteaccontrol.utils.*
import com.beust.klaxon.Klaxon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.*

object MqttRepository {

    private var mqttClient: MQTTClient? = null

    private val mMqttProgress = MutableLiveData(false)
    val mqttProgress: LiveData<Boolean> = mMqttProgress

    private val mPublishResult = MutableLiveData<RequestResult>()
    val publishResult: LiveData<RequestResult> = mPublishResult

    private val mReceivedMessage = MutableLiveData<SensorResponse>()
    val receivedMessage: LiveData<SensorResponse> = mReceivedMessage

    private val mCurrentAcState = MutableLiveData<AcState>()
    val currentAcState: LiveData<AcState> = mCurrentAcState

    private var isConnectRun: Boolean = false

    fun initializeAndConnect(context: Context) {
        mMqttProgress.value = true
        MQTTClient(context).let {
            mqttClient = it
        }
        connect()
        mMqttProgress.value = false
    }

    fun connect() {
        mqttClient?.let {
            if (isConnectRun) return
            isConnectRun = true
            GlobalScope.launch(Dispatchers.IO) {
                it.connect(cbConnect = connectCallback, cbClient = clientCallback)
                isConnectRun = false
            }
        }
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
        publishCmd(MQTT_TOPIC_JSON_CMD, json)
    }

    private fun subscribe() {
        mqttClient?.subscribe(topic = "$MQTT_TOPIC_ROOT+", qos = 1)
    }

    private val connectCallback = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            subscribe()
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            connect()
        }
    }

    private val clientCallback = object : MqttCallback {
        override fun messageArrived(topic: String?, message: MqttMessage?) {
            when (topic) {
                MQTT_TOPIC_SENSOR -> {
                    Klaxon().parse<SensorResponse>(message.toString())?.let { data ->
                        mReceivedMessage.value = data
                    }
                }
                MQTT_TOPIC_CALLBACK -> {
                    jsonToModel(message.toString())?.let {
                        mCurrentAcState.value = it
                    }
                }
            }
        }

        override fun connectionLost(cause: Throwable?) {
            connect()
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
            mPublishResult.value = RequestResult.SUCCESS

        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            Log.d(DEBUG_TAG, "Failed to publish message to topic")
            mPublishResult.value = RequestResult.FAIL
        }
    }

    enum class RequestResult(val str: String) {
        SUCCESS("Доставлено"),
        FAIL("Ошибка")
    }
}