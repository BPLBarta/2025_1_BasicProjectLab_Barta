package com.example.barta

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun HomeScreen(navController: NavController, modifier: Modifier = Modifier) {
    var url by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp), // ✅ 여기가 핵심
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("요리 영상 URL을 입력하세요", style = MaterialTheme.typography.h6)

        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = { Text("유튜브 링크") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        Button(
            onClick = {
                val videoId = extractVideoId(url)
                if (videoId.isNotEmpty()) {
                    navController.navigate("player/$videoId")
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("변환하기")
        }
    }
}


fun extractVideoId(url: String): String {
    val regex = Regex("(?:v=|be/|embed/)([\\w-]{11})")
    return regex.find(url)?.groupValues?.get(1) ?: ""
}
