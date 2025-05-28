package com.example.barta.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val coroutineScope = rememberCoroutineScope()
    var timerJob by remember { mutableStateOf<Job?>(null) }

    val color = LocalBartaPalette.current

    // 텍스트는 고정된 totalTime 기준으로 설정
    val timeText = if (totalTime < 60) {
        "${totalTime}초"
    } else {
        val min = totalTime / 60
        val sec = totalTime % 60
        if (sec == 0) "${min}분" else "${min}분\n${sec}초"
    }

    Box(
        modifier = modifier
            .size(100.dp) // 외부에서 받는 modifier에 size 고정
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                if (isRunning) {
                    timerJob?.cancel()
                    isRunning = false
                } else {
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
        // 흰색 원 배경
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.surface, shape = CircleShape)
        )

        // 진행도 표시 원
        CircularProgressIndicator(
            progress = timeLeft / totalTime.toFloat(),
            color = color.timerProgress,
            strokeWidth = 6.dp,
            modifier = Modifier.fillMaxSize()
        )

        // 남은 시간 텍스트
        Text(
            text = timeText,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.onSurface
        )
    }

}
