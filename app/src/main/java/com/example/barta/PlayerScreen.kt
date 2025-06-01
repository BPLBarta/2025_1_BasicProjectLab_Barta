@file:OptIn(ExperimentalComposeUiApi::class)

package com.example.barta

import android.content.Context
import android.speech.SpeechRecognizer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import coil.compose.AsyncImage
import com.example.barta.data.getPreparationText
import com.example.barta.ui.component.*
import com.example.barta.ui.theme.LocalBartaPalette
import com.example.barta.util.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.delay
import com.example.barta.ui.screen.PreparationScreen


class VoiceRecognizer(private val onResult: (String) -> Unit) {
    private var recognizer: SpeechRecognizer? = null

    fun start(context: Context) {
        recognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : SimpleRecognitionListener() {
                override fun onResults(results: android.os.Bundle?) {
                    val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    matches?.firstOrNull()?.let { onResult(it) }
                }
            })
        }

        val intent = android.content.Intent(android.speech.RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE_MODEL, android.speech.RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(android.speech.RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
        }

        recognizer?.startListening(intent)
    }

    fun stop() {
        recognizer?.stopListening()
        recognizer?.cancel()
        recognizer?.destroy()
    }
}

open class SimpleRecognitionListener : android.speech.RecognitionListener {
    override fun onReadyForSpeech(params: android.os.Bundle?) {}
    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {}
    override fun onError(error: Int) {}
    override fun onResults(results: android.os.Bundle?) {}
    override fun onPartialResults(partialResults: android.os.Bundle?) {}
    override fun onEvent(eventType: Int, params: android.os.Bundle?) {}
}

