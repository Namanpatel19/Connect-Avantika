package com.example.myapplication.ui.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.*

@Composable
fun EnhancedSplashScreen(
    onSplashComplete: () -> Unit
) {
    var animationPhase by remember { mutableStateOf(0) }
    var scale by remember { mutableStateOf(0.5f) }
    var rotation by remember { mutableStateOf(0f) }
    var alpha by remember { mutableStateOf(0f) }
    var particlesAlpha by remember { mutableStateOf(0f) }
    var loadingDots by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        // Phase 1: Logo entrance (0-1500ms)
        delay(300)
        alpha = 1f
        scale = 1f
        rotation = 360f
        
        delay(1200)
        
        // Phase 2: Particles and text appear (1500-2500ms)
        animationPhase = 1
        particlesAlpha = 1f
        
        delay(1000)
        
        // Phase 3: Loading animation (2500-4000ms)
        animationPhase = 2
        
        // Loading dots animation
        while (animationPhase == 2) {
            delay(300)
            loadingDots = (loadingDots + 1) % 4
        }
        
        delay(1500)
        
        // Phase 4: Exit transition (4000-4500ms)
        animationPhase = 3
        alpha = 0f
        scale = 1.2f
        
        delay(500)
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF334155)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(1000f, 1000f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Background particles
        if (animationPhase >= 1) {
            ParticlesBackground(
                modifier = Modifier.fillMaxSize(),
                alpha = particlesAlpha
            )
        }

        // Main logo container
        Box(
            modifier = Modifier
                .size(200.dp)
                .scale(scale)
                .rotate(rotation),
            contentAlignment = Alignment.Center
        ) {
            // Outer ring animation
            if (animationPhase >= 1) {
                AnimatedRings(
                    modifier = Modifier.size(200.dp)
                )
            }

            // Central logo
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF6366F1),
                                    Color(0xFF8B5CF6),
                                    Color(0xFFEC4899)
                                )
                            ),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "A",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "AVANTIKA",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 4.sp
                )

                Text(
                    text = "CONNECT",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 8.sp
                )
            }
        }

        // Loading text
        if (animationPhase == 2) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Text(
                    text = "Initializing Experience" + ".".repeat(loadingDots),
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Progress bar
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(4.dp)
                        .background(
                            Color.White.copy(alpha = 0.2f),
                            CircleShape
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .width(200.dp * (loadingDots / 3f))
                            .height(4.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF6366F1),
                                        Color(0xFFEC4899)
                                    )
                                ),
                                CircleShape
                            )
                    )
                }
                
                Spacer(modifier = Modifier.height(48.dp))
            }
        }
    }
}

@Composable
fun AnimatedRings(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rings")
    
    val ring1Scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring1_scale"
    )
    
    val ring1Alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring1_alpha"
    )
    
    val ring2Scale by infiniteTransition.animateFloat(
        initialValue = 1.2f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring2_scale"
    )
    
    val ring2Alpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ring2_alpha"
    )

    Box(modifier = modifier) {
        // Ring 1
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(ring1Scale)
                .drawWithCache {
                    onDrawBehind {
                        drawCircle(
                            color = Color(0xFF6366F1).copy(alpha = ring1Alpha),
                            radius = size.minDimension / 2 - 10.dp.toPx(),
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
        )
        
        // Ring 2
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(ring2Scale)
                .drawWithCache {
                    onDrawBehind {
                        drawCircle(
                            color = Color(0xFFEC4899).copy(alpha = ring2Alpha),
                            radius = size.minDimension / 2 - 20.dp.toPx(),
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
        )
    }
}

@Composable
fun ParticlesBackground(
    modifier: Modifier = Modifier,
    alpha: Float = 1f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "particles")
    
    val particles = remember { List(20) { Particle() } }
    
    Canvas(modifier = modifier.alpha(alpha)) {
        particles.forEach { particle ->
            val time = (System.currentTimeMillis() / 1000f) % particle.duration
            val progress = time / particle.duration
            
            val x = particle.startX + (particle.endX - particle.startX) * progress
            val y = particle.startY + (particle.endY - particle.startY) * progress
            
            drawCircle(
                color = particle.color.copy(alpha = particle.alpha * (1f - progress * 0.5f)),
                radius = particle.size,
                center = Offset(x, y)
            )
        }
    }
}

data class Particle(
    val startX: Float = (Math.random() * 1000).toFloat(),
    val startY: Float = (Math.random() * 1000).toFloat(),
    val endX: Float = (Math.random() * 1000).toFloat(),
    val endY: Float = (Math.random() * 1000).toFloat(),
    val size: Float = (2 + Math.random() * 4).toFloat(),
    val alpha: Float = (0.1f + Math.random() * 0.3f).toFloat(),
    val duration: Float = (3 + Math.random() * 4).toFloat(),
    val color: Color = listOf(
        Color(0xFF6366F1),
        Color(0xFF8B5CF6),
        Color(0xFFEC4899)
    ).random()
)
