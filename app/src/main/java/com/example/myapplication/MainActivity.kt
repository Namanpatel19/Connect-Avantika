package com.example.myapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var appViewModel: AppViewModel

    private val ONESIGNAL_APP_ID = "fa04dbc2-3bcb-4fe7-adc1-9205d5669056"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        // Handle edge-to-edge and system bars overlapping
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top)
            // We don't update bottom padding here because BottomNavigationView handles it or we want it at the very bottom
            insets
        }

        val userRole = intent.getStringExtra("USER_ROLE") ?: "student"
        val userId   = intent.getStringExtra("USER_ID")   ?: ""

        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        appViewModel.userId   = userId
        appViewModel.userRole = userRole

        // Login OneSignal User
        OneSignal.login(userId)

        setupNavigation(userRole)
    }

    private fun setupNavigation(role: String) {
        val (navRes, menuRes) = when (role) {
            "super_admin" -> Pair(R.navigation.nav_super_admin, R.menu.menu_super_admin)
            "faculty"   -> Pair(R.navigation.nav_faculty,   R.menu.menu_faculty)
            "club_head" -> Pair(R.navigation.nav_club_head, R.menu.menu_club_head)
            "dean"      -> Pair(R.navigation.nav_dean,      R.menu.menu_dean)
            else        -> Pair(R.navigation.nav_student,   R.menu.menu_student)
        }

        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHost.navController
        navController.setGraph(navRes)

        binding.bottomNav.menu.clear()
        binding.bottomNav.inflateMenu(menuRes)
        binding.bottomNav.setupWithNavController(navController)
    }

    fun navigateTo(destinationId: Int) {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navHost.navController.navigate(destinationId)
    }

    fun navigateToDeanView() {
        setupNavigation("dean")
    }
}
