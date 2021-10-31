package by.pzmandroid.mac.ui.root.vm

import androidx.lifecycle.*
import by.pzmandroid.mac.model.AcMode
import by.pzmandroid.mac.model.AcState
import by.pzmandroid.mac.model.AcTurbo
import by.pzmandroid.mac.repository.CmdRepository
import by.pzmandroid.mac.repository.MqttRepository
import by.pzmandroid.mac.utils.Event
import kotlinx.coroutines.launch

class RootVM : ViewModel() {

    val mqttProgress = MqttRepository.mqttProgress
    val receivedSensorState = MqttRepository.receivedSensorState
    val publishResult = MqttRepository.publishResult

    val connectResult = MqttRepository.connectionState

    private val currentAcStateFromEsp = MqttRepository.currentAcState

    val currentAcStateFromMobile = CmdRepository.currentState

    val needToSync = MediatorLiveData<Boolean>().apply {
        addSource(currentAcStateFromEsp) {
            value = mergeNeedToShowSyncBadge(
                stateFromMobile = currentAcStateFromMobile.value,
                stateFromEsp = it.peek()
            )
        }
        addSource(currentAcStateFromMobile) {
            value = mergeNeedToShowSyncBadge(
                stateFromMobile = it,
                stateFromEsp = currentAcStateFromEsp.value?.peek()
            )
        }
    }

    private fun mergeNeedToShowSyncBadge(stateFromMobile: AcState?, stateFromEsp: AcState?) = stateFromEsp != stateFromMobile && stateFromEsp != null

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
        needToSync.value?.let {
            mSyncProgress.value = true
            currentAcStateFromEsp.value?.peek()?.let { stateFromEsp ->
                CmdRepository.syncWithEsp(stateFromEsp)
            }
            mSyncProgress.value = false
        }
    }

    fun disconnect() {
        MqttRepository.disconnect()
    }
}