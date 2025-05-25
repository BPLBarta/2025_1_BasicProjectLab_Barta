package com.example.barta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.barta.ui.theme.BartaTheme
import com.example.barta.util.Step
import com.example.barta.util.parseChaptersFromDescription

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BartaTheme {
                Surface(color = MaterialTheme.colors.background) {
                    BartaAppMain()
                }
            }
        }
    }
}

//@Composable
//fun BartaNavRoot() {
//    val navController = rememberNavController()
//    NavHost(navController = navController, startDestination = "home") {
//        composable("home") {
//            HomeScreen(navController)
//        }
//        composable("player/{videoId}") { backStackEntry ->
//            val videoId = backStackEntry.arguments?.getString("videoId") ?: ""
//
//
//            PlayerScreen(videoId= videoId, navController= navController)
//        }
//    }
//}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    BartaTheme {
        BartaAppMain()
    }
}
