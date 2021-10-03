package by.pzmandroid.mac.ui.notconnected.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import by.pzmandroid.mac.repository.MqttRepository

class NotConnectedVM : ViewModel() {
    val connectionState = MqttRepository.connectionState

    fun reconnect(context: Context){
        MqttRepository.initializeAndConnect(context)
    }
}