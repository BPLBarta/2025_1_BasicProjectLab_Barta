package com.example.barta

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.barta.ui.theme.BartaTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.example.barta.ui.theme.BartaTheme
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    private val REQUEST_RECORD_AUDIO_PERMISSION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        } else {
            startApp()  // 권한이 이미 승인된 경우
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startApp()  // 권한 승인 후 앱 로드
            } else {
                Toast.makeText(this, "마이크 권한이 필요합니다", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun startApp() {
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
