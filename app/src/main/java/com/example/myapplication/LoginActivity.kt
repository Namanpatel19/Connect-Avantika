package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
        auth = FirebaseAuth.getInstance()

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("436310790892-equdtc1pmen3brt8400a2q34oqsa2jrp.apps.googleusercontent.com")
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        // Check if user is already logged in
        auth.currentUser?.let {
            startMainActivity()
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // For testing: allow dummy login
            if (email == "naman.patel@avantika.edu.in" && password == "12345678") {
                startMainActivity()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        startMainActivity()
                    } else {
                        // If Firebase fails, we still allow entry for UI testing as requested
                        Log.e("LoginActivity", "Login failed", task.exception)
                        Toast.makeText(this, "Firebase Login failed. Entering as guest...", Toast.LENGTH_SHORT).show()
                        startMainActivity()
                    }
                }
        }

        binding.btnGoogleLogin.setOnClickListener {
            signInWithGoogle()
        }
    }

    private val googleSignInLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    Log.e("LoginActivity", "Google sign in failed", e)
                    Toast.makeText(this, "Google sign in failed. Entering as guest...", Toast.LENGTH_SHORT).show()
                    startMainActivity()
                }
            } else {
                // If user cancels or it fails, allow entering for UI testing
                startMainActivity()
            }
        }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startMainActivity()
                } else {
                    Toast.makeText(this, "Auth Failed. Entering as guest...", Toast.LENGTH_SHORT).show()
                    startMainActivity()
                }
            }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}