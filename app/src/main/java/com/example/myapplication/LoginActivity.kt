package com.example.myapplication

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
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

        setupRoleSelection()
        setupClickListeners()
        
        // Initial UI state
        updateRoleUI(binding.roleFaculty)
        
        checkSession()
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
                        Toast.makeText(this, "Super Admin Mode Enabled", Toast.LENGTH_SHORT).show()
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
                    text.text = "Admin"
                }
            } else {
                layout.setBackgroundResource(R.drawable.bg_role_tile_default)
                ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(defaultIconColor))
                text.setTextColor(defaultTextColor)
                if (layout == binding.roleAdmin) text.text = "Admin"
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
    }

    private fun loginUser(emailStr: String, passwordStr: String) {
        lifecycleScope.launch {
            try {
                binding.btnLogin.isEnabled = false
                
                SupabaseClient.client.auth.signInWith(Email) {
                    email = emailStr
                    password = passwordStr
                }

                val userId = SupabaseClient.client.auth.currentUserOrNull()?.id
                Log.d("LoginActivity", "Logged in userId: $userId")
                
                if (userId != null) {
                    val role = repository.getUserRole(userId)
                    Log.d("LoginActivity", "Database role for $userId: $role")

                    if (role == selectedRole || (selectedRole == "dean" && role == "super_admin") || (selectedRole == "super_admin" && role == "super_admin")) {
                        navigateToDashboard(role ?: selectedRole, userId)
                    } else {
                        Toast.makeText(this@LoginActivity, "Incorrect role selected: expected $selectedRole, found $role", Toast.LENGTH_SHORT).show()
                        SupabaseClient.client.auth.signOut()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "User ID is null after login", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("LoginActivity", "Login Error: ${e.message}", e)
                Toast.makeText(this@LoginActivity, "Login Failed: ${e.message}", Toast.LENGTH_LONG).show()
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
                    // Stay on login
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
