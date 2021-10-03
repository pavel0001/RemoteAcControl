package by.pzmandroid.mac.ui.notconnected.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import by.pzmandroid.mac.R
import by.pzmandroid.mac.databinding.FragmentNotConnectedBinding
import by.pzmandroid.mac.utils.extensions.safelyNavigate

class NotConnectedFragment : Fragment(R.layout.fragment_not_connected) {

    private val binding by viewBinding(FragmentNotConnectedBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        with(binding) {
            fncSettings.setOnClickListener {
                findNavController().safelyNavigate(NotConnectedFragmentDirections.toSettings())
            }
            fncRefresh.setOnClickListener {

            }
        }
    }
}