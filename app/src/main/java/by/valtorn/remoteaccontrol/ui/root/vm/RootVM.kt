package by.valtorn.remoteaccontrol.ui.root.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.valtorn.remoteaccontrol.model.AcFan
import by.valtorn.remoteaccontrol.model.AcMode
import by.valtorn.remoteaccontrol.model.AcTurbo
import by.valtorn.remoteaccontrol.repository.CmdRepository
import by.valtorn.remoteaccontrol.repository.MqttRepository
import kotlinx.coroutines.launch

class RootVM : ViewModel() {

    val mqttProgress = MqttRepository.mqttProgress
    val receivedMessage = MqttRepository.receivedMessage
    val publishResult = MqttRepository.publishResult
    val currentAcState = MqttRepository.currentAcState

    val currentState = CmdRepository.currentState

    fun initMqtt(context: Context) {
        MqttRepository.initializeAndConnect(context)
    }

    fun acTogglePower() {
        CmdRepository.togglePower()
        sendCmd()
    }

    fun setFan(fan: AcFan) {
        CmdRepository.setFan(fan)
    }

    fun selectMode(mode: AcMode) {
        CmdRepository.setMode(mode)
    }

    fun selectTemp(temp: Int) {
        CmdRepository.setTemp(temp)
    }

    fun sendCmd() {
        viewModelScope.launch{
            MqttRepository.sendJsonCmd(CmdRepository.getJson())
        }
    }

    fun turbo() {
        CmdRepository.setTurbo(AcTurbo.ON)
        sendCmd()
    }

    fun checkConnection() {
        MqttRepository.connect()
    }

}