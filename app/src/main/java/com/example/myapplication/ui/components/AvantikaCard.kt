package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AvantikaCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    gradient: Brush? = null,
    backgroundColor: Color = Color.White,
    contentColor: Color = Color.Unspecified,
    elevation: Int = 0,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = modifier
        .shadow(elevation.dp, shape)
        .clip(shape)
        .let { if (gradient != null) it.background(gradient) else it.background(backgroundColor) }
        .let { if (onClick != null) it.clickable(onClick = onClick) else it }
        .padding(20.dp)

    CompositionLocalProvider(LocalContentColor provides contentColor) {
        Column(
            modifier = cardModifier
        ) {
            content()
        }
    }
}
