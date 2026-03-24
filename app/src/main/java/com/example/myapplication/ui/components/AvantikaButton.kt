package com.example.myapplication.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AvantikaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    gradient: Brush = Brush.linearGradient(
        listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.secondary
        )
    ),
    textColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                enabled = enabled && !loading,
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, radius = 28.dp)
            )
            .background(gradient)
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = loading,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = textColor,
                strokeWidth = 2.dp
            )
        }

        AnimatedVisibility(
            visible = !loading,
            enter = fadeIn(animationSpec = tween(300)),
            exit = fadeOut(animationSpec = tween(300))
        ) {
            Text(
                text = text,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun AvantikaOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, radius = 28.dp)
            )
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 32.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
    }
}
