package by.pzmandroid.mac.ui.settings.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import by.kirich1409.viewbindingdelegate.viewBinding
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
            loadSettings(it)
            initUI(it)
            initVM(it)
        }
    }

    private fun initUI(activity: FragmentActivity) {
        with(binding) {
            fsTestConnection.setOnClickListener {
                if (fsServerText.text.isNullOrEmpty() || fsTopicText.text.isNullOrEmpty()) {
                    Toast.makeText(activity, "Server empty", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                viewModel.testConnection(
                    activity,
                    fsServerText.text.toString(),
                    fsClientText.text.toString(),
                    fsUserText.text.toString(),
                    fsPwdText.text.toString()
                )
            }
            fsPresetDemo.setOnClickListener {
                loadDemoPreset()
            }
            fsApply.setOnClickListener {
                if (fsServerText.text.isNullOrEmpty() || fsTopicText.text.isNullOrEmpty()) {
                    Toast.makeText(activity, "Server empty", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                saveSettings(activity)
                findNavController().popBackStack()
            }
        }
    }

    private fun loadSettings(activity: FragmentActivity) {
        with(binding) {
            PreferenceManager.getDefaultSharedPreferences(activity).let {
                fsServerText.setText(it.getString(PREFERENCE_KEY_SERVER, String()))
                fsUserText.setText(it.getString(PREFERENCE_KEY_USER, String()))
                fsPwdText.setText(it.getString(PREFERENCE_KEY_PWD, String()))
                fsClientText.setText(it.getString(PREFERENCE_KEY_CLIENT, String()))
                fsTopicText.setText(it.getString(PREFERENCE_KEY_TOPIC, String()))
            }
        }
    }

    private fun saveSettings(activity: FragmentActivity) {
        with(binding) {
            PreferenceManager.getDefaultSharedPreferences(activity).edit().let {
                it.putString(PREFERENCE_KEY_SERVER, fsServerText.text.toString())
                it.putString(PREFERENCE_KEY_USER, fsUserText.text.toString())
                it.putString(PREFERENCE_KEY_PWD, fsPwdText.text.toString())
                it.putString(PREFERENCE_KEY_CLIENT, fsClientText.text.toString())
                it.putString(PREFERENCE_KEY_TOPIC, fsTopicText.text.toString())
            }
        }
    }

    private fun loadDemoPreset() {
        with(binding) {
            fsServerText.setText(MQTT_SERVER_URI)
            fsUserText.setText(MQTT_USERNAME)
            fsPwdText.setText(MQTT_PWD)
            fsClientText.setText(MQTT_CLIENT_ID)
            fsTopicText.setText(MQTT_TOPIC_ROOT)
        }
    }

    private fun initVM(activity: FragmentActivity) {
        with(binding) {
            viewModel.progress.observe(viewLifecycleOwner) {
                Log.i(DEBUG_TAG, "progress test $it")
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