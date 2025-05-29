package com.example.barta

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
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
import com.example.barta.ui.theme.LocalBartaPalette
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.platform.LocalConfiguration


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
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val screenWidth = maxWidth
            val screenHeight = maxHeight
            val configuration = LocalConfiguration.current
            val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

            val videoModifier = if (isPortrait) {
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            } else {
                val calculatedWidth = screenHeight * 16f / 9f
                Modifier
                    .width(calculatedWidth)
                    .height(screenHeight)
            }

            // 🔲 검은 배경 채우기
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
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
            }
        }


        // 재료화면
        if (currentStepIndex == -1) {
            val prepText = getPreparationText(videoId)
            val thumbnailUrl = "https://img.youtube.com/vi/$videoId/0.jpg"

            // 텍스트 2단 분할
            val lines = prepText.lines().filter { it.isNotBlank() }
            val mid = lines.size / 2
            val leftText = lines.subList(0, mid).joinToString("\n")
            val rightText = lines.subList(mid, lines.size).joinToString("\n")

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // 🔹 썸네일 배경
                AsyncImage(
                    model = thumbnailUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // 🔹 반투명 오버레이
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )

                // 🔹 중앙 정렬된 내용 박스
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(491.dp)
                            .heightIn(330.dp)
                            .background(
                                color = Color(0xFFFCECD7),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = leftText,
                                style = MaterialTheme.typography.subtitle1,
                                modifier = Modifier
                                    .weight(1f)
                                    .wrapContentHeight(align = Alignment.CenterVertically),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = rightText,
                                style = MaterialTheme.typography.subtitle1,
                                modifier = Modifier
                                    .weight(1f)
                                    .wrapContentHeight(align = Alignment.CenterVertically),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
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

    val color = LocalBartaPalette.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            backgroundColor = Color(0xFFEFEFEF),
            modifier = Modifier
                .width(289.dp) // 너비 조절
                .heightIn(min = 130.dp), // 높이 조절 (최소값만)// 다이얼로그 배경 색
            shape = RoundedCornerShape(15.dp), // 둥근 모서리

            title = {
                Text(
                    text = "요리가 완성 되었습니다!\n홈 화면으로 돌아가시겠어요?",
                    style = MaterialTheme.typography.subtitle2,
                    color = color.textBlack,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
                confirmButton = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Spacer(modifier = Modifier.height(30.dp))
                        Button(
                        onClick = {
                            navController.navigate("home") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = color.primaryOrange1, // 오렌지색
                            contentColor = color.textWhite
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .width(136.dp)   // ✅ 버튼 너비
                            .height(36.dp)   // ✅ 버튼 높이
                    ) {
                        Text(
                            text = "돌아가기",
                            style = MaterialTheme.typography.subtitle2
                        )
                    }
                }
            },
            dismissButton = {}
        )
    }
}

fun formatTime(seconds: Float): String {
    val total = seconds.toInt()
    val minutes = total / 60
    val sec = total % 60
    return "%02d:%02d".format(minutes, sec)
}