package com.example.myapplication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logo     = findViewById<ImageView>(R.id.logo)
        val tvName   = findViewById<TextView>(R.id.tvAppName)
        val tvTag    = findViewById<TextView>(R.id.tvTagline)

        // Logo: scale + fade in with bounce
        val logoAnim = AnimationUtils.loadAnimation(this, R.anim.logo_anim)
        logo.startAnimation(logoAnim)
        logo.animate().alpha(1f).setDuration(700).setStartDelay(0).start()

        // App name: fade + slide up (delayed)
        tvName.animate()
            .alpha(1f)
            .translationYBy(-20f)
            .setDuration(600)
            .setStartDelay(500)
            .setInterpolator(DecelerateInterpolator())
            .start()

        // Tagline: fade in (more delayed)
        tvTag.animate()
            .alpha(1f)
            .setDuration(500)
            .setStartDelay(800)
            .start()

        // Navigate to Login after 2.5 s
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 2500)
    }
}
