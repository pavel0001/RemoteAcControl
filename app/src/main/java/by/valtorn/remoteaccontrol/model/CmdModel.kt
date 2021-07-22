package by.valtorn.remoteaccontrol.model

import by.valtorn.remoteaccontrol.R

data class AcState(
    val power: Int = AcPower.ON.value,
    val mode: Int = AcMode.Cool.ordinal,
    val temp: Int = 17,
    val fan: Int = AcFan.High.value,
    val swingV: Int = AcSwingV.Auto.value,
    val swingH: Int = AcSwingH.Auto.value,
    val turbo: Int = 0,
    val threeD: Int = 0,
    val clean: Int = 0
)

@Suppress("Unused")
enum class AcTurbo(val value: Int) {
    OFF(0),
    ON(1)
}

@Suppress("Unused")
enum class AcPower(val value: Int) {
    OFF(0),
    ON(1)
}

@Suppress("Unused")
enum class AcMode(val value: Int, val str: Int) {
    Auto(0, R.string.menu_bottom_auto),
    Cool(1, R.string.menu_bottom_cool),
    Dry(2, R.string.menu_bottom_dry),
    Fan(3, R.string.menu_bottom_fan),
    Heat(4, R.string.menu_bottom_heat)
}

@Suppress("Unused")
enum class AcFan(val value: Int, val numberSlider: Int, val str: Int) {
    Auto(0, 0, R.string.fan_auto),
    Low(2, 1, R.string.fan_low),
    Med(3, 2, R.string.fan_mid),
    High(4, 3, R.string.fan_high),
    Turbo(6, 5, R.string.fan_turbo),
    Eco(7, 4, R.string.fan_eco)
}

@Suppress("Unused")
enum class AcSwingV(val value: Int) {
    Off(1),
    Auto(2),
    Highest(3),
    High(4),
    Middle(5),
    Low(6),
    Lowest(7)
}

@Suppress("Unused")
enum class AcSwingH(val value: Int) {
    Off(1),
    Auto(2),
    LeftMax(3),
    Left(4),
    Middle(5),
    Right(6),
    RightMax(7),
    RightLeft(8),
    LeftRight(9),
    ThreeD(10)
}