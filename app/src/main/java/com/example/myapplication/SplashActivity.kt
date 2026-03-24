package com.example.myapplication

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val splashDurationMs = 3500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Immersive mode
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        startEnhancedOpeningSequence()
    }

    private fun startEnhancedOpeningSequence() {
        // 1. Rings Expansion Animation (Moving Outside)
        val ringsExpansion = ValueAnimator.ofFloat(1.0f, 4.5f).apply {
            duration = 2000
            interpolator = AccelerateInterpolator(1.5f)
            addUpdateListener { animator ->
                binding.magicRingsView.expansion = animator.animatedValue as Float
            }
        }

        // 2. Rings Fade Out as they move away
        val ringsFadeOut = ValueAnimator.ofFloat(1.0f, 0f).apply {
            duration = 800
            startDelay = 1400
            addUpdateListener { animator ->
                binding.magicRingsView.opacity = animator.animatedValue as Float
            }
        }

        // 3. Logo Appearance (Scale and Alpha)
        val logoAlpha = ObjectAnimator.ofFloat(binding.appLogo, View.ALPHA, 0f, 1f).apply {
            duration = 1000
            startDelay = 500
        }
        val logoScaleX = ObjectAnimator.ofFloat(binding.appLogo, View.SCALE_X, 0.5f, 1.0f).apply {
            duration = 1200
            startDelay = 500
            interpolator = DecelerateInterpolator()
        }
        val logoScaleY = ObjectAnimator.ofFloat(binding.appLogo, View.SCALE_Y, 0.5f, 1.0f).apply {
            duration = 1200
            startDelay = 500
            interpolator = DecelerateInterpolator()
        }

        // 4. Text Appearance
        val titleAlpha = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 0f, 1f).apply {
            duration = 800
            startDelay = 1200
        }
        val taglineAlpha = ObjectAnimator.ofFloat(binding.tvTagline, View.ALPHA, 0f, 1f).apply {
            duration = 800
            startDelay = 1400
        }

        // Combine all animations
        AnimatorSet().apply {
            playTogether(
                ringsExpansion,
                ringsFadeOut,
                logoAlpha,
                logoScaleX,
                logoScaleY,
                titleAlpha,
                taglineAlpha
            )
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // Navigate to Login (handled in MainActivity)
                    navigateToMain()
                }
            })
            start()
        }
    }

    private fun navigateToMain() {
        if (!isFinishing) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }
}
