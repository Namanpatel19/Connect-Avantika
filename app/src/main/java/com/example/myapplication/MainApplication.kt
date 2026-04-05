package com.example.myapplication

import android.app.Application
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainApplication : Application() {
    private val ONESIGNAL_APP_ID = "fa04dbc2-3bcb-4fe7-adc1-9205d5669056"

    override fun onCreate() {
        super.onCreate()

        // Verbose Logging helps with debugging OneSignal issues.
        OneSignal.Debug.logLevel = LogLevel.VERBOSE

        // OneSignal Initialization
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID)

        // requestPermission will show the native Android notification permission prompt.
        // NOTE: It's recommended to use a Custom Message Prompt before calling this method
        // to increase your conversion rates: https://documentation.onesignal.com/docs/permission-requests
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(true)
        }
    }
}
