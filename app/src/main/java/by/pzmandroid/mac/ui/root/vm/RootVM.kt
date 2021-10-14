package by.pzmandroid.mac.ui.root.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.pzmandroid.mac.model.AcMode
import by.pzmandroid.mac.model.AcTurbo
import by.pzmandroid.mac.repository.CmdRepository
import by.pzmandroid.mac.repository.MqttRepository
import by.pzmandroid.mac.utils.Event
import by.pzmandroid.mac.utils.SYNC_TIMEOUT
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

    private val mSyncError = MutableLiveData<Event<MqttRepository.RequestResult>>()
    val syncError: LiveData<Event<MqttRepository.RequestResult>> = mSyncError

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
            var counter = 0
            loop@ while (true) {
                val currentState = currentAcStateFromEsp.value?.getIfPending()
                if (currentState != null) {
                    CmdRepository.syncWithEsp(currentState)
                    break@loop
                }
                if (counter >= SYNC_TIMEOUT) {
                    mSyncError.value = Event(MqttRepository.RequestResult.FAIL)
                    break@loop
                }
                counter++
                delay(1000)
            }
            mSyncProgress.value = false
        }
    }

    fun disconnect() {
        MqttRepository.disconnect()
    }
}