package by.pzmandroid.mac.ui.root.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import by.pzmandroid.mac.R
import by.pzmandroid.mac.databinding.FragmentRootBinding
import by.pzmandroid.mac.model.AcMode
import by.pzmandroid.mac.model.AcPower
import by.pzmandroid.mac.repository.MqttRepository
import by.pzmandroid.mac.ui.root.vm.RootVM

class RootFragment : Fragment(R.layout.fragment_root) {

    private val viewModel by viewModels<RootVM>()
    private val binding by viewBinding(FragmentRootBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            viewModel.syncWithCurrent()
            initUI()
            initVM(it)
        }
    }

    private fun initUI() {
        with(binding) {
            activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.disconnect()
                }
            })
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
            frDisconnect.setOnClickListener {
                viewModel.disconnect()
            }
            frAcModeCool.setOnClickListener {
                viewModel.selectMode(AcMode.Cool)
            }
            frAcModeAuto.setOnClickListener {
                viewModel.selectMode(AcMode.Auto)
            }
            frAcModeDry.setOnClickListener {
                viewModel.selectMode(AcMode.Dry)
            }
            frAcModeFan.setOnClickListener {
                viewModel.selectMode(AcMode.Fan)
            }
            frAcModeHeat.setOnClickListener {
                viewModel.selectMode(AcMode.Heat)
            }
            frTempSlider.setOnValueChangeListener {
                frTempValue.text = getString(R.string.root_temperature_for_selector, it)
                viewModel.selectTemp(it)
            }
            frSync.setOnClickListener {
                viewModel.syncWithCurrent()
            }
        }
    }

    private fun initVM(activity: FragmentActivity) {
        with(binding) {
            viewModel.syncState.observe(viewLifecycleOwner) {
                togglePowerBtn(it.power == AcPower.ON.value)
                if (frTempSlider.getValue() != it.temp) {
                    frTempSlider.setupSlider(it.temp.toFloat())
                }
                calculateEnabledMode(activity, it.mode)
            }

            viewModel.syncError.observe(viewLifecycleOwner) { syncError ->
                syncError.getIfPending()?.let {
                    if (it == MqttRepository.RequestResult.FAIL) {
                        Toast.makeText(activity, getString(R.string.root_sync_error), Toast.LENGTH_SHORT).show()
                    }
                }
            }

            viewModel.connectResult.observe(viewLifecycleOwner) { connectResult ->
                connectResult.getIfPending()?.let {
                    if (it != MqttRepository.ConnectionState.CONNECTED) {
                        findNavController().popBackStack()
                    }
                }
            }

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
                if (it.str.isNotEmpty()) {
                    Toast.makeText(activity, it.str, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun togglePowerBtn(isEnabled: Boolean) {
        with(binding) {
            frAcToggle.setImageResource(
                if (isEnabled) {
                    R.drawable.ic_power_on
                } else {
                    R.drawable.ic_power_off
                }
            )
        }
    }

    private fun calculateEnabledMode(context: Context, mode: Int) {
        with(binding) {
            frAcModeCool.putOnMode(context, mode == AcMode.Cool.value)
            frAcModeAuto.putOnMode(context, mode == AcMode.Auto.value)
            frAcModeDry.putOnMode(context, mode == AcMode.Dry.value)
            frAcModeFan.putOnMode(context, mode == AcMode.Fan.value)
            frAcModeHeat.putOnMode(context, mode == AcMode.Heat.value)
        }
    }

    private fun ImageView.putOnMode(context: Context, isEnabled: Boolean) {
        this.drawable.setTint(
            context.getColor(
                if (isEnabled) {
                    R.color.colorModeSelected
                } else {
                    R.color.colorModeNormal
                }
            )
        )
    }
}