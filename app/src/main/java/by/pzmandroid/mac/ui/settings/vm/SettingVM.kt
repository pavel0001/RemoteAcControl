package by.pzmandroid.mac.ui.settings.vm

import androidx.lifecycle.ViewModel
import by.pzmandroid.mac.repository.MqttRepository

class SettingVM : ViewModel() {
    val progress = MqttRepository.mqttTestProgress
    val result = MqttRepository.mqttTestResult

    fun disconnectMqtt() {
        //MqttRepository.disconnect()
    }
}