package by.valtorn.remoteaccontrol.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.valtorn.remoteaccontrol.model.*
import com.beust.klaxon.Klaxon

object CmdRepository {

    private val mCurrentState = MutableLiveData(AcState())
    val currentState: LiveData<AcState> = mCurrentState

    fun setMode(mode: AcMode) {
        mCurrentState.value = currentState.value?.copy(mode = mode.value)
    }

    fun setTemp(temp: Int) {
        mCurrentState.value = currentState.value?.copy(temp = temp)
    }

/*    fun setPower(power: AcPower) {
        mCurrentState.value = currentState.value?.copy(power = power.value)
    }*/

    fun setFan(fan: AcFan) {
        mCurrentState.value = currentState.value?.copy(fan = fan.value)
    }

    fun togglePower() {
        currentState.value?.let {
            mCurrentState.value = currentState.value?.copy(
                power =
                if (it.power == AcPower.OFF.value)
                    AcPower.ON.value
                else
                    AcPower.OFF.value
            )
        }
    }

    fun setTurbo(turbo: AcTurbo) {
        mCurrentState.value = currentState.value?.copy(turbo = turbo.value)
    }

    suspend fun getJson() = Klaxon().toJsonString(currentState.value)

    fun jsonToModel(json: String) = Klaxon().parse<AcState>(json)
}