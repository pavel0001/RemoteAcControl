package by.valtorn.remoteaccontrol.ui.root.vm

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import by.valtorn.remoteaccontrol.model.AcFan
import by.valtorn.remoteaccontrol.model.AcMode
import by.valtorn.remoteaccontrol.model.AcState
import by.valtorn.remoteaccontrol.model.AcTurbo
import by.valtorn.remoteaccontrol.repository.CmdRepository
import by.valtorn.remoteaccontrol.repository.MqttRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class RootVM : ViewModel() {

    val mqttProgress = MqttRepository.mqttProgress
    val receivedMessage = MqttRepository.receivedMessage
    val publishResult = MqttRepository.publishResult

    private val currentAcStateFromEsp = MqttRepository.currentAcState

    private val mSyncState = MutableLiveData<AcState>()
    val syncState: LiveData<AcState> = mSyncState

    private val mSyncProgress = MutableLiveData(false)
    val syncProgress: LiveData<Boolean> = mSyncProgress

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
            CmdRepository.currentState.value?.let {
                mSyncState.value = it
            }
            mSyncProgress.value = false
        }
    }

    fun checkConnection() {
        MqttRepository.connect()
        syncWithCurrent()
    }
}