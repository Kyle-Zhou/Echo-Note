package com.example.echonote.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.echonote.R
import com.example.echonote.ui.pages.AddPageScreen
import com.example.echonote.ui.pages.HomePageScreen
import com.example.echonote.ui.pages.LoginPageScreen
import com.example.echonote.ui.pages.SignupPageScreen
import com.example.echonote.ui.pages.TestPageScreen
import com.example.echonote.ui.theme.EchoNoteTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EchoNoteTheme {
                MyApp()
            }
        }
    }
}

@Preview
@Composable
fun MyApp() {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    // Get the current backstack entry to check the current screen
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = {
            // Floating action button will still be visible on all pages except login and signup
            if (currentDestination?.route !in listOf("login", "signup")) {
                FloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            navController.navigate("add")
                        }
                    },
                    backgroundColor = Color.White,
                    modifier = Modifier.size(75.dp)
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        bottomBar = {
            if (currentDestination?.route !in listOf("login", "signup")) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            Modifier.padding(innerPadding)
        ) {
            composable("home") { HomePageScreen() }
            composable("add") { AddPageScreen() }
            composable("test") { TestPageScreen() }
            composable("login") {
                LoginPageScreen(
                    onLoginSuccess = { message ->
                        navController.navigate("home")
                    },
                    onNavigateToSignup = { navController.navigate("signup") }
                )
            }
            composable("signup") {
                SignupPageScreen(
                    onSignupSuccess = { message ->
                        navController.navigate("home")
                    },
                    onNavigateToLogin = { navController.navigate("login") }
                )
            }

        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    BottomNavigation(
        backgroundColor = colorResource(id = R.color.blue),
        contentColor = Color.White
    ) {
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentDestination?.hierarchy?.any { it.route == "home" } == true,
            onClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Checklist, contentDescription = "Test") },
            label = { Text("Test") },
            selected = currentDestination?.hierarchy?.any { it.route == "test" } == true,
            onClick = {
                navController.navigate("test") {
                    popUpTo("home")
                }
            }
        )
    }
}
