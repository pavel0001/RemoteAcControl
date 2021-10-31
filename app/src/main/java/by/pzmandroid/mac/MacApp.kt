package by.pzmandroid.mac

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import by.pzmandroid.mac.model.Credits
import by.pzmandroid.mac.ui.settings.ui.CREDITS_TYPE_DEV
import by.pzmandroid.mac.ui.settings.ui.CREDITS_TYPE_PROD
import by.pzmandroid.mac.utils.*
import timber.log.Timber

class MacApp : Application() {

    private val credServer = "credServer"
    private val credLogin = "credLogin"
    private val credPwd = "credPwd"
    private val credClientId = "credClientId"
    private val credTopic = "credTopic"

    private val sensorStateKey = "sensorState"

    var firstNotConnectedRun = true
        private set

    var credentials: Credits? = null
        private set

    var sensorState: Boolean = false
        private set

    private val demoCredentialsString = "demoCredentialsString"

    var demoCredentialsType: Int? = null
        private set

    companion object {
        lateinit var instance: MacApp
            private set
    }

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        sharedPreferences = getSharedPreferences("MacAppPreferences", Context.MODE_PRIVATE)

        loadDemoCredentialsType()
        loadCred()
        loadSensorState()
        instance = this
    }


    fun saveCredits(server: String, login: String, pwd: String, clientId: String, topic: String) {
        sharedPreferences.edit {
            putString(credServer, server)
            putString(credLogin, login)
            putString(credPwd, pwd)
            putString(credClientId, clientId)
            putString(credTopic, topic)
        }
        loadCred()
    }

    fun saveSensorState(state: Boolean) {
        sharedPreferences.edit {
            putBoolean(sensorStateKey, state)
        }
        loadSensorState()
    }

    private fun loadSensorState() {
        sensorState = sharedPreferences.getBoolean(sensorStateKey, false)
    }

    private fun loadCred() {
        credentials = Credits(
            server = sharedPreferences.getString(credServer, MQTT_SERVER_URI)!!,
            login = sharedPreferences.getString(credLogin, MQTT_LOGIN)!!,
            pwd = sharedPreferences.getString(credPwd, MQTT_PWD)!!,
            clientId = sharedPreferences.getString(credClientId, MQTT_CLIENT_ID)!!,
            topic = sharedPreferences.getString(credTopic, MQTT_TOPIC_ROOT)!!
        )
    }

    fun setNotConnectedRun(isItFirstConnect: Boolean) {
        firstNotConnectedRun = isItFirstConnect
    }

    fun setupDemoCredentialsDev() {
        sharedPreferences.edit {
            putInt(demoCredentialsString, CREDITS_TYPE_DEV)
        }
        loadDemoCredentialsType()
    }

    fun setupDemoCredentialsProd() {
        sharedPreferences.edit {
            putInt(demoCredentialsString, CREDITS_TYPE_PROD)
        }
        loadDemoCredentialsType()
    }

    private fun loadDemoCredentialsType() {
        demoCredentialsType = sharedPreferences.getInt(demoCredentialsString, 0)
    }
}