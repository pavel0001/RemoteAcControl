package by.pzmandroid.mac.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.pzmandroid.mac.MQTTClient
import by.pzmandroid.mac.MacApp
import by.pzmandroid.mac.model.AcState
import by.pzmandroid.mac.model.Credits
import by.pzmandroid.mac.model.SensorResponse
import by.pzmandroid.mac.repository.CmdRepository.jsonToModel
import by.pzmandroid.mac.utils.Event
import com.beust.klaxon.Klaxon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.*
import timber.log.Timber

object MqttRepository {

    private var mqttClient: MQTTClient? = null

    lateinit var credits: Credits

    private val mConnectionState = MutableLiveData(Event(ConnectionState.DISCONNECTED))
    val connectionState: LiveData<Event<ConnectionState>> = mConnectionState

    private val mMqttProgress = MutableLiveData(false)
    val mqttProgress: LiveData<Boolean> = mMqttProgress

    private val mPublishResult = MutableLiveData<RequestResult>()
    val publishResult: LiveData<RequestResult> = mPublishResult

    private val mConnectResult = MutableLiveData<Event<RequestResult>>()
    val connectResult: LiveData<Event<RequestResult>> = mConnectResult

    private val mReceivedMessage = MutableLiveData<SensorResponse>()
    val receivedMessage: LiveData<SensorResponse> = mReceivedMessage

    private val mCurrentAcState = MutableLiveData<AcState>()
    val currentAcState: LiveData<AcState> = mCurrentAcState

    private var isConnectRun: Boolean = false

    fun initializeAndConnect(context: Context) {
        Timber.d("init mqtt")
        refreshCredits()
        mMqttProgress.value = true
        mqttClient?.close()
        MQTTClient(context, credits.server, credits.clientId).let {
            mqttClient = it
        }
        connect()
    }

    private fun refreshCredits() {
        Timber.d("refreshCredits")
        MacApp.instance.credentials?.let {
            credits = it
        }
    }

    private fun connect() {
        mqttClient?.let {
            if (it.isConnected() || isConnectRun) {
                mMqttProgress.value = false
                return
            }
            isConnectRun = true
            GlobalScope.launch(Dispatchers.IO) {
                it.connect(
                    username = credits.login,
                    password = credits.pwd,
                    cbConnect = connectCallback,
                    cbClient = clientCallback
                )
            }
        }
    }

    fun disconnect() {
        mqttClient?.let {
            if (it.isConnected())
                it.disconnect()
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
        Timber.d("publishCmd topic ${credits.MQTT_TOPIC_JSON_CMD} cmd = $cmd")
    }

    fun sendJsonCmd(json: String) {
        Timber.d("sending json $json")
        publishCmd(json)
    }

    private fun subscribe() {
        mqttClient?.subscribe(topic = "${credits.topic}+", qos = 1)
    }

    private val connectCallback = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            Timber.d("connectCallback onSuccess $asyncActionToken")
            mMqttProgress.value = false
            isConnectRun = false
            mConnectResult.value = Event(RequestResult.SUCCESS)
            mConnectionState.value = Event(ConnectionState.CONNECTED)
            subscribe()
        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            mMqttProgress.value = false
            isConnectRun = false
            mConnectResult.value = Event(
                RequestResult.FAIL.also { it.str = exception.toString() }
            )
            mConnectionState.value = Event(ConnectionState.DISCONNECTED)
            Timber.d("fail connect $asyncActionToken")
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
            mConnectionState.value = Event(ConnectionState.DISCONNECTED)
            Timber.d("Connection lost ${cause.toString()}")
        }

        override fun deliveryComplete(token: IMqttDeliveryToken?) {
            Timber.d("Delivery completed")
            mMqttProgress.value = false
        }
    }

    private val publishCallback = object : IMqttActionListener {
        override fun onSuccess(asyncActionToken: IMqttToken?) {
            Timber.d("Message published to topic")
            mPublishResult.value = RequestResult.SUCCESS

        }

        override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
            Timber.d("Failed to publish message to topic")
            mPublishResult.value = RequestResult.FAIL
        }
    }

    enum class RequestResult(var str: String) {
        SUCCESS("Доставлено"),
        FAIL("Ошибка")
    }

    enum class ConnectionState {
        CONNECTED,
        DISCONNECTED
    }
}