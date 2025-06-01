package com.example.barta.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.barta.ui.theme.LocalBartaPalette
import kotlinx.coroutines.*

@Composable
fun TimerComponent(
    totalTime: Int = 180,
    onFinish: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var timeLeft by remember { mutableStateOf(totalTime) }
    var isRunning by remember { mutableStateOf(false) }
    val color = LocalBartaPalette.current

    // 1초씩 카운트다운
    LaunchedEffect(isRunning, timeLeft) {
        if (isRunning && timeLeft > 0) {
            while (timeLeft > 0) {
                delay(1000)
                timeLeft -= 1
            }
            isRunning = false
            onFinish()
        }
    }

    val timeText = if (timeLeft < 60) {
        "${timeLeft}초"
    } else {
        val min = timeLeft / 60
        val sec = timeLeft % 60
        if (sec == 0) "${min}분" else "${min}분\n${sec}초"
    }

    val progress = (timeLeft / totalTime.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            // 클릭 시 카운트다운 토글
            .clickable {
                if (totalTime <= 0) return@clickable
                isRunning = !isRunning
            }
            // 외부에서 Circle 모양과 크기를 지정하므로 여기서는 clip과 background만 처리
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // 내부는 넘겨받은 크기(modifier)에 맞춰 fillMaxSize
        CircularProgressIndicator(
            progress = progress,
            color = color.timerProgress,
            strokeWidth = 6.dp,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            text = timeText,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onSurface
        )
    }
}
