package by.pzmandroid.mac.ui.settings.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import by.pzmandroid.mac.MacApp
import by.pzmandroid.mac.R
import by.pzmandroid.mac.databinding.FragmentSettingsBinding
import by.pzmandroid.mac.model.Credits
import by.pzmandroid.mac.ui.settings.vm.SettingVM
import by.pzmandroid.mac.utils.*
import by.pzmandroid.mac.utils.extensions.safelyNavigate

const val CREDITS_TYPE_DEV = 97
const val CREDITS_TYPE_PROD = 12

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding by viewBinding(FragmentSettingsBinding::bind)
    private val viewModel by viewModels<SettingVM>()

    private val demoCredentialsProd = Credits("tcp://test.mosquitto.org:1883", "", "", "", "prod/")
    private val demoCredentialsDev = Credits("tcp://test.mosquitto.org:1883", "", "", "", "demo/")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            loadSettings()
            initUI(it)
            viewModel.disconnectMqtt()
        }
    }

    private fun initUI(activity: FragmentActivity) {
        with(binding) {
            fsPresetDemo.setOnClickListener {
                loadDemoPreset()
            }
            fsApply.setOnClickListener {
                if (fsServerText.text.isNullOrEmpty() || fsTopicText.text.isNullOrEmpty()) {
                    Toast.makeText(activity, "Server empty", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                saveSettings()
                MacApp.instance.setNotConnectedRun(true)
                findNavController().safelyNavigate(SettingsFragmentDirections.toNotConnected())
            }
            fsSensorState.isChecked = MacApp.instance.sensorState
            fsSensorState.setOnCheckedChangeListener { _, isChecked ->
                MacApp.instance.saveSensorState(isChecked)
            }
        }
    }

    private fun loadSettings() {
        with(binding) {
            MacApp.instance.credentials?.let {
                fsServerText.setText(it.server)
                fsLoginText.setText(it.login)
                fsPwdText.setText(it.pwd)
                fsClientText.setText(it.clientId)
                fsTopicText.setText(it.topic)
            }
        }
    }

    private fun saveSettings() {
        with(binding) {
            MacApp.instance.saveCredits(
                server = fsServerText.text.toString(),
                login = fsLoginText.text.toString(),
                pwd = fsPwdText.text.toString(),
                clientId = fsClientText.text.toString(),
                topic = fsTopicText.text.toString()
            )
        }
    }

    private fun loadDemoPreset() {
        when (MacApp.instance.demoCredentialsType) {
            CREDITS_TYPE_DEV -> {
                demoCredentialsDev
            }
            else -> {
                demoCredentialsProd
            }
        }.let {
            with(binding) {
                fsServerText.setText(it.server)
                fsLoginText.setText(it.login)
                fsPwdText.setText(it.pwd)
                fsClientText.setText(it.clientId)
                fsTopicText.setText(it.topic)
            }
        }
    }
}