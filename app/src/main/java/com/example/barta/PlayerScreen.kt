package com.example.barta

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.example.barta.util.Step
import com.example.barta.util.fetchYoutubeDescription
import com.example.barta.util.parseChaptersFromDescription
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.barta.util.*
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.example.barta.util.getDefaultStepSummaries



@Composable
fun PlayerScreen(videoId: String, navController: NavController) {
    val tracker = remember { YouTubePlayerTracker() }
    val handler = remember { Handler(Looper.getMainLooper()) }
    val youTubePlayerRef = remember { mutableStateOf<YouTubePlayer?>(null) }

    var steps by remember { mutableStateOf<List<Step>>(emptyList()) }
    var transcripts by remember { mutableStateOf<List<TranscriptItem>>(emptyList()) }
    var currentStepIndex by remember { mutableStateOf(0) }
    var showDialog by remember { mutableStateOf(false) }

    // 🎤 STT 상태 변수 추가
    var listeningText by remember { mutableStateOf("인식 대기중...") }
    var isCommandMode by remember { mutableStateOf(false) }

    val apiKey = BuildConfig.YOUTUBE_API_KEY

    // ✅ 자막과 챕터 가져오기
    LaunchedEffect(videoId) {
        val description = fetchYoutubeDescription(videoId, apiKey)
        val fetchedSteps = parseChaptersFromDescription(description)
        val fetchedTranscripts = fetchTranscript(videoId)

        steps = fetchedSteps
        transcripts = fetchedTranscripts
    }

//       ✅ step별 자막 정리 후 요약 요청
//    향후 필요한 주석입니다.
//    LaunchedEffect(steps, transcripts) {
//        if (steps.isEmpty() || transcripts.isEmpty()) return@LaunchedEffect
//        val grouped = groupTranscriptByStep(steps, transcripts)
//        summaries.clear()
//        grouped.forEach { (_, lines) ->
//            val merged = mergeTranscriptText(lines)
//            val summary = fetchSummary(merged)
//            summaries.add(summary)
//        }
//    }
    LaunchedEffect(steps) {
        if (steps.isEmpty()) return@LaunchedEffect

        summaries.clear()

        val defaultSummaries = getDefaultStepSummaries()

        steps.forEachIndexed { index, _ ->
            val summary = defaultSummaries.getOrElse(index) { "기본적인 요리 과정입니다." }
            summaries.add(summary)
        }
    }


    // ✅ 영상 반복 재생 루프
    val repeatRunnable = remember {
        object : Runnable {
            override fun run() {
                val current = tracker.currentSecond
                val currentStep = steps.getOrNull(currentStepIndex)
                if (currentStep != null) {
                    if (current >= currentStep.endTime) {
                        youTubePlayer?.seekTo(currentStep.startTime)

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

    // ✅ 로딩 화면
    if (steps.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    // ✅ 전체 UI
    Column(modifier = Modifier.fillMaxSize()) {
        // 영상 플레이어
        AndroidView(
            modifier = Modifier.height(200.dp),
            factory = { ctx ->
                YouTubePlayerView(ctx).apply {
                    addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                        override fun onReady(player: YouTubePlayer) {
                            youTubePlayerRef.value = player
                            player.addListener(tracker)
                            player.loadVideo(videoId, steps.firstOrNull()?.startTime ?: 0f)
                        }
                    })
                }
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
                    youTubePlayerRef.value?.seekTo(steps[currentStepIndex].startTime)
                }
            }) {
                Text("이전")
            }

            Button(onClick = {
                if (currentStepIndex < steps.lastIndex) {
                    currentStepIndex++
                    youTubePlayerRef.value?.seekTo(steps[currentStepIndex].startTime)
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

        Divider(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            text = "📝 요약 내용",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
// 향후 필요한 주석입니다.
//        LazyColumn(modifier = Modifier.fillMaxHeight(0.3f)) {
//            itemsIndexed(summaries) { index, summary ->
//                Text(
//                    text = "Step ${index + 1}: $summary",
//                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
//                    style = MaterialTheme.typography.body2
//                )
//            }
//        }
        // ✅ 현재 스텝 요약만 표시
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth(),
            elevation = 4.dp
        ) {
            Text(
                text = summaries.getOrNull(currentStepIndex)
                    ?: "⚠️ 현재 스텝에 대한 요약이 없습니다.",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.body2
            )
        }
    }

    // ✅ 요리 완료 알림창
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("요리를 완성하였습니다!") },
            text = { Text("영상으로 다시 돌아가거나 홈 화면으로 이동할 수 있어요.") },
            confirmButton = {
                Button(onClick = {
                    currentStepIndex = 0
                    youTubePlayerRef.value?.seekTo(steps.firstOrNull()?.startTime ?: 0f)
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
