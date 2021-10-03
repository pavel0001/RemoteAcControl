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
import by.pzmandroid.mac.repository.MqttRepository
import by.pzmandroid.mac.ui.settings.vm.SettingVM
import by.pzmandroid.mac.utils.*


class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding by viewBinding(FragmentSettingsBinding::bind)
    private val viewModel by viewModels<SettingVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            loadSettings()
            initUI(it)
            initVM(it)
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
                findNavController().popBackStack()
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
        with(binding) {
            fsServerText.setText(MQTT_SERVER_URI)
            fsLoginText.setText(MQTT_LOGIN)
            fsPwdText.setText(MQTT_PWD)
            fsClientText.setText(MQTT_CLIENT_ID)
            fsTopicText.setText(MQTT_TOPIC_ROOT)
        }
    }

    private fun initVM(activity: FragmentActivity) {
        with(binding) {
            viewModel.disconnectMqtt()
            viewModel.progress.observe(viewLifecycleOwner) {
                fsProgress.updateProgressState(it)
            }
            viewModel.result.observe(viewLifecycleOwner) {
                Toast.makeText(
                    activity, if (it == MqttRepository.RequestResult.SUCCESS) {
                        "Соединение установлено"
                    } else {
                        "Ошибка"
                    }, Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}