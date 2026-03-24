package com.example.myapplication

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import kotlin.math.pow

class GridScanView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#392e4e")
        strokeWidth = 2f
        style = Paint.Style.STROKE
    }

    private val scanPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF9FFC")
        strokeWidth = 4f
        style = Paint.Style.STROKE
    }

    private val glowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FF9FFC")
        maskFilter = BlurMaskFilter(20f, BlurMaskFilter.Blur.NORMAL)
    }

    private val vignettePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var vignetteGradient: RadialGradient? = null

    private var time = 0f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        vignetteGradient = RadialGradient(
            w / 2f, h / 2f, h.toFloat(),
            intArrayOf(Color.TRANSPARENT, Color.BLACK),
            floatArrayOf(0.3f, 1f),
            Shader.TileMode.CLAMP
        )
        vignettePaint.shader = vignetteGradient
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(Color.BLACK)

        time += 0.015f
        val w = width.toFloat()
        val h = height.toFloat()
        val centerX = w / 2f
        val centerY = h / 2f

        // Draw 3D Grid Perspective
        val gridCount = 25
        val spacing = w / gridCount
        val scanPos = (time % 2.5f) - 1.2f // Range -1.2 to 1.3 for full coverage

        // Horizontal lines (Perspective)
        for (i in 0..gridCount) {
            val progress = i.toFloat() / gridCount
            // Exponential scale creates the 3D depth effect
            val scale = progress.pow(2.2f)
            val lineY = centerY + scale * centerY
            
            // Calculate distance to scan line for glow effect
            val normalizedY = progress * 2f - 1f
            val distToScan = Math.abs(normalizedY - scanPos)
            val glowIntensity = (1f - (distToScan * 4f)).coerceIn(0f, 1f)
            
            if (glowIntensity > 0) {
                scanPaint.alpha = (glowIntensity * 255).toInt()
                glowPaint.alpha = (glowIntensity * 120).toInt()
                canvas.drawLine(0f, lineY, w, lineY, glowPaint)
                canvas.drawLine(0f, lineY, w, lineY, scanPaint)
            } else {
                gridPaint.alpha = 70
                canvas.drawLine(0f, lineY, w, lineY, gridPaint)
            }
        }

        // Vertical lines (Perspective from vanishing point)
        for (i in -gridCount..gridCount) {
            val xOffset = i * spacing
            // Vanishing point at (centerX, centerY)
            val startX = centerX + xOffset * 0.05f 
            val endX = centerX + xOffset * 6f // Flare out at bottom
            
            gridPaint.alpha = 50
            canvas.drawLine(startX, centerY, endX, h, gridPaint)
        }

        // Animated Scanning Bar Effect
        val scanY = centerY + ((scanPos + 1f) / 2f).coerceIn(0f, 1f) * centerY
        if (scanY > centerY && scanY < h) {
            glowPaint.alpha = 180
            canvas.drawRect(0f, scanY - 15f, w, scanY + 15f, glowPaint)
            scanPaint.alpha = 255
            canvas.drawLine(0f, scanY, w, scanY, scanPaint)
        }

        // Apply pre-calculated vignette for depth
        canvas.drawRect(0f, 0f, w, h, vignettePaint)

        invalidate()
    }
}
