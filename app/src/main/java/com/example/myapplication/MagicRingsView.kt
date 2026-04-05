package com.example.myapplication

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator

class MagicRingsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val ringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
    }

    private var animationValue = 0f
    private val animator = ValueAnimator.ofFloat(0f, 1f).apply {
        duration = 2500 // Faster duration for snappier feel
        interpolator = LinearInterpolator()
        repeatCount = ValueAnimator.INFINITE
        addUpdateListener {
            animationValue = it.animatedValue as Float
            invalidate()
        }
    }

    // Colors
    private val colorPurple = Color.parseColor("#E02BFF")
    private val colorCyan = Color.parseColor("#00F5FF")
    
    // Optimized: Only 2 rings as requested to prevent lag and focus the effect
    private val ringCount = 2

    // Pre-allocated objects
    private val blurFilter = BlurMaskFilter(50f, BlurMaskFilter.Blur.NORMAL)

    init {
        // Hardware acceleration is usually fine for Normal blur on modern devices.
        // We'll keep it on for better performance.
        animator.start()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        
        // Start near the logo center
        val baseRadius = 80f
        // Ensure it goes way outside the screen corners
        val screenDiagonal = Math.sqrt((width * width + height * height).toDouble()).toFloat()
        val maxRadius = screenDiagonal * 1.5f

        for (i in 0 until ringCount) {
            // Stagger only 2 rings
            val progress = (animationValue + i.toFloat() / ringCount) % 1f
            
            // "Shooting out" quadratic growth
            val radius = baseRadius + (progress * progress) * maxRadius
            
            // Fast fade in, then fade out as it clears the screen
            val alpha = if (progress < 0.15f) {
                (progress / 0.15f) * 255
            } else {
                (1f - progress) * 255
            }

            // Assign one color to each ring for high contrast
            val ringColor = if (i == 0) colorPurple else colorCyan
            
            // 1. Draw Massive Glow (Aura)
            glowPaint.color = ringColor
            glowPaint.alpha = (alpha * 0.4f).toInt()
            glowPaint.strokeWidth = 60f * (1f + progress)
            glowPaint.maskFilter = blurFilter
            canvas.drawCircle(centerX, centerY, radius, glowPaint)

            // 2. Draw Sharp Core
            ringPaint.color = ringColor
            ringPaint.alpha = alpha.toInt()
            ringPaint.strokeWidth = 12f * (1f + progress * 0.5f)
            ringPaint.maskFilter = null
            canvas.drawCircle(centerX, centerY, radius, ringPaint)
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator.cancel()
    }
}
