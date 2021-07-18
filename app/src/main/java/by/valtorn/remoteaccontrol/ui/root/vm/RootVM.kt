package by.valtorn.remoteaccontrol.ui.root.vm

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import by.valtorn.remoteaccontrol.repository.MqttRepository
import by.valtorn.remoteaccontrol.utils.AC_MODE_COOL
import by.valtorn.remoteaccontrol.utils.AC_MODE_HEAT
import by.valtorn.remoteaccontrol.utils.AcMode
import by.valtorn.remoteaccontrol.utils.tempForAc

class RootVM : ViewModel() {

    val mqttProgress = MqttRepository.mqttProgress
    val receivedMessage = MqttRepository.receivedMessage
    val connectResult = MqttRepository.connectResult

    private val _currentAcMode = MutableLiveData(AcModeWithTemp(AcMode.AC_MODE_COOL, 19))
    val currentAcMode: LiveData<AcModeWithTemp> = _currentAcMode

    fun initMqtt(context: Context) {
        MqttRepository.initializeAndConnect(context)
    }

    fun acOn() {
        MqttRepository.acOn()
    }

    fun acOff() {
        MqttRepository.acOff()
    }

    fun selectMode(mode: AcMode) {
        _currentAcMode.value = _currentAcMode.value?.copy(mode = mode)
    }

    fun selectTemp(temp: Int) {
        if (tempForAc.contains(temp)) {
            _currentAcMode.value = _currentAcMode.value?.copy(temp = temp)
        }
    }

    fun applyCmd() {
        currentAcMode.value?.let { mode ->
            when (mode.mode.str) {
                AC_MODE_COOL -> {
                    MqttRepository.acTempCool(mode.temp)
                }
                AC_MODE_HEAT -> {
                    MqttRepository.acTempHeat(mode.temp)
                }
                else -> {
                    MqttRepository.acMode(mode.mode)
                }
            }
        }
    }

    fun reConnect(context: Context) {
        MqttRepository.reconect(context)
    }

    data class AcModeWithTemp(val mode: AcMode, val temp: Int)

}