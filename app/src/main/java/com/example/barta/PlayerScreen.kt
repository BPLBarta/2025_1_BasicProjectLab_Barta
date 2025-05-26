package com.example.barta

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.barta.util.STTController
import com.example.barta.util.Step
import com.example.barta.util.fetchYoutubeDescription
import com.example.barta.util.parseChaptersFromDescription
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import androidx.navigation.NavGraph.Companion.findStartDestination

@Composable
fun PlayerScreen(videoId: String, navController: NavController) {
    val context = LocalContext.current
    val tracker = remember { YouTubePlayerTracker() }
    val handler = remember { Handler(Looper.getMainLooper()) }
    var currentStepIndex by remember { mutableStateOf(0) }
    var youTubePlayer: YouTubePlayer? by remember { mutableStateOf(null) }
    var steps by remember { mutableStateOf<List<Step>>(emptyList()) }
    var showDialog by remember { mutableStateOf(false) }

    // 🎤 STT 상태 변수 추가
    var listeningText by remember { mutableStateOf("인식 대기중...") }
    var isCommandMode by remember { mutableStateOf(false) }

    val apiKey = BuildConfig.YOUTUBE_API_KEY

    LaunchedEffect(videoId) {
        val description = fetchYoutubeDescription(videoId, apiKey)
        steps = parseChaptersFromDescription(description)
    }

    
    val sttController = remember {
        STTController(
            context,
            onCommandDetected = { command ->
                if (isCommandMode) {
                    when {
                        command.contains("멈춰", ignoreCase = true) -> {
                            // 영상 멈춤 상태 유지
                        }
                        command.contains("다음", ignoreCase = true) -> {
                            if (currentStepIndex < steps.lastIndex) {
                                currentStepIndex++
                                youTubePlayer?.seekTo(steps[currentStepIndex].startTime)
                            }
                        }
                        command.contains("이전", ignoreCase = true) -> {
                            if (currentStepIndex > 0) {
                                currentStepIndex--
                                youTubePlayer?.seekTo(steps[currentStepIndex].startTime)
                            }
                        }
                    }
                    isCommandMode = false
                    youTubePlayer?.play()
                }
            },
            onListeningText = { text ->
                listeningText = text
            },
            onWakeWordDetected = {
                youTubePlayer?.pause()
                isCommandMode = true
            }
        )
    }

    // 🎤 STT 시작 및 종료 처리
    LaunchedEffect(Unit) { sttController.startListening() }
    DisposableEffect(Unit) { onDispose { sttController.destroy() } }

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.height(200.dp),
            factory = { ctx ->
                val view = YouTubePlayerView(ctx)
                view.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(player: YouTubePlayer) {
                        youTubePlayer = player
                        player.addListener(tracker)
                        player.loadVideo(videoId, steps.firstOrNull()?.startTime ?: 0f)
                    }
                })
                view
            }
        )

        // 🎤 STT 상태 표시
        Text(
            text = if (isCommandMode) "명령어 모드 (7초)" else "현재 인식: $listeningText",
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            style = MaterialTheme.typography.body1
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = {
                if (currentStepIndex > 0) {
                    currentStepIndex--
                    youTubePlayer?.seekTo(steps[currentStepIndex].startTime)
                }
            }) {
                Text("이전")
            }

            Button(onClick = {
                if (currentStepIndex < steps.lastIndex) {
                    currentStepIndex++
                    youTubePlayer?.seekTo(steps[currentStepIndex].startTime)
                } else {
                    showDialog = true
                }
            }) {
                Text("다음")
            }
        }

        Text(
            text = steps.getOrNull(currentStepIndex)?.title ?: "",
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.h6
        )

        Divider()

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(steps) { step ->
                Text(
                    text = "${formatTime(step.startTime)} ~ ${formatTime(step.endTime)}: ${step.title}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("요리를 완성하였습니다!") },
            text = { Text("영상으로 다시 돌아가거나 홈 화면으로 이동할 수 있어요.") },
            confirmButton = {
                Button(onClick = {
                    currentStepIndex = 0
                    youTubePlayer?.seekTo(steps.getOrNull(0)?.startTime ?: 0f)
                    showDialog = false
                }) {
                    Text("영상 다시 보기")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showDialog = false
                    navController.navigate("home") {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }) {
                    Text("홈으로 가기")
                }
            }
        )
    }
}

fun formatTime(seconds: Float): String {
    val total = seconds.toInt()
    val minutes = total / 60
    val sec = total % 60
    return "%02d:%02d".format(minutes, sec)
}
