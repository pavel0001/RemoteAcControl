package by.valtorn.remoteaccontrol.ui.root.vm

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import by.valtorn.remoteaccontrol.model.AcMode
import by.valtorn.remoteaccontrol.model.AcTurbo
import by.valtorn.remoteaccontrol.repository.CmdRepository
import by.valtorn.remoteaccontrol.repository.MqttRepository
import by.valtorn.remoteaccontrol.utils.DEBUG_TAG

class RootVM : ViewModel() {

    val mqttProgress = MqttRepository.mqttProgress
    val receivedMessage = MqttRepository.receivedMessage
    val publishResult = MqttRepository.publishResult
    val currentAcState = MqttRepository.currentAcState

    private val currentState = CmdRepository.currentState

    fun initMqtt(context: Context) {
        MqttRepository.initializeAndConnect(context)
    }

    fun acTogglePower() {
        CmdRepository.togglePower()
        sendCmd()
    }

    fun selectMode(mode: AcMode) {
        CmdRepository.setMode(mode)
    }

    fun selectTemp(temp: Int) {
        CmdRepository.setTemp(temp)
    }

    fun sendCmd() {
        Log.i(DEBUG_TAG, "sending sendCmd ViewModel")
        MqttRepository.sendJsonCmd(CmdRepository.getJson())
    }

    fun turbo() {
        CmdRepository.setTurbo(AcTurbo.ON)
        sendCmd()
    }

    fun checkConnection() {
        MqttRepository.reconnect()
    }

}