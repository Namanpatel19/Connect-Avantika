package com.example.myapplication.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClubsScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
    ) {
        // App Bar
        CenterAlignedTopAppBar(
            title = { Text("Clubs", fontWeight = FontWeight.Bold, color = TextDark) },
            navigationIcon = {
                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextDark)
                }
            },
            actions = {
                IconButton(onClick = { /* TODO */ }) {
                    Box {
                        Icon(Icons.Default.Notifications, contentDescription = null, tint = TextDark)
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color.Red)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
        )

        Column(modifier = Modifier.padding(24.dp)) {
            // Tabs
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("All", "My Clubs", "Popular").forEach { tab ->
                    FilterChip(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        label = { Text(tab) },
                        shape = RoundedCornerShape(12.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryTeal,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = TextGray
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = Color.Transparent, 
                            selectedBorderColor = Color.Transparent,
                            enabled = true,
                            selected = selectedTab == tab
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search clubs...", color = TextGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextGray) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color(0xFFE5E7EB),
                    focusedBorderColor = PrimaryTeal,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Club Cards
            ClubCard(
                name = "Tech Club",
                category = "Innovation & Technology",
                members = 248,
                isJoined = true,
                tags = listOf("Top Club", "Technology"),
                color = Color(0xFF22C55E)
            )

            Spacer(modifier = Modifier.height(24.dp))

            ClubCard(
                name = "Arts Society",
                category = "Creative Arts & Performance",
                members = 192,
                isJoined = false,
                tags = listOf("Trending", "Arts & Culture"),
                color = Color(0xFFEAB308)
            )
        }
    }
}

@Composable
fun ClubCard(name: String, category: String, members: Int, isJoined: Boolean, tags: List<String>, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFF3F4F6))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (name.contains("Tech")) Icons.Default.Laptop else Icons.Default.Palette,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text(category, fontSize = 14.sp, color = TextGray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (tag == "Top Club") Color(0xFFFEF9C3)
                                else if (tag == "Trending") Color(0xFFFFEDD5)
                                else Color(0xFFF3F4F6)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (tag == "Top Club") Icon(Icons.Default.Star, null, tint = Color(0xFFEAB308), modifier = Modifier.size(12.dp))
                            else if (tag == "Trending") Icon(Icons.Default.Whatshot, null, tint = Color(0xFFF97316), modifier = Modifier.size(12.dp))
                            if (tag == "Top Club" || tag == "Trending") Spacer(modifier = Modifier.width(4.dp))
                            Text(tag, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = TextDark)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Groups, null, tint = TextGray, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("$members members", fontSize = 14.sp, color = TextDark, fontWeight = FontWeight.Medium)
                }

                if (isJoined) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF0FDF4))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("Joined ✓", color = Color(0xFF22C55E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isJoined) PrimaryTeal else SecondaryGreen
                )
            ) {
                Text(if (isJoined) "View Club" else "Join Club", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}
