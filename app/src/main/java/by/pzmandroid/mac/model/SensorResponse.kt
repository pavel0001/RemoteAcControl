package by.pzmandroid.mac.model

data class SensorResponse(val temperature: Float, val pressure: Float, val altitude: Double) {
    fun getPressureMm() = pressure.div(133.322).toInt()
}
