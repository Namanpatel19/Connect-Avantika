package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.myapplication.ui.components.BottomNavigationBar
import com.example.myapplication.ui.screens.*
import com.example.myapplication.ui.theme.AvantikaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AvantikaTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "login"

    Scaffold(
        bottomBar = {
            if (currentRoute != "login") {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(padding)
        ) {
            composable("login") {
                LoginScreen(onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                })
            }
            composable("home") { HomeScreen() }
            composable("calendar") { CalendarScreen() }
            composable("clubs") { ClubsScreen() }
            composable("profile") { 
                ProfileScreen(onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }) 
            }
        }
    }
}
