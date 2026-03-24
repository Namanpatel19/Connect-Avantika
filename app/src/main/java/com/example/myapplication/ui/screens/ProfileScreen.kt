package com.example.myapplication.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
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
import com.example.myapplication.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
    ) {
        // App Bar
        CenterAlignedTopAppBar(
            title = { Text("My Profile", fontWeight = FontWeight.Bold, color = TextDark) },
            navigationIcon = {
                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextDark)
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
        )

        Column(modifier = Modifier.padding(24.dp)) {
            // Profile Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Brush.verticalGradient(listOf(SecondaryGreen, PrimaryTeal))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp))
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PrimaryTeal)
                                .border(2.dp, Color.White, CircleShape)
                                .clickable { /* TODO */ },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Sarah Johnson", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Text("Computer Science", fontSize = 16.sp, color = TextGray)
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF3F4F6))
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text("Student ID: 2024001", fontSize = 13.sp, color = TextGray, fontWeight = FontWeight.Medium)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = Color(0xFFF3F4F6))
                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        StatItem("3", "Clubs", Modifier.weight(1f))
                        StatItem("12", "Events", Modifier.weight(1f))
                        StatItem("7", "Achievements", Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Information Sections
            InfoSection("Personal Information") {
                InfoItem(Icons.Default.Email, "Email", "sarah.johnson@university.edu", AccentBlue)
                InfoItem(Icons.Default.Phone, "Phone", "+1 (555) 123-4567", SecondaryGreen)
                InfoItem(Icons.Default.CalendarToday, "Date of Birth", "January 15, 2003", AccentPurple)
                InfoItem(Icons.Default.LocationOn, "Address", "123 University Ave, Campus City", AccentOrange)
            }

            Spacer(modifier = Modifier.height(24.dp))

            InfoSection("Academic Information") {
                AcademicItem("Department", "Computer Science")
                AcademicItem("Year", "3rd Year")
                AcademicItem("Semester", "Spring 2026")
                AcademicItem("GPA", "3.8 / 4.0")
            }

            Spacer(modifier = Modifier.height(24.dp))

            InfoSection("Recent Achievements", trailingIcon = Icons.Default.BookmarkBorder) {
                AchievementItem("Event Enthusiast", "Attended 10+ events", AccentYellow)
                AchievementItem("Club Leader", "Active in 3+ clubs", AccentBlue)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
            ) {
                Icon(Icons.Default.EditNote, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Edit Profile", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LogoutRedBg)
            ) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = LogoutRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout", color = LogoutRed, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun StatItem(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDark)
        Text(label, fontSize = 12.sp, color = TextGray)
    }
}

@Composable
fun InfoSection(title: String, trailingIcon: ImageVector? = null, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
                trailingIcon?.let { Icon(it, contentDescription = null, tint = SecondaryGreen) }
            }
            Spacer(modifier = Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun InfoItem(icon: ImageVector, label: String, value: String, color: Color) {
    Row(modifier = Modifier.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(color.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 12.sp, color = TextGray)
            Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextDark)
        }
    }
}

@Composable
fun AcademicItem(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, fontSize = 14.sp, color = TextGray)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
    }
}

@Composable
fun AchievementItem(title: String, desc: String, color: Color) {
    Row(modifier = Modifier.padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Text(desc, fontSize = 12.sp, color = TextGray)
        }
    }
}
