package by.pzmandroid.mac.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.pzmandroid.mac.model.AcMode
import by.pzmandroid.mac.model.AcPower
import by.pzmandroid.mac.model.AcState
import by.pzmandroid.mac.model.AcTurbo
import com.beust.klaxon.Klaxon

object CmdRepository {

    private val mCurrentState = MutableLiveData(AcState())
    val currentState: LiveData<AcState> = mCurrentState

    fun syncWithEsp(stateFromEsp: AcState) {
        mCurrentState.value = stateFromEsp
    }

    fun setMode(mode: AcMode) {
        mCurrentState.value = currentState.value?.copy(mode = mode.value)
    }

    fun setTemp(temp: Int) {
        mCurrentState.value = currentState.value?.copy(temp = temp)
    }

/*    fun setFan(fan: AcFan) {
        mCurrentState.value = currentState.value?.copy(fan = fan.value)
    }*/

    fun togglePower() {
        currentState.value?.let {
            mCurrentState.value = it.copy(
                power = if (it.power == AcPower.OFF.value)
                    AcPower.ON.value
                else
                    AcPower.OFF.value
            )
        }
    }

    fun setTurbo(turbo: AcTurbo) {
        mCurrentState.value = currentState.value?.copy(turbo = turbo.value)
    }

    fun getJson() = Klaxon().toJsonString(currentState.value)

    fun jsonToModel(json: String) = Klaxon().parse<AcState>(json)
}