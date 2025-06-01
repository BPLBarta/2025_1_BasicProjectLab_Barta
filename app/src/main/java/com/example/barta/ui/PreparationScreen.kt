package com.example.barta.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.barta.data.getPreparationText
import com.example.barta.ui.component.BartaIcon

@Composable
fun PreparationScreen(videoId: String,onMicClick: () -> Unit) {
    val prepText = getPreparationText(videoId).ingredients
    val lines = prepText.lines().filter { it.isNotBlank() }
    val mid = lines.size / 2
    val left = lines.subList(0, mid).joinToString("\n")
    val right = lines.subList(mid, lines.size).joinToString("\n")

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .zIndex(2f)
        ) {
            BartaIcon(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onMicClick() }  // 음성인식 콜백 호출
            )
        }

        AsyncImage(
            model = "https://img.youtube.com/vi/$videoId/0.jpg",
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))
        Text(
            text = "요리재료",
            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp).align(Alignment.TopCenter),
            textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier.fillMaxSize().padding(top = 56.dp, start = 16.dp, end = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(491.dp)
                    .heightIn(min = 330.dp)
                    .background(Color(0xFFFCECD7), RoundedCornerShape(10.dp))
                    .padding(20.dp)
            ) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = left, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = right, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                }
            }
        }
    }
}
