package com.example.myapplication

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.toColorInt
import java.util.*
import kotlin.math.hypot

class ConnectPlexusView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val random = Random()
    private val particles = mutableListOf<Particle>()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 2.0f
    }

    private var touchX = -1f
    private var touchY = -1f
    private val maxDistance = 400f
    private val particleCount = 40

    // Theme colors matching your image
    private val particleColor = "#9C27B0".toColorInt() // Vivid Purple
    private val connectionColor = "#673AB7".toColorInt() // Deep Indigo

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        particles.clear()
        for (i in 0 until particleCount) {
            particles.add(createParticle(w.toFloat(), h.toFloat()))
        }
    }

    private fun createParticle(w: Float, h: Float): Particle {
        return Particle(
            x = random.nextFloat() * w,
            y = random.nextFloat() * h,
            vx = (random.nextFloat() - 0.5f) * 1.2f,
            vy = (random.nextFloat() - 0.5f) * 1.2f,
            radius = random.nextFloat() * 4f + 2f
        )
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                touchX = event.x
                touchY = event.y
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                touchX = -1f
                touchY = -1f
                performClick()
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw a dark gradient background if needed, or leave transparent to see XML background
        val w = width.toFloat()
        val h = height.toFloat()

        for (i in particles.indices) {
            val p = particles[i]
            
            p.x += p.vx
            p.y += p.vy

            if (p.x < 0 || p.x > w) p.vx *= -1
            if (p.y < 0 || p.y > h) p.vy *= -1

            // Draw particle glow
            paint.color = particleColor
            paint.alpha = 100
            canvas.drawCircle(p.x, p.y, p.radius * 2, paint)
            
            paint.alpha = 255
            canvas.drawCircle(p.x, p.y, p.radius, paint)

            // Draw connections
            for (j in i + 1 until particles.size) {
                val p2 = particles[j]
                val dist = hypot(p.x - p2.x, p.y - p2.y)

                if (dist < maxDistance) {
                    val alpha = (150 * (1f - dist / maxDistance)).toInt()
                    linePaint.color = connectionColor
                    linePaint.alpha = alpha
                    canvas.drawLine(p.x, p.y, p2.x, p2.y, linePaint)
                }
            }

            // Interactive Touch Connection
            if (touchX != -1f) {
                val distToTouch = hypot(p.x - touchX, p.y - touchY)
                if (distToTouch < maxDistance * 1.5f) {
                    val alpha = (255 * (1f - distToTouch / (maxDistance * 1.5f))).toInt()
                    linePaint.color = Color.WHITE
                    linePaint.alpha = alpha
                    canvas.drawLine(p.x, p.y, touchX, touchY, linePaint)
                }
            }
        }
        invalidate()
    }

    private data class Particle(
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        var radius: Float
    )
}
