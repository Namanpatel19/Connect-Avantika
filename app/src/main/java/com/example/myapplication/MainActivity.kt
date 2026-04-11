package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.viewmodel.AppViewModel
import com.onesignal.OneSignal
import com.onesignal.debug.LogLevel
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var appViewModel: AppViewModel
    private var navController: NavController? = null

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
        CoroutineScope(Dispatchers.IO).launch {
            OneSignal.Notifications.requestPermission(true)
        }

        // Handle edge-to-edge and system bars overlapping
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(top = systemBars.top)
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
        navController = navHost.navController
        navController?.setGraph(navRes)

        binding.bottomNav.menu.clear()
        binding.bottomNav.inflateMenu(menuRes)
        
        navController?.let { controller ->
            binding.bottomNav.setOnItemSelectedListener { item ->
                NavigationUI.onNavDestinationSelected(item, controller)
                true
            }
            
            // Re-sync bottom nav state with current destination
            controller.addOnDestinationChangedListener { _, destination, _ ->
                val menu = binding.bottomNav.menu
                for (i in 0 until menu.size()) {
                    val item = menu.getItem(i)
                    if (item.itemId == destination.id) {
                        item.isChecked = true
                    }
                }
            }
        }
    }

    fun navigateTo(destinationId: Int) {
        navController?.navigate(destinationId)
    }

    fun navigateToDeanView() {
        setupNavigation("dean")
    }

    fun logout() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                SupabaseClient.client.auth.signOut()
            } catch (e: Exception) {
                // Ignore
            }
            OneSignal.logout()
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
