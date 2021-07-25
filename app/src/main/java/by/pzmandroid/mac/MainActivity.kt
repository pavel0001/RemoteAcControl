package by.pzmandroid.mac

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.preference.PreferenceManager
import by.pzmandroid.mac.repository.CredRepository
import by.pzmandroid.mac.utils.*

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.nav_graph)

        PreferenceManager.getDefaultSharedPreferences(this).let {
            CredRepository.initCredits(
                it.getString("server", MQTT_SERVER_URI).orEmpty(),
                it.getString("login", MQTT_USERNAME).orEmpty(),
                it.getString("pwd", MQTT_PWD).orEmpty(),
                it.getString("clientId", MQTT_CLIENT_ID).orEmpty(),
                it.getString("topic", MQTT_TOPIC_ROOT).orEmpty()
            )
        }
    }
}