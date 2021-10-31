package by.pzmandroid.mac.ui.dev.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import by.pzmandroid.mac.MacApp
import by.pzmandroid.mac.R
import by.pzmandroid.mac.databinding.FragmentDevBinding
import by.pzmandroid.mac.ui.settings.ui.CREDITS_TYPE_DEV

class DevFragment : Fragment(R.layout.fragment_dev) {

    private val binding by viewBinding(FragmentDevBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
    }

    private fun initUI() {
        with(binding) {
            checkCurrentDemoCreditsType()
            fdApply.setOnClickListener {
                when (fdRadioGroup.checkedRadioButtonId) {
                    R.id.fd_dev -> MacApp.instance.setupDemoCredentialsDev()
                    R.id.fd_prod -> MacApp.instance.setupDemoCredentialsProd()
                }
                findNavController().popBackStack()
            }
        }
    }

    private fun checkCurrentDemoCreditsType() {
        with(binding) {
            fdRadioGroup.check(
                when (MacApp.instance.demoCredentialsType) {
                    CREDITS_TYPE_DEV -> {
                        R.id.fd_dev
                    }
                    else -> {
                        R.id.fd_prod
                    }
                }
            )
        }
    }
}