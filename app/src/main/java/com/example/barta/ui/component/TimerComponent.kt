package com.example.barta.ui.component

import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.barta.ui.theme.LocalBartaPalette
import kotlinx.coroutines.delay

@Composable
fun TimerComponent(
    totalTime: Int = 180,  // 기본 3분
    onFinish: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var timeLeft by remember { mutableStateOf(totalTime) }
    val color = LocalBartaPalette.current

    LaunchedEffect(Unit) {
        while (timeLeft > 0) {
            delay(1000)
            timeLeft -= 1
        }
        onFinish()
    }

    // 분과 초 계산
    val minutes = totalTime / 60
    val seconds = totalTime % 60
    val timeText = if (seconds > 0 && 60 > seconds) {
        String.format("%d초", seconds)
    } else if (seconds == 0) { String.format("%d분", minutes) }
    else {
        String.format("%d분\n%d초", minutes, seconds)
    }

    Box(
        modifier = modifier
            .size(100.dp)  // 전체 크기 지정
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = timeLeft / totalTime.toFloat(),
            color = color.timerProgress,
            strokeWidth = 4.dp,
            modifier = Modifier.fillMaxSize()
        )

        Text(
            text = timeText,
            textAlign = TextAlign.Center,// 원형 중앙에 텍스트 배치
            style = MaterialTheme.typography.subtitle2
        )
    }
}