package com.example.barta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.example.barta.ui.theme.BartaTheme
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Context에서 로그인 상태를 가져옴
        val sharedPref = getSharedPreferences("AppPreferences", MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        setContent {
            BartaTheme {
                Surface(color = MaterialTheme.colors.background) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = if (isLoggedIn) "bartaAppMain" else "login"
                    ) {
                        composable("login") {
                            LoginScreen(navController)
                        }
                        composable("bartaAppMain") {
                            BartaAppMain()
                        }
                    }
                }
            }
        }
    }
}