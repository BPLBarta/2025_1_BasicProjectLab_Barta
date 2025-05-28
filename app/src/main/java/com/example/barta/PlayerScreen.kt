package com.example.barta

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.barta.ui.component.BartaIcon
import com.example.barta.ui.component.ProgressBarComponet
import com.example.barta.ui.component.RecipeSubtitle
import com.example.barta.ui.component.TimerComponent
import com.example.barta.util.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.delay
import com.example.barta.data.getPreparationText

@Composable
fun PlayerScreen(videoId: String, navController: NavController) {
    var descriptionText by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val tracker = remember { YouTubePlayerTracker() }
    val handler = remember { Handler(Looper.getMainLooper()) }
    val youTubePlayerRef = remember { mutableStateOf<YouTubePlayer?>(null) }

    var steps by remember { mutableStateOf<List<Step>>(emptyList()) }
    var transcripts by remember { mutableStateOf<List<TranscriptItem>>(emptyList()) }
    var currentStepIndex by remember { mutableStateOf(-1) } // 시작은 description

    var listeningText by remember { mutableStateOf("인식 대기중...") }
    var isCommandMode by remember { mutableStateOf(false) }

    val summaries = remember { mutableStateListOf<String>() }
    val timers = getTimersOnly()
    val apiKey = BuildConfig.YOUTUBE_API_KEY

    LaunchedEffect(videoId) {
        val description = fetchYoutubeDescription(videoId, apiKey)
        descriptionText = description

        val fetchedSteps = parseChaptersFromDescription(description)
        val fetchedTranscripts = fetchTranscript(videoId)

        steps = fetchedSteps
        transcripts = fetchedTranscripts
    }

    LaunchedEffect(steps) {
        if (steps.isEmpty()) return@LaunchedEffect
        summaries.clear()
        val defaultSummaries = getSummariesOnly()
        steps.forEachIndexed { index, _ ->
            val summary = defaultSummaries.getOrElse(index) { "기본적인 요리 과정입니다." }
            summaries.add(summary)
        }
    }

    val sttController = remember {
        STTController(
            context,
            onCommandDetected = { command ->
                if (isCommandMode) {
                    when {
                        command.contains("멈춰", ignoreCase = true) -> {}
                        command.contains("다음", ignoreCase = true) -> {
                            if (currentStepIndex < steps.lastIndex) {
                                currentStepIndex++
                                if (currentStepIndex >= 0) {
                                    youTubePlayerRef.value?.seekTo(steps[currentStepIndex].startTime)
                                }
                            }
                        }
                        command.contains("이전", ignoreCase = true) -> {
                            if (currentStepIndex > -1) {
                                currentStepIndex--
                                if (currentStepIndex >= 0) {
                                    youTubePlayerRef.value?.seekTo(steps[currentStepIndex].startTime)
                                }
                            }
                        }
                    }
                    isCommandMode = false
                    youTubePlayerRef.value?.play()
                }
            },
            onListeningText = { text -> listeningText = text },
            onWakeWordDetected = {
                youTubePlayerRef.value?.pause()
                isCommandMode = true
            }
        )
    }

    LaunchedEffect(Unit) { sttController.startListening() }

    LaunchedEffect(currentStepIndex) {
        if (currentStepIndex >= 0) {
            youTubePlayerRef.value?.loadVideo(videoId, steps[currentStepIndex].startTime)
        }
    }

    LaunchedEffect(currentStepIndex, youTubePlayerRef.value) {
        while (true) {
            delay(500)
            val player = youTubePlayerRef.value ?: continue
            if (currentStepIndex >= 0) {
                val currentTime = tracker.currentSecond
                val currentStep = steps.getOrNull(currentStepIndex) ?: continue
                val endTime = currentStep.endTime
                if (currentTime >= endTime) {
                    player.seekTo(currentStep.startTime)
                }
            }
        }
    }

    if (steps.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Box(modifier = Modifier.fillMaxWidth()) {
        val videoModifier = Modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)

        AndroidView(
            modifier = videoModifier,
            factory = { ctx ->
                YouTubePlayerView(ctx).apply {
                    addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(player: YouTubePlayer) {
                            youTubePlayerRef.value = player
                            player.addListener(tracker)
                        }
                    })
                }
            }
        )

        if (currentStepIndex == -1) {
            val prepText = getPreparationText(videoId)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .background(MaterialTheme.colors.surface.copy(alpha = 0.7f))
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = prepText,
                        style = MaterialTheme.typography.body1
                    )
                }
            }
        }


        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BartaIcon(modifier = Modifier.size(48.dp))

            Spacer(modifier = Modifier.width(8.dp))

            if (currentStepIndex >= 0) {
                val stepTimer = timers.getOrNull(currentStepIndex)
                val timerSize = 48.dp

                if (stepTimer != null) {
                    Box(
                        modifier = Modifier
                            .size(timerSize)
                            .background(Color.White, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        TimerComponent(
                            totalTime = stepTimer,
                            modifier = Modifier.size(timerSize)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp),
            horizontalAlignment = Alignment.End
        ) {
            Button(onClick = {
                if (currentStepIndex > -1) {
                    currentStepIndex--
                    if (currentStepIndex >= 0) {
                        youTubePlayerRef.value?.seekTo(steps[currentStepIndex].startTime)
                    }
                }
            }) {
                Text("이전")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                if (currentStepIndex < steps.lastIndex) {
                    currentStepIndex++
                    if (currentStepIndex >= 0) {
                        youTubePlayerRef.value?.seekTo(steps[currentStepIndex].startTime)
                    }
                } else if (currentStepIndex == -1 && steps.isNotEmpty()) {
                    currentStepIndex = 0
                    youTubePlayerRef.value?.seekTo(steps[0].startTime)
                } else {
                    showDialog = true
                }
            }) {
                Text("다음")
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            if (currentStepIndex >= 0) {
                RecipeSubtitle(
                    stepNumber = currentStepIndex + 1,
                    description = summaries.getOrNull(currentStepIndex) ?: "요약 없음",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                ProgressBarComponet(
                    progress = (currentStepIndex + 1).toFloat() / steps.size.toFloat(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
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
                    currentStepIndex = -1
                    youTubePlayerRef.value?.seekTo(0f)
                    showDialog = false
                }) {
                    Text("영상 다시 보기")
                }
            },
            dismissButton = {
                Button(onClick = {
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