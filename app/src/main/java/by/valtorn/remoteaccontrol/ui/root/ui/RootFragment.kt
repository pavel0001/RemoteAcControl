package by.valtorn.remoteaccontrol.ui.root.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import by.valtorn.remoteaccontrol.R
import by.valtorn.remoteaccontrol.databinding.FragmentRootBinding
import by.valtorn.remoteaccontrol.ui.root.vm.RootVM
import by.valtorn.remoteaccontrol.utils.AcMode
import by.valtorn.remoteaccontrol.utils.DEBUG_TAG
import by.valtorn.remoteaccontrol.utils.MQTT_TOPIC_TEMPERATURE
import by.valtorn.remoteaccontrol.utils.tempForAc

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
        activity?.let {
            viewModel.reConnect(it)
        }
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
            frOnButton.setOnClickListener {
                viewModel.acOn()
            }
            frOffButton.setOnClickListener {
                viewModel.acOff()
            }
            frApply.setOnClickListener {
                viewModel.applyCmd()
            }

            frAcTemperature.minValue = 1
            frAcTemperature.maxValue = tempForAc.size
            frAcTemperature.displayedValues = tempForAc.map {
                getString(R.string.root_temperature_for_selector, it)
            }.toTypedArray()

            frAcMode.minValue = 1
            frAcMode.maxValue = AcMode.values().size
            frAcMode.displayedValues = AcMode.values().map { it.str }.toTypedArray()

            frAcTemperature.setOnValueChangedListener { _, _, newVal ->
                viewModel.selectTemp(tempForAc[newVal - 1])
            }
            frAcMode.setOnValueChangedListener { _, _, newVal ->
                viewModel.selectMode(AcMode.values()[newVal - 1])
            }
        }
    }

    private fun initVM(activity: FragmentActivity) {
        with(binding) {
            viewModel.mqttProgress.observe(viewLifecycleOwner) {
                frProgress.updateProgressState(it)
            }
            viewModel.receivedMessage.observe(viewLifecycleOwner) {
                when (it.topic) {
                    MQTT_TOPIC_TEMPERATURE -> {
                        frTemperature.isVisible = true
                        frTemperatureSign.isVisible = true
                        frTemperature.text =
                            getString(R.string.root_temperature, it.getTemperatureFloat())
                    }
                    else -> {

                    }
                }
            }
            viewModel.currentAcMode.observe(viewLifecycleOwner) {
                Log.i(DEBUG_TAG, "currentAcMode $it")
            }
        }
    }
}