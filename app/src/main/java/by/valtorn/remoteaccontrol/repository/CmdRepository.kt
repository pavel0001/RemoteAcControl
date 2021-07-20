package by.valtorn.remoteaccontrol.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import by.valtorn.remoteaccontrol.model.AcMode
import by.valtorn.remoteaccontrol.model.AcPower
import by.valtorn.remoteaccontrol.model.AcState
import by.valtorn.remoteaccontrol.model.AcTurbo
import com.beust.klaxon.Klaxon
import com.beust.klaxon.PropertyStrategy
import kotlin.reflect.KProperty

object CmdRepository {

    private val mCurrentState = MutableLiveData(AcState())
    val currentState: LiveData<AcState> = mCurrentState

    fun setMode(mode: AcMode) {
        mCurrentState.value = currentState.value?.copy(mode = mode.value)
    }

    fun setTemp(temp: Int) {
        mCurrentState.value = currentState.value?.copy(temp = temp)
    }

    fun setPower(power: AcPower) {
        mCurrentState.value = currentState.value?.copy(power = power.value)
    }

    fun setTurbo(turbo: AcTurbo) {
        mCurrentState.value = currentState.value?.copy(turbo = turbo.value)
    }

    val ps = object : PropertyStrategy {
        override fun accept(property: KProperty<*>) = property.name != "something"
    }

    fun getJson() = Klaxon().toJsonString(currentState.value)

    fun jsonToModel(json: String) = Klaxon().parse<AcState>(json)
}