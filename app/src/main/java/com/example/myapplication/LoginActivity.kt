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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.databinding.ActivityLoginBinding
import com.example.myapplication.repository.MainRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.onesignal.OneSignal
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var selectedRole: String = "faculty"
    private val repository = MainRepository()
    private var adminClickCount = 0

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
            val idToken = account?.idToken
            val email = account?.email
            Log.d("LoginActivity", "Google Account: $email, IDToken available: ${idToken != null}")
            
            if (idToken != null && email != null) {
                loginWithGoogle(idToken, email)
            } else {
                Toast.makeText(this, "Google ID Token not found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("LoginActivity", "Google sign in failed code: ${e.message}", e)
            Toast.makeText(this, "Google Sign-In failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

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

        binding.btnGoogle.setOnClickListener {
            // Web Client ID from Google Cloud Console / Firebase
            val webClientId = "225378641751-v73hfjt2lio06vto7c2u6r5882utm6df.apps.googleusercontent.com"
            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(webClientId)
                .build()
            val client = GoogleSignIn.getClient(this, gso)
            // Sign out first to ensure account picker shows up
            client.signOut().addOnCompleteListener {
                googleSignInLauncher.launch(client.signInIntent)
            }
        }
    }

    private fun loginWithGoogle(idTokenValue: String, email: String) {
        lifecycleScope.launch {
            try {
                binding.btnGoogle.isEnabled = false
                Log.d("LoginActivity", "Login with Google: $email")
                
                // 1. Check if user exists in our public.users table
                val user = repository.getUserByEmail(email)
                if (user == null) {
                    Toast.makeText(this@LoginActivity, "Account not found. Please contact admin to register your email.", Toast.LENGTH_LONG).show()
                    GoogleSignIn.getClient(this@LoginActivity, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
                    return@launch
                }

                // 2. Sign in with Supabase using IDToken
                SupabaseClient.client.auth.signInWith(IDToken) {
                    idToken = idTokenValue
                    provider = Google
                }

                val userId = SupabaseClient.client.auth.currentUserOrNull()?.id
                if (userId != null) {
                    OneSignal.login(userId)
                    navigateToDashboard(user.role, userId)
                } else {
                    Toast.makeText(this@LoginActivity, "Supabase session failed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("LoginActivity", "Google login error", e)
                Toast.makeText(this@LoginActivity, "Supabase Login failed: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.btnGoogle.isEnabled = true
            }
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
                        "student" -> normalizedDbRole == "student" || normalizedDbRole == "club_head" || isSuperAdmin
                        else -> normalizedDbRole == normalizedSelectedRole || isSuperAdmin 
                    }

                    if (canLogin) {
                        Log.d("LoginActivity", "Login authorized. Navigating...")
                        
                        // OneSignal Login
                        OneSignal.login(userId)
                        
                        // Pass the selected role to dashboard so user gets the expected UI
                        navigateToDashboard(selectedRole, userId)
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
        lifecycleScope.launch {
            val session = SupabaseClient.client.auth.currentSessionOrNull()
            if (session != null) {
                val userId = session.user?.id
                if (userId != null) {
                    val dbRole = repository.getUserRole(userId)
                    if (dbRole != null) {
                        navigateToDashboard(dbRole, userId)
                    }
                }
            }
        }
    }

    private fun navigateToDashboard(role: String, userId: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("USER_ID", userId)
        intent.putExtra("USER_ROLE", role)
        startActivity(intent)
        finish()
    }
}
