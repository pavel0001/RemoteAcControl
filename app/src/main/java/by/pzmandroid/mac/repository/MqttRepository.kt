package by.pzmandroid.mac.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.pzmandroid.mac.MQTTClient
import by.pzmandroid.mac.model.AcState
import by.pzmandroid.mac.model.SensorResponse
import by.pzmandroid.mac.repository.CmdRepository.jsonToModel
import by.pzmandroid.mac.utils.DEBUG_TAG
import com.beust.klaxon.Klaxon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.*

object MqttRepository {

    private var mqttTestClient: MQTTClient? = null

    private val mMqttTestProgress = MutableLiveData(false)
    val mqttTestProgress: LiveData<Boolean> = mMqttTestProgress

    private val mMqttTestResult = MutableLiveData<RequestResult>()
    val mqttTestResult: LiveData<RequestResult> = mMqttTestResult

    private var mqttClient: MQTTClient? = null

    lateinit var credits: CredRepository.Credits

    private val mMqttProgress = MutableLiveData(false)
    val mqttProgress: LiveData<Boolean> = mMqttProgress

    private val mPublishResult = MutableLiveData<RequestResult>()
    val publishResult: LiveData<RequestResult> = mPublishResult

    private val mConnectResult = MutableLiveData<RequestResult>()
    val connectResult: LiveData<RequestResult> = mConnectResult

    private val mReceivedMessage = MutableLiveData<SensorResponse>()
    val receivedMessage: LiveData<SensorResponse> = mReceivedMessage

    private val mCurrentAcState = MutableLiveData<AcState>()
    val currentAcState: LiveData<AcState> = mCurrentAcState

    private var isConnectRun: Boolean = false

    fun initializeAndConnect(context: Context) {
        credits = CredRepository.getCredits()
        mMqttProgress.value = true
        MQTTClient(context, credits.server, credits.clientId).let {
            mqttClient = it
        }
        connect()
        mMqttProgress.value = false
    }

    fun testConnection(
        context: Context,
        server: String,
        clientId: String,
        user: String,
        pwd: String
    ) {
        credits = CredRepository.getCredits()
        mMqttTestProgress.value = true
        MQTTClient(context, server, clientId).let {
            mqttTestClient = it
        }
        testConnect(user, pwd)
        mMqttTestProgress.value = false
    }

    private fun testConnect(user: String, pwd: String) {
        mqttTestClient?.let {
            if (it.isConnected()) return
            if (isConnectRun) return
            isConnectRun = true
            GlobalScope.launch(Dispatchers.IO) {
                it.connect(
                    username = user,
                    password = pwd,
                    cbConnect = testConnectCallback
                )
                isConnectRun = false
            }
        }
    }

    fun connect() {
        mqttClient?.let {
            if (it.isConnected()) return
            if (isConnectRun) return
            isConnectRun = true
            GlobalScope.launch(Dispatchers.IO) {
                it.connect(
                    username = credits.login,
                    password = credits.pwd,
                    cbConnect = connectCallback,
                    cbClient = clientCallback
                )
                isConnectRun = false
            }
        }
    }

    private fun publishCmd(cmd: String) {
        mMqttProgress.value = true
        mqttClient?.publish(
            topic = credits.MQTT_TOPIC_JSON_CMD,
            msg = cmd,
            qos = 1,
            cbPublish = publishCallback
        )
        Log.i(DEBUG_TAG, "publishCmd topic ${credits.MQTT_TOPIC_JSON_CMD} cmd = $cmd")
    }

    fun sendJsonCmd(json: String) {
        Log.i(DEBUG_TAG, "sending json $json")
        publishCmd(json)
    }

    private fun subscribe() {
        mqttClient?.subscribe(topic = "${credits.topic}+", qos = 1)
    }

    private val connectCallback = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            mConnectResult.value = RequestResult.SUCCESS
            subscribe()
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            mConnectResult.value = RequestResult.FAIL
            Log.i(DEBUG_TAG, "fail connect")
        }
    }

    private val testConnectCallback = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            mMqttTestResult.value = RequestResult.SUCCESS

            mqttTestClient?.let {
                if (it.isConnected())
                    it.disconnect()
            }
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            mMqttTestResult.value = RequestResult.FAIL
        }
    }

    private val clientCallback = object : MqttCallback {
        override fun messageArrived(topic: String?, message: MqttMessage?) {
            when (topic) {
                credits.MQTT_TOPIC_SENSOR -> {
                    Klaxon().parse<SensorResponse>(message.toString())?.let { data ->
                        mReceivedMessage.value = data
                    }
                }
                credits.MQTT_TOPIC_CALLBACK -> {
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