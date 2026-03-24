package com.example.myapplication.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.*

@Composable
fun AvantikaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector? = null,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextDark,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(text = placeholder, color = TextGray, fontSize = 14.sp) },
            leadingIcon = leadingIcon?.let { { Icon(imageVector = it, contentDescription = null, tint = TextGray, modifier = Modifier.size(20.dp)) } },
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = TextGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else null,
            visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE5E7EB),
                focusedBorderColor = PrimaryTeal,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
}

@Composable
fun RoleSelector(
    roles: List<Triple<String, ImageVector, String>>,
    selectedRole: String,
    onRoleSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Select Your Role",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = TextDark,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            roles.take(2).forEach { (label, icon, id) ->
                RoleCard(
                    label = label,
                    icon = icon,
                    isSelected = selectedRole == id,
                    onClick = { onRoleSelected(id) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            roles.drop(2).forEach { (label, icon, id) ->
                RoleCard(
                    label = label,
                    icon = icon,
                    isSelected = selectedRole == id,
                    onClick = { onRoleSelected(id) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun RoleCard(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) PrimaryTeal else Color(0xFFE5E7EB)
    val textColor = if (isSelected) PrimaryTeal else TextGray
    val iconColor = if (isSelected) PrimaryTeal else TextGray
    val borderWidth = if (isSelected) 2.dp else 1.dp

    Box(
        modifier = modifier
            .height(90.dp)
            .clip(RoundedCornerShape(20.dp))
            .border(borderWidth, borderColor, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

@Composable
fun AvantikaPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = PrimaryTeal,
    textColor: Color = Color.White
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor)
    ) {
        Text(text = text, color = textColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AvantikaHeader(
    title: String,
    subtitle: String,
    icon: ImageVector = Icons.Default.School
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .background(PrimaryTeal),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(bottom = 40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val items = listOf(
            Triple("Home", Icons.Default.Home, "home"),
            Triple("Calendar", Icons.Default.CalendarMonth, "calendar"),
            Triple("Clubs", Icons.Default.Groups, "clubs"),
            Triple("Profile", Icons.Default.Person, "profile")
        )

        items.forEach { (label, icon, route) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = { onNavigate(route) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 12.sp) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryTeal,
                    selectedTextColor = PrimaryTeal,
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray,
                    indicatorColor = PrimaryTeal.copy(alpha = 0.1f)
                )
            )
        }
    }
}