@Composable
fun PlayerScreen(videoId: String, navController: NavController) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
    val color = LocalBartaPalette.current

    val tracker = remember { YouTubePlayerTracker() }
    val youTubePlayerRef = remember { mutableStateOf<YouTubePlayer?>(null) }

    var steps by remember { mutableStateOf<List<Step>>(emptyList()) }
    var currentStepIndex by remember { mutableStateOf(-1) }
    val summaries = remember { mutableStateListOf<String>() }
    val timers = remember { mutableStateListOf<Int?>() }
    var showSubtitle by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(true) }
    var timerRunning by remember { mutableStateOf(false) }
    var isListening by remember { mutableStateOf(false) }


    val voiceRecognizer = remember {
        VoiceRecognizer(
            context = context,
            onStart = { isListening = true },
            onResult = { command ->
                when (command.trim()) {
                    "다음" -> {
                        if (currentStepIndex < steps.lastIndex) {
                            currentStepIndex++
                        } else {
                            showDialog = true
                        }
                    }
                    "이전" -> currentStepIndex = if (currentStepIndex > 0) currentStepIndex - 1 else -1
                    "시작" -> {
                        youTubePlayerRef.value?.play()
                        isPlaying = true
                    }
                    "멈춰" -> {
                        youTubePlayerRef.value?.pause()
                        isPlaying = false
                    }
                    "타이머 시작" -> timerRunning = true
                    "타이머 멈춰" -> timerRunning = false
                    "두 단계 뒤" -> if (currentStepIndex + 2 <= steps.lastIndex) currentStepIndex += 2
                    "마지막 과정" -> currentStepIndex = steps.lastIndex
                    "타이머 과정" -> {
                        val withTimer = timers.indexOfFirst { it != null }
                        if (withTimer != -1) currentStepIndex = withTimer
                    }
                    "자막 꺼", "자막 꺼줘", "자막 꺼주세요" -> showSubtitle = false
                    "자막 켜", "자막 켜줘", "자막 켜주세요" -> showSubtitle = true
                    
                    "요리 끝" -> {
                        currentStepIndex = steps.lastIndex
                        showDialog = true
                    }
                    "홈으로" -> navController.navigate("home") {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                    "대쉬보드로" -> navController.navigate("dashboard") {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            onEnd = { isListening = false }
        )
    }


    LaunchedEffect(videoId) {
        val description = fetchYoutubeDescription(videoId, BuildConfig.YOUTUBE_API_KEY)
        steps = parseChaptersFromDescription(description)
    }

    LaunchedEffect(steps) {
        if (steps.isEmpty()) return@LaunchedEffect
        summaries.clear()
        timers.clear()
        val defaultSummaries = getStepSummaries(videoId)
        steps.forEachIndexed { index, _ ->
            val (summary, timerSec) = defaultSummaries.getOrElse(index) {
                "기본적인 요리 과정입니다." to null
            }
            summaries.add(summary)
            timers.add(timerSec)
        }
    }

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
                if (currentTime >= currentStep.endTime) {
                    player.seekTo(currentStep.startTime)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        val rightTimer = timers.getOrNull(currentStepIndex)

        if (isListening) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp)
                    .zIndex(5f),
                contentAlignment = Alignment.TopCenter
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xCC000000),
                    elevation = 6.dp
                ) {
                    Text(
                        text = "🎤 음성 인식 중입니다...",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.body2,
                        color = Color.White
                    )
                }
            }
        }

        if (rightTimer != null && currentStepIndex >= 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
                    .zIndex(2f)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Spacer(modifier = Modifier.width(8.dp))

//                    // ⏱ 타이머 컴포넌트 (살짝 크게)
//                    TimerComponent(
//                        totalTime = rightTimer,
//                        isRunning = timerRunning,
//                        modifier = Modifier.size(52.dp)
//                    )
                }
            }
        }

        // ▶️ 영상 재생 화면
        if (currentStepIndex >= 0) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val screenHeight = maxHeight
                val calculatedWidth = screenHeight * 16f / 9f
                val videoModifier = if (isPortrait)
                    Modifier.fillMaxWidth().aspectRatio(16f / 9f)
                else
                    Modifier.width(calculatedWidth).height(screenHeight)

                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Box(modifier = videoModifier.zIndex(0f)) {
                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { ctx ->
                                YouTubePlayerView(ctx).apply {
                                    addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                                        override fun onReady(player: YouTubePlayer) {
                                            youTubePlayerRef.value = player
                                            player.addListener(tracker)
                                            if (steps.isNotEmpty() && currentStepIndex >= 0) {
                                                player.loadVideo(videoId, steps[currentStepIndex].startTime)
                                            }
                                        }
                                    })
                                }
                            }
                        )

                        // ✅ 여기 추가 (영상 안의 왼쪽 상단)
                        val rightTimer = timers.getOrNull(currentStepIndex)

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                                .zIndex(1f)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                BartaIcon(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clickable {
                                            isListening = true
                                            voiceRecognizer.start()
                                        }
                                )

                                if (rightTimer != null && currentStepIndex >= 0) {
                                    Spacer(modifier = Modifier.width(8.dp))

                                    TimerComponent(
                                        totalTime = rightTimer,
                                        isRunning = timerRunning,
                                        modifier = Modifier.size(44.dp) // 타이머 크기 살짝 조정
                                    )
                                }
                            }
                        }

                        // ⬇️ 하단 자막 + 프로그레스바
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(8.dp)
                                .zIndex(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            if (showSubtitle) {
                                RecipeSubtitle(
                                    stepNumber = currentStepIndex + 1,
                                    description = summaries.getOrNull(currentStepIndex) ?: "요약 없음",
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                                )
                            }
                            ProgressBarComponet(
                                progress = (currentStepIndex + 1).toFloat() / steps.size.toFloat(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                        }
                    }
                }

            }
        }

        // 🧄 준비 화면
        if (currentStepIndex == -1) {
            PreparationScreen(videoId = videoId, onMicClick = { voiceRecognizer.start() })
        }


    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            backgroundColor = Color(0xFFEFEFEF),
            shape = RoundedCornerShape(15.dp),

            buttons = {
                Column(
                    modifier = Modifier
                        .width(289.dp)
                        .padding(top = 36.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "요리가 완성 되었습니다!\n홈 화면으로 돌아가시겠어요?",
                        style = MaterialTheme.typography.subtitle1,
                        color = color.textBlack,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .fillMaxWidth()
                    )

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
                            backgroundColor = color.primaryOrange1,
                            contentColor = color.textWhite
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .width(136.dp)
                            .height(36.dp)
                    ) {
                        Text(
                            text = "돌아가기",
                            style = MaterialTheme.typography.subtitle1
                        )
                    }
                }
            }
        )
    }

}
