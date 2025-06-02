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
    isRunning: Boolean,
    onFinish: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var timeLeft by remember { mutableStateOf(totalTime) }
    val color = LocalBartaPalette.current

    // 외부 상태에 따라 타이머 실행
    LaunchedEffect(isRunning, timeLeft) {
        if (isRunning && timeLeft > 0) {
            while (timeLeft > 0 && isRunning) {
                delay(1000)
                timeLeft -= 1
            }
            if (timeLeft == 0) onFinish()
        }
    }

    val timeText = if (totalTime < 60) {
        "${totalTime}초"
    } else {
        val min = totalTime / 60
        val sec = totalTime % 60
        if (sec == 0) "${min}분" else "${min}분\n${sec}초"
    }

    val progress = (timeLeft / totalTime.toFloat()).coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = progress,
            color = color.timerProgress,
            strokeWidth = 4.dp,
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

