package com.example.myapplication

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
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

        val unselectedColor = "#4D5D78".toColorInt()
        val selectedIconColor = ContextCompat.getColor(this, R.color.primary)
        val selectedTextColor = Color.BLACK

        roleViews.forEach { (layout, icon, text) ->
            if (layout == selectedView) {
                layout.setBackgroundResource(R.drawable.bg_role_selector_selected)
                ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(selectedIconColor))
                text.setTextColor(selectedTextColor)
            } else {
                layout.setBackgroundResource(R.drawable.bg_role_selector)
                ImageViewCompat.setImageTintList(icon, ColorStateList.valueOf(unselectedColor))
                text.setTextColor(unselectedColor)
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
                if (userId != null) {
                    val role = repository.getUserRole(userId)

                    if (role == selectedRole || (selectedRole == "dean" && role == "super_admin")) {
                        navigateToDashboard(role ?: selectedRole, userId)
                    } else {
                        Toast.makeText(this@LoginActivity, "Incorrect role selected: expected $selectedRole, found $role", Toast.LENGTH_SHORT).show()
                        SupabaseClient.client.auth.signOut()
                    }
                }
            } catch (e: Exception) {
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
