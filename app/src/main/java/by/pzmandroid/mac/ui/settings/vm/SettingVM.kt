package by.pzmandroid.mac.ui.settings.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import by.pzmandroid.mac.repository.MqttRepository

class SettingVM : ViewModel() {
    val progress = MqttRepository.mqttTestProgress
    val result = MqttRepository.mqttTestResult

    fun testConnection(
        context: Context,
        server: String,
        clientId: String,
        user: String,
        pwd: String
    ) {
        MqttRepository.testConnection(
            context,
            server,
            clientId,
            user,
            pwd
        )
    }
}