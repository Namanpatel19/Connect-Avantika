package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.card.MaterialCardView

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val logoCard = findViewById<MaterialCardView>(R.id.logo_card)
        val tvName   = findViewById<TextView>(R.id.tvAppName)
        val tvTag    = findViewById<TextView>(R.id.tvTagline)
        val tvFooter = findViewById<TextView>(R.id.tvFooter)
        val flashView = findViewById<View>(R.id.flash_view)
        
        // 1. Initial State
        logoCard.scaleX = 0.5f
        logoCard.scaleY = 0.5f
        logoCard.alpha = 0f
        tvName.alpha = 0f
        tvTag.alpha = 0f
        tvFooter.alpha = 0f

        // 2. Entrance Animations
        logoCard.animate()
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(1000)
            .setStartDelay(200)
            .setInterpolator(AnticipateOvershootInterpolator())
            .start()

        tvName.animate()
            .alpha(1f)
            .setDuration(800)
            .setStartDelay(800)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .start()

        tvTag.animate()
            .alpha(1f)
            .setDuration(800)
            .setStartDelay(1100)
            .start()

        tvFooter.animate()
            .alpha(1f)
            .setDuration(800)
            .setStartDelay(1600)
            .start()

        // 3. Smooth Transition Sequence
        Handler(Looper.getMainLooper()).postDelayed({
            
            // Phase A: Rapid zoom-out of content
            val zoomOutDuration = 600L
            logoCard.animate()
                .scaleX(8f)
                .scaleY(8f)
                .alpha(0f)
                .setDuration(zoomOutDuration)
                .setInterpolator(AccelerateInterpolator())
                .start()
                
            tvName.animate()
                .scaleX(3f)
                .scaleY(3f)
                .alpha(0f)
                .setDuration(zoomOutDuration)
                .start()
                
            tvTag.animate()
                .scaleX(3f)
                .scaleY(3f)
                .alpha(0f)
                .setDuration(zoomOutDuration)
                .start()

            // Phase B: White Flash overlay
            flashView.animate()
                .alpha(1f)
                .setDuration(500)
                .setStartDelay(100) // Start slightly after zoom begins
                .withEndAction {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    
                    // Use a seamless fade transition
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    finish()
                }
                .start()
                
        }, 3000)
    }
}
