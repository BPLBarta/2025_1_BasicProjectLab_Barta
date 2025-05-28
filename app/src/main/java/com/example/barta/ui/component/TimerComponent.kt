package com.example.barta.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.barta.ui.theme.LocalBartaPalette
import kotlinx.coroutines.*
import androidx.compose.foundation.interaction.MutableInteractionSource

@Composable
fun TimerComponent(
    totalTime: Int = 180,  // 기본 3분
    onFinish: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var timeLeft by remember { mutableStateOf(totalTime) }
    var isRunning by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var timerJob by remember { mutableStateOf<Job?>(null) }

    val color = LocalBartaPalette.current

    Box(
        modifier = modifier
            .size(100.dp)
            .padding(16.dp)
            .clickable(
                indication = null, // ✅ Ripple 제거
                interactionSource = remember { MutableInteractionSource() } // ✅ 클릭은 유지
            ) {
                if (isRunning) {
                    // 타이머 멈추기
                    timerJob?.cancel()
                    isRunning = false
                } else {
                    // 타이머 시작 또는 재개
                    isRunning = true
                    timerJob = coroutineScope.launch {
                        while (timeLeft > 0) {
                            delay(1000)
                            timeLeft -= 1
                        }
                        onFinish()
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = timeLeft / totalTime.toFloat(),
            color = color.timerProgress,
            strokeWidth = 4.dp,
            modifier = Modifier.fillMaxSize()
        )

        val minutes = totalTime / 60
        val seconds = totalTime % 60
        val timeText = if (totalTime < 60 || minutes == 0) {
            String.format("%d초", totalTime)
        } else if (seconds > 0) {
            String.format("%d분\n%d초", minutes, seconds)
        } else {
            String.format("%d분", minutes)
        }

        Text(
            text = timeText,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.subtitle2
        )
    }
}