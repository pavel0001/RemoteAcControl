package by.valtorn.remoteaccontrol.ui.root.vm

import android.content.Context
import androidx.lifecycle.ViewModel
import by.valtorn.remoteaccontrol.model.AcMode
import by.valtorn.remoteaccontrol.model.AcPower
import by.valtorn.remoteaccontrol.model.AcTurbo
import by.valtorn.remoteaccontrol.repository.CmdRepository
import by.valtorn.remoteaccontrol.repository.MqttRepository

class RootVM : ViewModel() {

    val mqttProgress = MqttRepository.mqttProgress
    val receivedMessage = MqttRepository.receivedMessage
    val publishResult = MqttRepository.publishResult

    private val currentState = CmdRepository.currentState

    fun initMqtt(context: Context) {
        MqttRepository.initializeAndConnect(context)
    }

    fun acOn() {
        CmdRepository.setPower(AcPower.ON)
    }

    fun acOff() {
        CmdRepository.setPower(AcPower.OFF)
    }

    fun selectMode(mode: AcMode) {
        CmdRepository.setMode(mode)
    }

    fun selectTemp(temp: Int) {
        CmdRepository.setTemp(temp)
    }

    fun sendCmd() {
        MqttRepository.sendJsonCmd(CmdRepository.getJson())
    }

    fun turbo() {
        CmdRepository.setTurbo(AcTurbo.ON)
    }

    fun checkConnection() {
        MqttRepository.reconnect()
    }

}