package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.ui.viewmodel.AppViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var appViewModel: AppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userRole = intent.getStringExtra("USER_ROLE") ?: "student"
        val userId   = intent.getStringExtra("USER_ID")   ?: ""

        appViewModel = ViewModelProvider(this)[AppViewModel::class.java]
        appViewModel.userId   = userId
        appViewModel.userRole = userRole

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
        try { navHost.navController.navigate(destinationId) } catch (_: Exception) {}
    }
    
    fun navigateToDeanView() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("USER_ROLE", "dean")
        intent.putExtra("USER_ID", appViewModel.userId)
        startActivity(intent)
        finish()
    }
}
