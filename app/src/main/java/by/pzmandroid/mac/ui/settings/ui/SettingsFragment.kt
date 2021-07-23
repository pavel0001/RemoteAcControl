package by.pzmandroid.mac.ui.settings.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import by.pzmandroid.mac.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}