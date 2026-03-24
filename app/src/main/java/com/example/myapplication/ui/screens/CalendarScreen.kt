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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .verticalScroll(rememberScrollState())
    ) {
        // App Bar
        CenterAlignedTopAppBar(
            title = { Text("Calendar", fontWeight = FontWeight.Bold, color = TextDark) },
            navigationIcon = {
                IconButton(onClick = { /* TODO */ }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = TextDark)
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
        )

        Column(modifier = Modifier.padding(24.dp)) {
            // Calendar Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("March 2026", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryTeal)
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = PrimaryTeal)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    val days = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
                    Row(modifier = Modifier.fillMaxWidth()) {
                        days.forEach { day ->
                            Text(
                                text = day,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                color = TextGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simplified Calendar Grid
                    val calendarRows = listOf(
                        listOf("1", "2", "3", "4", "5", "6", "7"),
                        listOf("8", "9", "10", "11", "12", "13", "14"),
                        listOf("15", "16", "17", "18", "19", "20", "21"),
                        listOf("22", "23", "24", "25", "26", "27", "28"),
                        listOf("29", "30", "31", "", "", "", "")
                    )

                    calendarRows.forEach { row ->
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            row.forEach { day ->
                                Box(
                                    modifier = Modifier.weight(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (day == "24") {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(PrimaryTeal),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(day, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                    } else if (day.isNotEmpty()) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(day, color = TextDark, fontSize = 14.sp)
                                            if (day == "25" || day == "30" || day == "28") {
                                                Box(modifier = Modifier.size(4.dp).clip(CircleShape).background(SecondaryGreen))
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Upcoming Events", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextDark)
            Spacer(modifier = Modifier.height(16.dp))

            EventItem(
                title = "Tech Club Meetup",
                category = "Club Event",
                date = "Mar 25, 2026",
                time = "14:00",
                location = "Room 301",
                color = AccentBlue
            )

            Spacer(modifier = Modifier.height(16.dp))

            EventItem(
                title = "Basketball Tournament Finals",
                category = "Sports",
                date = "Mar 26, 2026",
                time = "16:00",
                location = "Sports Complex",
                color = SecondaryGreen
            )
        }
    }
}

@Composable
fun EventItem(title: String, category: String, date: String, time: String, location: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(modifier = Modifier.width(4.dp).fillMaxHeight().background(color))
            Column(modifier = Modifier.padding(20.dp)) {
                Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text(category, fontSize = 12.sp, color = TextGray)
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = TextGray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(date, fontSize = 13.sp, color = TextGray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = TextGray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(time, fontSize = 13.sp, color = TextGray)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextGray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(location, fontSize = 13.sp, color = TextGray)
                }
            }
        }
    }
}
