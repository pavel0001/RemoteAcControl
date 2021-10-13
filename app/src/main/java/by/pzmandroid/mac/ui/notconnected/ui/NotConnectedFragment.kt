package by.pzmandroid.mac.ui.notconnected.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import by.pzmandroid.mac.MacApp
import by.pzmandroid.mac.R
import by.pzmandroid.mac.databinding.FragmentNotConnectedBinding
import by.pzmandroid.mac.repository.MqttRepository
import by.pzmandroid.mac.ui.notconnected.vm.NotConnectedVM
import by.pzmandroid.mac.utils.extensions.safelyNavigate

class NotConnectedFragment : Fragment(R.layout.fragment_not_connected) {

    private val binding by viewBinding(FragmentNotConnectedBinding::bind)
    private val viewModel by viewModels<NotConnectedVM>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            initUI(it)
            initVM(it)
        }
    }

    private fun initUI(activity: FragmentActivity) {
        with(binding) {
            fncSettings.setOnClickListener {
                findNavController().safelyNavigate(NotConnectedFragmentDirections.toSettings())
            }
            fncRefresh.setOnClickListener {
                viewModel.reconnect(activity)
            }
        }
    }

    private fun initVM(activity: FragmentActivity) {
        with(binding) {
            viewModel.connectionState.observe(viewLifecycleOwner) { connectionState ->
                connectionState.getIfPending()?.let {
                    if (it == MqttRepository.ConnectionState.CONNECTED) {
                        findNavController().safelyNavigate(NotConnectedFragmentDirections.toRoot())
                    }
                }
            }
            viewModel.progress.observe(viewLifecycleOwner) {
                fncProgress.isVisible = it
                fncProgress.spin()
                fncRefresh.isEnabled = !it
                fncRefresh.text = getString(
                    if (it) {
                        R.string.not_connected_progress
                    } else {
                        R.string.not_connected_refresh
                    }
                )
            }
            viewModel.connectResult.observe(viewLifecycleOwner) { connectResult ->
                connectResult.getIfPending()?.let {
                    if (it == MqttRepository.RequestResult.FAIL)
                        Toast.makeText(activity, it.str, Toast.LENGTH_SHORT).show()
                }
            }
            MacApp.instance.apply {
                if (firstNotConnectedRun) {
                    setNotConnectedRun(false)
                    viewModel.reconnect(activity)
                }
            }
        }
    }
}