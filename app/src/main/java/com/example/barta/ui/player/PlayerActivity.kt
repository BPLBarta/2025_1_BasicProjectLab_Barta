package com.example.barta.ui.player

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.barta.PlayerScreen

class PlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val videoId = intent.getStringExtra("videoId") ?: ""

        setContent {
            val navController = rememberNavController()
            PlayerScreen(videoId = videoId, navController = navController)
        }
    }
}
