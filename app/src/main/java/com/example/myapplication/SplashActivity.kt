package com.example.myapplication

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startUltraCoolAnimation()
    }

    private fun startUltraCoolAnimation() {
        // 1. Rings Animation (Expanding and rotating)
        val ring1ScaleX = ObjectAnimator.ofFloat(binding.ring1, View.SCALE_X, 0.5f, 1.2f).apply { duration = 2000 }
        val ring1ScaleY = ObjectAnimator.ofFloat(binding.ring1, View.SCALE_Y, 0.5f, 1.2f).apply { duration = 2000 }
        val ring1Alpha = ObjectAnimator.ofFloat(binding.ring1, View.ALPHA, 0f, 0.4f).apply { duration = 1500 }
        val ring1Rotate = ObjectAnimator.ofFloat(binding.ring1, View.ROTATION, 0f, 180f).apply { duration = 3000 }

        val ring2ScaleX = ObjectAnimator.ofFloat(binding.ring2, View.SCALE_X, 0.3f, 1.1f).apply { duration = 2500 }
        val ring2ScaleY = ObjectAnimator.ofFloat(binding.ring2, View.SCALE_Y, 0.3f, 1.1f).apply { duration = 2500 }
        val ring2Alpha = ObjectAnimator.ofFloat(binding.ring2, View.ALPHA, 0f, 0.3f).apply { duration = 2000 }
        val ring2Rotate = ObjectAnimator.ofFloat(binding.ring2, View.ROTATION, 0f, -180f).apply { duration = 3500 }

        // 2. Logo Entrance (Anticipate Overshoot - "Pop" effect)
        val logoFade = ObjectAnimator.ofFloat(binding.appLogo, View.ALPHA, 0f, 1f).apply {
            duration = 1000
        }
        val logoScaleX = ObjectAnimator.ofFloat(binding.appLogo, View.SCALE_X, 0.4f, 1f).apply {
            duration = 1500
            interpolator = AnticipateOvershootInterpolator()
        }
        val logoScaleY = ObjectAnimator.ofFloat(binding.appLogo, View.SCALE_Y, 0.4f, 1f).apply {
            duration = 1500
            interpolator = AnticipateOvershootInterpolator()
        }

        // 3. Loader Entrance
        val loaderAlpha = ObjectAnimator.ofFloat(binding.loader, View.ALPHA, 0f, 1f).apply {
            duration = 800
            startDelay = 1200
        }

        // Run Background Loop
        AnimatorSet().apply {
            playTogether(ring1Rotate, ring2Rotate)
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }

        val mainSet = AnimatorSet().apply {
            playTogether(
                ring1ScaleX, ring1ScaleY, ring1Alpha,
                ring2ScaleX, ring2ScaleY, ring2Alpha,
                logoFade, logoScaleX, logoScaleY,
                loaderAlpha
            )
            startDelay = 300
        }

        mainSet.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                // Hold the state for a bit
                Handler(Looper.getMainLooper()).postDelayed({
                    fadeOutAndExit()
                }, 1500)
            }
        })

        mainSet.start()
    }

    private fun fadeOutAndExit() {
        val containerFade = ObjectAnimator.ofFloat(binding.splashContainer, View.ALPHA, 1f, 0f).apply {
            duration = 600
        }
        containerFade.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                navigateToLogin()
            }
        })
        containerFade.start()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish()
    }
}
