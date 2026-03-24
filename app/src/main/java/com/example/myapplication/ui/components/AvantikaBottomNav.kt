package com.example.myapplication.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class NavItem(
    val title: String,
    val icon: ImageVector,
    val badge: String? = null
)

@Composable
fun AvantikaBottomNav(
    items: List<NavItem>,
    selectedItem: Int,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 16.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                NavItemCard(
                    item = item,
                    isSelected = index == selectedItem,
                    onClick = { onItemClick(index) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun NavItemCard(
    item: NavItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) {
        Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.primary,
                MaterialTheme.colorScheme.secondary
            )
        )
    } else {
        Brush.linearGradient(
            colors = listOf(
                MaterialTheme.colorScheme.surface,
                MaterialTheme.colorScheme.surface
            )
        )
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    }

    Box(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true)
            )
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            
            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = item.title,
                    color = contentColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
            }
            
            item.badge?.let { badge ->
                Spacer(modifier = Modifier.width(4.dp))
                Badge(
                    modifier = Modifier.size(16.dp),
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                ) {
                    Text(
                        text = badge,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onError
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun FloatingActionBar(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.secondary
                    )
                )
            )
            .clickable(
                onClick = onClick,
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false, radius = 28.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(24.dp)
        )
    }
}
