package by.valtorn.remoteaccontrol.model

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

enum class AcTurbo(val value: Int) {
    OFF(0),
    ON(1)
}

enum class AcPower(val value: Int) {
    OFF(0),
    ON(1)
}

enum class AcMode(val value: Int) {
    Auto(0),
    Cool(1),
    Dry(2),
    Fan(3),
    Heat(4)
}

enum class AcFan(val value: Int) {
    Auto(0),
    Low(2),
    Med(3),
    High(4),
    Turbo(6),
    Eco(7)
}

enum class AcSwingV(val value: Int) {
    Off(1),
    Auto(2),
    Highest(3),
    High(4),
    Middle(5),
    Low(6),
    Lowest(7)
}

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