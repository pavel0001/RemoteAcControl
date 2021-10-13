package by.pzmandroid.mac.ui.root.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.pzmandroid.mac.model.AcFan
import by.pzmandroid.mac.model.AcMode
import by.pzmandroid.mac.model.AcTurbo
import by.pzmandroid.mac.repository.CmdRepository
import by.pzmandroid.mac.repository.MqttRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RootVM : ViewModel() {

    val mqttProgress = MqttRepository.mqttProgress
    val receivedMessage = MqttRepository.receivedMessage
    val publishResult = MqttRepository.publishResult

    val connectResult = MqttRepository.connectionState

    private val currentAcStateFromEsp = MqttRepository.currentAcState

    val syncState = CmdRepository.currentState

    private val mSyncProgress = MutableLiveData(false)
    val syncProgress: LiveData<Boolean> = mSyncProgress

    fun acTogglePower() {
        CmdRepository.togglePower()
        sendCmd()
    }

/*    fun setFan(fan: AcFan) {
        CmdRepository.setFan(fan)
    }*/

    fun selectMode(mode: AcMode) {
        CmdRepository.setMode(mode)
    }

    fun selectTemp(temp: Int) {
        CmdRepository.setTemp(temp)
    }

    fun sendCmd() {
        viewModelScope.launch {
            MqttRepository.sendJsonCmd(CmdRepository.getJson())
        }
    }

    fun turbo() {
        CmdRepository.setTurbo(AcTurbo.ON)
        sendCmd()
    }

    fun syncWithCurrent() {
        viewModelScope.launch {
            mSyncProgress.value = true
            var flag = true
            while (flag) {
                currentAcStateFromEsp.value?.let {
                    CmdRepository.syncWithEsp(it)
                    flag = false
                }
                delay(1000)
            }
            mSyncProgress.value = false
        }
    }

    fun disconnect() {
        MqttRepository.disconnect()
    }
}