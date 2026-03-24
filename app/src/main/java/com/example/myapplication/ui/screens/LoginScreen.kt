package com.example.myapplication.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.components.AvantikaHeader
import com.example.myapplication.ui.components.AvantikaPrimaryButton
import com.example.myapplication.ui.components.AvantikaTextField
import com.example.myapplication.ui.components.RoleSelector
import com.example.myapplication.ui.theme.PrimaryTeal
import com.example.myapplication.ui.theme.TextDark
import com.example.myapplication.ui.theme.TextGray

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("student") }

    val roles = listOf(
        Triple("Admin", Icons.Default.AdminPanelSettings, "admin"),
        Triple("Club Lead", Icons.Default.Groups, "club_lead"),
        Triple("Faculty", Icons.Default.School, "faculty"),
        Triple("Student", Icons.Default.Person, "student")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        AvantikaHeader(
            title = "University Students",
            subtitle = "Welcome Back"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset(y = (-30).dp)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color.White)
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                AvantikaTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    placeholder = "your.email@university.edu",
                    leadingIcon = Icons.Default.Email
                )

                Spacer(modifier = Modifier.height(20.dp))

                AvantikaTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "Enter your password",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                RoleSelector(
                    roles = roles,
                    selectedRole = selectedRole,
                    onRoleSelected = { selectedRole = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Forgot Password?",
                    color = PrimaryTeal,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable { /* TODO */ }
                )

                Spacer(modifier = Modifier.height(24.dp))

                AvantikaPrimaryButton(
                    text = "Login",
                    onClick = onLoginSuccess
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Divider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
                    Text(
                        text = "OR",
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color = TextGray,
                        fontSize = 12.sp
                    )
                    Divider(modifier = Modifier.weight(1f), color = Color(0xFFE5E7EB))
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = { /* TODO */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color(0xFFE5E7EB))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Assuming you have a google icon resource
                        Icon(
                            imageVector = Icons.Default.AccountCircle, // Placeholder for Google Icon
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Continue with Google",
                            color = TextDark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Don't have an account? ", color = TextGray, fontSize = 14.sp)
                    Text(
                        text = "Contact Admin",
                        color = PrimaryTeal,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { /* TODO */ }
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
