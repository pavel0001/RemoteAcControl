package by.pzmandroid.mac.ui.notconnected.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import by.pzmandroid.mac.repository.MqttRepository

class NotConnectedVM : ViewModel() {
    val connectionState = MqttRepository.connectionState
    val progress = MqttRepository.mqttProgress
    val connectResult = MqttRepository.connectResult

    fun reconnect(context: Context) {
        MqttRepository.initializeAndConnect(context)
    }
}