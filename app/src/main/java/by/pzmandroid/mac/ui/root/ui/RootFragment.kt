package by.pzmandroid.mac.ui.root.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import by.pzmandroid.mac.R
import by.pzmandroid.mac.databinding.FragmentRootBinding
import by.pzmandroid.mac.repository.MqttRepository
import by.pzmandroid.mac.ui.root.vm.RootVM
import by.pzmandroid.mac.utils.extensions.safelyNavigate

class RootFragment : Fragment(R.layout.fragment_root) {

    private val viewModel by viewModels<RootVM>()
    private val binding by viewBinding(FragmentRootBinding::bind)

    private var powerButtonState = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            //viewModel.initMqtt(it)
            initUI()
            initVM(it)
        }
    }

    private fun initUI() {
        with(binding) {
            frTempProgress.spin()
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
            frSettings.setOnClickListener {
                viewModel.disconnect()
            }
        }
    }

    private fun initVM(activity: FragmentActivity) {
        with(binding) {
            viewModel.connectResult.observe(viewLifecycleOwner) { connectResult ->
                connectResult.getIfPending()?.let {
                    if (it != MqttRepository.ConnectionState.CONNECTED) {
                        findNavController().safelyNavigate(RootFragmentDirections.toNotConnected())
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
                Toast.makeText(activity, it.str, Toast.LENGTH_SHORT).show()
            }

            viewModel.syncState.observe(viewLifecycleOwner) {
                powerButtonState = it.power == 1
                if (it.power == 1) frAcToggle.setImageResource(R.drawable.ic_power_on)
                else frAcToggle.setImageResource(R.drawable.ic_power_off)
            }

        }
    }

    private fun togglePoserBtn() {
        with(binding) {
            powerButtonState = if (powerButtonState) {
                frAcToggle.setImageResource(R.drawable.ic_power_off)
                false
            } else {
                frAcToggle.setImageResource(R.drawable.ic_power_on)
                true
            }
        }
    }
}