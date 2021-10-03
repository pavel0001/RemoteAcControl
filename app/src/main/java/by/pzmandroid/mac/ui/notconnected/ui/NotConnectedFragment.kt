package by.pzmandroid.mac.ui.notconnected.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
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
            initUI()
            initVM(it)
        }
    }

    private fun initUI() {
        with(binding) {
            fncSettings.setOnClickListener {
                findNavController().safelyNavigate(NotConnectedFragmentDirections.toSettings())
            }
            fncRefresh.setOnClickListener {
                viewModel.reconnect()
            }
        }
    }

    private fun initVM(activity: FragmentActivity) {
        viewModel.connectionState.observe(viewLifecycleOwner) { connectionState ->
            connectionState.getIfPending()?.let {
                if (it == MqttRepository.ConnectionState.CONNECTED) {
                    findNavController().safelyNavigate(NotConnectedFragmentDirections.toRoot())
                }
            }
        }
    }
}