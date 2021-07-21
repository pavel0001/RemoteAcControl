package by.valtorn.remoteaccontrol.ui.root.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import by.valtorn.remoteaccontrol.R
import by.valtorn.remoteaccontrol.databinding.FragmentRootBinding
import by.valtorn.remoteaccontrol.model.AcFan
import by.valtorn.remoteaccontrol.model.AcMode
import by.valtorn.remoteaccontrol.ui.root.vm.RootVM
import by.valtorn.remoteaccontrol.utils.DEBUG_TAG
import com.bumptech.glide.Glide

class RootFragment : Fragment() {

    private val viewModel by viewModels<RootVM>()
    private val binding by viewBinding(FragmentRootBinding::bind)

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
            frTempSliderValue.text = getString(R.string.root_temperature_for_selector, 17)

            frTempSlider.addOnChangeListener { _, value, _ ->
                frTempSliderValue.text = getString(R.string.root_temperature_for_selector, value.toInt())
                viewModel.selectTemp(value.toInt())
            }
            frTempProgress.spin()
            frApply.setOnClickListener {
                viewModel.sendCmd()
            }
            frTurbo.setOnClickListener {
                viewModel.turbo()
            }
            frAcToggle.setOnClickListener {
                viewModel.acTogglePower()
            }

            frFanSlider.addOnChangeListener { _, value, _ ->
                when (value.toInt()) {
                    0 -> AcFan.Auto
                    1 -> AcFan.Low
                    2 -> AcFan.Med
                    3 -> AcFan.High
                    4 -> AcFan.Eco
                    else -> null
                }?.let {
                    frFanSliderValue.text = when (value.toInt()) {
                        0 -> getString(R.string.fan_auto)
                        1 -> getString(R.string.fan_low)
                        2 -> getString(R.string.fan_mid)
                        3 -> getString(R.string.fan_high)
                        4 -> getString(R.string.fan_eco)
                        else -> String()
                    }
                    viewModel.setFan(it)
                }
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
                when (it.toInt()) {
                    0 -> getString(R.string.fan_auto)
                    1 -> getString(R.string.fan_low)
                    2 -> getString(R.string.fan_mid)
                    3 -> getString(R.string.fan_high)
                    4 -> getString(R.string.fan_eco)
                    else -> String()
                }
            }
        }
    }

    private fun initVM(activity: FragmentActivity) {
        with(binding) {
            viewModel.mqttProgress.observe(viewLifecycleOwner) {
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
            viewModel.currentAcState.observe(viewLifecycleOwner) {
                if (it.power == 1) Glide.with(activity).load(ContextCompat.getDrawable(activity, R.drawable.ic_power_on)).into(frAcToggle)
                else Glide.with(activity).load(ContextCompat.getDrawable(activity, R.drawable.ic_power_off)).into(frAcToggle)
                Log.i(DEBUG_TAG, "receive current state $it")
            }
            viewModel.currentState.observe(viewLifecycleOwner) {
                Log.i(DEBUG_TAG, "cmd state $it")
            }
        }
    }
}