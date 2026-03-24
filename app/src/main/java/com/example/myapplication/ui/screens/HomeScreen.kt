package com.example.myapplication.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.AvantikaCard
import com.example.myapplication.ui.theme.*

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
    ) {
        // Custom Dashboard Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(PrimaryTeal, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Welcome back,", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        Text("Sarah Johnson", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Box {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color.Red)
                            .align(Alignment.TopEnd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("3", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Next Event Card
        Box(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .offset(y = (-60).dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SecondaryGreen)
            ) {
                Row(
                    modifier = Modifier.padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Next Event", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tech Symposium 2024", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("Tech Club", color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { /* TODO */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                        ) {
                            Text("View Details", color = SecondaryGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Today", color = Color.White, fontSize = 12.sp)
                        Text("2:00", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text("PM", color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp).offset(y = (-40).dp)) {
            Text("Quick Actions", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(16.dp))

            val items = listOf(
                Triple("Events", Icons.Default.Event, AccentBlue),
                Triple("Clubs", Icons.Default.Groups, SecondaryGreen),
                Triple("Calendar", Icons.Default.CalendarMonth, AccentPurple),
                Triple("Transport", Icons.Default.DirectionsBus, AccentOrange),
                Triple("Awards", Icons.Default.EmojiEvents, AccentYellow)
            )

            items.chunked(2).forEach { row ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    row.forEach { (label, icon, color) ->
                        QuickActionCard(label, icon, color, modifier = Modifier.weight(1f))
                    }
                    if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun QuickActionCard(label: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    AvantikaCard(
        modifier = modifier,
        elevation = 0,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(label, fontWeight = FontWeight.Bold, color = TextDark)
            Text(
                text = if (label == "Events") "2 events" else if (label == "Clubs") "3 clubs" else "",
                fontSize = 12.sp,
                color = TextGray
            )
        }
    }
}
