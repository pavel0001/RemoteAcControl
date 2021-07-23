package by.pzmandroid.mac.ui.root.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import by.pzmandroid.mac.R
import by.pzmandroid.mac.databinding.FragmentRootBinding
import by.pzmandroid.mac.ui.root.vm.RootVM

class RootFragment : Fragment() {

    private val viewModel by viewModels<RootVM>()
    private val binding by viewBinding(FragmentRootBinding::bind)

    private var powerButtonState = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_root, container, false)
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkConnection()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            viewModel.initMqtt(it)
            initUI()
            initVM(it)
        }
    }

    private fun initUI() {
        with(binding) {
            frTempProgress.spin()
            frTempSliderValue.text = getString(R.string.root_temperature_for_selector, 17)
            frTempSlider.addOnChangeListener { _, value, _ ->
                frTempSliderValue.text = getString(R.string.root_temperature_for_selector, value.toInt())
                viewModel.selectTemp(value.toInt())
            }
            frApply.setOnClickListener {
                viewModel.sendCmd()
            }
            frTurbo.setOnClickListener {
                viewModel.turbo()
            }
            frAcToggle.setOnClickListener {
                viewModel.acTogglePower()
                togglePoserBtn()
            }

            frFanSlider.addOnChangeListener { _, value, _ ->
                frFanSliderValue.text = getString(AcFan.values().first { fan -> fan.numberSlider == value.toInt() }.str)
                viewModel.setFan(AcFan.values().first { it.numberSlider == value.toInt() })
            }

            frBottomMenu.setOnItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.menu_cool -> viewModel.selectMode(AcMode.Cool)
                    R.id.menu_dry -> viewModel.selectMode(AcMode.Dry)
                    R.id.menu_heat -> viewModel.selectMode(AcMode.Heat)
                    R.id.menu_auto -> viewModel.selectMode(AcMode.Auto)
                    R.id.menu_fan -> viewModel.selectMode(AcMode.Fan)
                }
                true
            }
            frFanSlider.setLabelFormatter {
                getString(AcFan.values().first { fan -> fan.numberSlider == it.toInt() }.str)
            }
            frSync.setOnClickListener {
                viewModel.syncWithCurrent()
            }
        }
    }

    private fun initVM(activity: FragmentActivity) {
        with(binding) {
            viewModel.mqttProgress.observe(viewLifecycleOwner) {
                frProgress.updateProgressState(it)
            }

            viewModel.syncProgress.observe(viewLifecycleOwner) {
                frProgress.updateProgressState(it)
            }

            viewModel.receivedMessage.observe(viewLifecycleOwner) { receivedMessage ->
                receivedMessage?.let {
                    frTempProgress.stopSpinning()
                    frTemperature.text =
                        getString(R.string.root_temperature, it.temperature)
                    frPressure.text = getString(R.string.root_pressure, it.getPressureMm())
                    frTemperatureSign.isGone = false
                    frTemperature.isGone = false
                }
            }

            viewModel.publishResult.observe(viewLifecycleOwner) {
                Toast.makeText(activity, it.str, Toast.LENGTH_SHORT).show()
            }

            viewModel.syncState.observe(viewLifecycleOwner) {
                powerButtonState = it.power == 1
                if (it.power == 1) frAcToggle.setImageResource(R.drawable.ic_power_on)
                else frAcToggle.setImageResource(R.drawable.ic_power_off)
                frFanSlider.value = AcFan.values().first { fan -> fan.value == it.fan }.numberSlider.toFloat()
                frTempSlider.value = it.temp.toFloat()
                frBottomMenu.selectedItemId = when (it.mode) {
                    AcMode.Dry.ordinal -> R.id.menu_dry
                    AcMode.Heat.ordinal -> R.id.menu_heat
                    AcMode.Auto.ordinal -> R.id.menu_auto
                    AcMode.Fan.ordinal -> R.id.menu_fan
                    else -> R.id.menu_cool
                }
            }

        }
    }

    private fun togglePoserBtn() {
        with(binding) {
            if (powerButtonState) {
                frAcToggle.setImageResource(R.drawable.ic_power_off)
                powerButtonState = false
            } else {
                frAcToggle.setImageResource(R.drawable.ic_power_on)
                powerButtonState = true
            }
        }
    }
}