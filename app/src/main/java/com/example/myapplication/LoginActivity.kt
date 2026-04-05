package com.example.myapplication

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.repository.MainRepository
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var selectedRole: String = "faculty"
    private val repository = MainRepository()
    private var adminClickCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Prepare for Entrance Animation
        prepareEntranceAnimation()

        setupRoleSelection()
        setupClickListeners()
        
        // Initial UI state
        updateRoleUI(binding.roleFaculty)
        
        checkSession()

        // 2. Start Entrance Animation
        startEntranceAnimation()
    }

    private fun prepareEntranceAnimation() {
        // Hide elements initially
        binding.headerLayout.alpha = 0f
        binding.headerLogoCard.scaleX = 0.5f
        binding.headerLogoCard.scaleY = 0.5f
        
        binding.loginCardLayout.alpha = 0f
        binding.loginCardLayout.translationY = 100f
        
        binding.tvContactAdmin.alpha = 0f
    }

    private fun startEntranceAnimation() {
        // Header fade in
        binding.headerLayout.animate()
            .alpha(1f)
            .setDuration(600)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // Logo pop in
        binding.headerLogoCard.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(800)
            .setStartDelay(200)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        // Login card slide up
        binding.loginCardLayout.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(700)
            .setStartDelay(400)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // Footer fade in
        binding.tvContactAdmin.animate()
            .alpha(1f)
            .setDuration(600)
            .setStartDelay(800)
            .start()
    }

    private fun setupRoleSelection() {
        val roles = mapOf(
            binding.roleAdmin to "dean",
            binding.roleClubLead to "club_head",
            binding.roleFaculty to "faculty",
            binding.roleStudent to "student"
        )

        roles.forEach { (view, role) ->
            view.setOnClickListener {
                if (role == "dean") {
                    adminClickCount++
                    if (adminClickCount >= 5) {
                        selectedRole = "super_admin"
                        Toast.makeText(this, "SUPER ADMIN MODE ACTIVE", Toast.LENGTH_SHORT).show()
                        updateRoleUI(view)
                        return@setOnClickListener
                    }
                } else {
                    adminClickCount = 0
                }
                
                selectedRole = role
                updateRoleUI(view)
            }
        }
    }

    private fun updateRoleUI(selectedView: View) {
        val roleViews = listOf(
            Triple(binding.roleAdmin, binding.ivAdmin, binding.tvAdmin),
            Triple(binding.roleClubLead, binding.ivClubLead, binding.tvClubLead),
            Triple(binding.roleFaculty, binding.ivFaculty, binding.tvFaculty),
            Triple(binding.roleStudent, binding.ivStudent, binding.tvStudent)
        )

        val defaultIconColor = "#4D5D78".toColorInt()
        val defaultTextColor = "#4D5D78".toColorInt()
        val selectedIconColor = ContextCompat.getColor(this, R.color.primary)
        val selectedTextColor = Color.BLACK

        roleViews.forEach { (layout, icon, text) ->
            if (layout == selectedView) {
                layout.setBackgroundResource(R.drawable.bg_role_tile_selected)
                ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(selectedIconColor))
                text.setTextColor(selectedTextColor)
                if (selectedRole == "super_admin") {
                    text.text = "Super Admin"
                } else if (layout == binding.roleAdmin) {
                    text.text = "Dean"
                }
            } else {
                layout.setBackgroundResource(R.drawable.bg_role_tile_default)
                ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(defaultIconColor))
                text.setTextColor(defaultTextColor)
                if (layout == binding.roleAdmin) text.text = "Dean"
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }

        binding.tvForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(emailStr: String, passwordStr: String) {
        lifecycleScope.launch {
            try {
                binding.btnLogin.isEnabled = false
                Log.d("LoginActivity", "Attempting login: $emailStr, Role selected: $selectedRole")
                
                SupabaseClient.client.auth.signInWith(Email) {
                    email = emailStr
                    password = passwordStr
                }

                val userId = SupabaseClient.client.auth.currentUserOrNull()?.id
                Log.d("LoginActivity", "Auth success. UserID: $userId")
                
                if (userId != null) {
                    var dbRole = repository.getUserRole(userId)
                    Log.d("LoginActivity", "Role from DB: $dbRole")

                    // Fallback for hardcoded admin if DB record is missing
                    if (dbRole == null && emailStr == "admin@avantika.edu.in") {
                        dbRole = "super_admin"
                        Log.d("LoginActivity", "Fallback to super_admin for admin email")
                    }

                    val normalizedDbRole = dbRole?.lowercase()?.trim()
                    val normalizedSelectedRole = selectedRole.lowercase().trim()

                    val isSuperAdmin = normalizedDbRole == "super_admin"
                    
                    val canLogin = when (normalizedSelectedRole) {
                        "super_admin" -> isSuperAdmin
                        "dean" -> normalizedDbRole == "dean" || isSuperAdmin
                        else -> normalizedDbRole == normalizedSelectedRole || isSuperAdmin 
                    }

                    if (canLogin) {
                        Log.d("LoginActivity", "Login authorized. Navigating...")
                        navigateToDashboard(dbRole ?: selectedRole, userId)
                    } else {
                        Log.e("LoginActivity", "Role mismatch. DB: $dbRole, UI Selected: $selectedRole")
                        val displayRole = dbRole ?: "unknown"
                        Toast.makeText(this@LoginActivity, "Access Denied: You are registered as '$displayRole', but selected '$selectedRole'", Toast.LENGTH_LONG).show()
                        SupabaseClient.client.auth.signOut()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Login failed: No user found", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("LoginActivity", "Login Exception: ${e.message}", e)
                Toast.makeText(this@LoginActivity, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.btnLogin.isEnabled = true
            }
        }
    }

    private fun checkSession() {
        val user = SupabaseClient.client.auth.currentUserOrNull()
        if (user != null) {
            lifecycleScope.launch {
                try {
                    val role = repository.getUserRole(user.id)
                    role?.let { navigateToDashboard(it, user.id) }
                } catch (e: Exception) {
                    // Session might be invalid or role fetch failed
                }
            }
        }
    }

    private fun navigateToDashboard(role: String, userId: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("USER_ROLE", role)
        intent.putExtra("USER_ID", userId)
        startActivity(intent)
        finish()
    }
}
