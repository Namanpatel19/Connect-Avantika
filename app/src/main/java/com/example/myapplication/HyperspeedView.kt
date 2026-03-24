package com.example.myapplication

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import java.util.*

class HyperspeedView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val random = Random()
    private val streaks = mutableListOf<Streak>()
    private val particles = mutableListOf<Particle>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val blurPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    private val leftColors = intArrayOf(0xFFD856BF.toInt(), 0xFF6750A2.toInt(), 0xFFC247AC.toInt())
    private val rightColors = intArrayOf(0xFF03B3C3.toInt(), 0xFF0E5EA5.toInt(), 0xFF324555.toInt())

    private var centerX = 0f
    private var centerY = 0f
    
    // Animation state
    private var globalSpeed = 0.01f
    private val speedInterpolator = AccelerateInterpolator(2f)
    private var startTime = System.currentTimeMillis()

    init {
        // Initial streaks
        for (i in 0 until 80) {
            streaks.add(createStreak(initial = true))
        }
        // Initial particles
        for (i in 0 until 40) {
            particles.add(createParticle())
        }
        
        blurPaint.maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.NORMAL)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
    }

    private fun createStreak(initial: Boolean = false): Streak {
        val isLeft = random.nextBoolean()
        val color = if (isLeft) leftColors[random.nextInt(leftColors.size)] else rightColors[random.nextInt(rightColors.size)]
        
        val angle = (random.nextFloat() * 2f * Math.PI).toFloat()

        return Streak(
            angle = angle,
            color = color,
            thickness = random.nextFloat() * 3f + 1f,
            progress = if (initial) random.nextFloat() else 0f,
            speedFactor = random.nextFloat() * 0.5f + 0.8f
        )
    }

    private fun createParticle(): Particle {
        return Particle(
            angle = (random.nextFloat() * 2f * Math.PI).toFloat(),
            distance = random.nextFloat(),
            size = random.nextFloat() * 4f + 2f,
            opacity = random.nextInt(100) + 50
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)

        // Calculate global speed increase over time
        val elapsed = (System.currentTimeMillis() - startTime) / 3000f // 3 second cycle
        globalSpeed = 0.01f + speedInterpolator.getInterpolation(elapsed.coerceIn(0f, 1f)) * 0.05f

        // Draw Particles (Background)
        drawParticles(canvas)

        // Draw Hyperspeed Streaks
        val iterator = streaks.iterator()
        while (iterator.hasNext()) {
            val streak = iterator.next()
            drawStreak(canvas, streak)
            streak.progress += globalSpeed * streak.speedFactor

            if (streak.progress >= 1.0f) {
                streak.reset()
            }
        }

        // Vignette effect for depth
        drawVignette(canvas)

        invalidate()
    }

    private fun drawParticles(canvas: Canvas) {
        paint.style = Paint.Style.FILL
        for (p in particles) {
            p.distance += globalSpeed * 0.2f
            if (p.distance > 1f) p.distance = 0f
            
            val dist = p.distance * p.distance * centerX * 1.5f
            val x = centerX + Math.cos(p.angle.toDouble()).toFloat() * dist
            val y = centerY + Math.sin(p.angle.toDouble()).toFloat() * dist
            
            paint.color = Color.WHITE
            paint.alpha = (p.opacity * (1f - p.distance)).toInt().coerceIn(0, 255)
            canvas.drawCircle(x, y, p.size * p.distance, paint)
        }
    }

    private fun drawStreak(canvas: Canvas, streak: Streak) {
        val p = streak.progress
        // Exponential growth for warp effect
        val scaleStart = p * p * p * 12f 
        val scaleEnd = (p + 0.15f) * (p + 0.15f) * (p + 0.15f) * 12f
        
        val startX = centerX + Math.cos(streak.angle.toDouble()).toFloat() * scaleStart * 60f
        val startY = centerY + Math.sin(streak.angle.toDouble()).toFloat() * scaleStart * 60f
        
        val endX = centerX + Math.cos(streak.angle.toDouble()).toFloat() * scaleEnd * 60f
        val endY = centerY + Math.sin(streak.angle.toDouble()).toFloat() * scaleEnd * 60f

        // Glow layer
        blurPaint.color = streak.color
        blurPaint.strokeWidth = streak.thickness * scaleStart * 2f
        blurPaint.alpha = (p * 150).toInt().coerceIn(0, 255)
        canvas.drawLine(startX, startY, endX, endY, blurPaint)

        // Core layer
        paint.color = Color.WHITE
        paint.strokeWidth = streak.thickness * scaleStart * 0.5f
        paint.alpha = (p * 255).toInt().coerceIn(0, 255)
        canvas.drawLine(startX, startY, endX, endY, paint)
    }

    private fun drawVignette(canvas: Canvas) {
        val gradient = RadialGradient(
            centerX, centerY, centerX * 1.5f,
            intArrayOf(Color.TRANSPARENT, Color.BLACK),
            floatArrayOf(0.4f, 1f),
            Shader.TileMode.CLAMP
        )
        paint.shader = gradient
        paint.alpha = 255
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        paint.shader = null
    }

    private inner class Streak(
        var angle: Float,
        var color: Int,
        var thickness: Float,
        var progress: Float,
        var speedFactor: Float
    ) {
        fun reset() {
            progress = 0f
            angle = (random.nextFloat() * 2f * Math.PI).toFloat()
            val isLeft = random.nextBoolean()
            color = if (isLeft) leftColors[random.nextInt(leftColors.size)] else rightColors[random.nextInt(rightColors.size)]
        }
    }

    private inner class Particle(
        var angle: Float,
        var distance: Float,
        var size: Float,
        var opacity: Int
    )
}
