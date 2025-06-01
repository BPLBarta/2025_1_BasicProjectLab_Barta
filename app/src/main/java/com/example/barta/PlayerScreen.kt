// 파일: com/example/barta/PlayerScreen.kt

@file:OptIn(ExperimentalComposeUiApi::class)

package com.example.barta

import androidx.compose.foundation.background
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
import com.example.barta.ui.component.BartaIcon
import com.example.barta.ui.component.ProgressBarComponet
import com.example.barta.ui.component.RecipeSubtitle
import com.example.barta.ui.component.TimerComponent
import com.example.barta.ui.theme.LocalBartaPalette
import com.example.barta.util.fetchYoutubeDescription
import com.example.barta.util.parseChaptersFromDescription
import com.example.barta.util.getStepSummaries

import com.example.barta.util.Step
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.delay
import com.example.barta.data.getPreparationText

@Composable
fun PlayerScreen(videoId: String, navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var showSubtitle by remember { mutableStateOf(true) } // 자막 표시 여부
    val context = LocalContext.current

    // YouTubePlayer 및 트래커
    val tracker = remember { YouTubePlayerTracker() }
    val youTubePlayerRef = remember { mutableStateOf<YouTubePlayer?>(null) }

    // 챕터 리스트와 현재 인덱스
    var steps by remember { mutableStateOf<List<Step>>(emptyList()) }
    var currentStepIndex by remember { mutableStateOf(-1) }

    // 챕터별 요약과 타이머 설정
    val summaries = remember { mutableStateListOf<String>() }
    val timers = remember { mutableStateListOf<Int?>() }

    // 화면 회전 여부
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT

    // 색 팔레트
    val color = LocalBartaPalette.current

    // 1) videoId 변경 시 유튜브 설명 → steps 파싱
    LaunchedEffect(videoId) {
        val description = fetchYoutubeDescription(videoId, BuildConfig.YOUTUBE_API_KEY)
        steps = parseChaptersFromDescription(description)
    }

    // 2) steps 변경 시 summaries / timers 초기화
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

    // 3) currentStepIndex 변경 시 해당 챕터 loadVideo
    LaunchedEffect(currentStepIndex) {
        if (currentStepIndex >= 0) {
            youTubePlayerRef.value?.loadVideo(videoId, steps[currentStepIndex].startTime)
        }
    }

    // 4) 재생 중 챕터 구간 끝나면 자동 반복
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

    // 5) UI 구성
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // (A) 오른쪽 상단: “이전 / 다음” 버튼 + 우측 타이머 + 자막 토글 버튼
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp, end = 16.dp)
                .wrapContentSize(Alignment.TopEnd)
                .zIndex(10f),
            horizontalAlignment = Alignment.End
        ) {
            // 이전 버튼
            Button(
                onClick = {
                    if (currentStepIndex > 0) {
                        currentStepIndex--
                    } else if (currentStepIndex == 0) {
                        currentStepIndex = -1
                    }
                },
                modifier = Modifier.height(36.dp)
            ) {
                Text("이전", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 다음 버튼
            Button(
                onClick = {
                    if (steps.isNotEmpty()) {
                        when {
                            currentStepIndex == -1 -> {
                                currentStepIndex = 0
                                youTubePlayerRef.value?.loadVideo(videoId, steps[0].startTime)
                            }
                            currentStepIndex < steps.lastIndex -> {
                                currentStepIndex++
                                youTubePlayerRef.value?.loadVideo(videoId, steps[currentStepIndex].startTime)
                            }
                            else -> {
                                showDialog = true
                            }
                        }
                    }
                },
                modifier = Modifier.height(36.dp)
            ) {
                Text("다음", fontSize = 14.sp)
            }

            // 우측 타이머
            val rightTimer = timers.getOrNull(currentStepIndex)
            if (rightTimer != null && currentStepIndex >= 0) {
                Spacer(modifier = Modifier.height(12.dp))
                TimerComponent(
                    totalTime = rightTimer,
                    modifier = Modifier.size(64.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 자막 토글 버튼 (이전/다음 버튼과 같은 높이)
            Button(
                onClick = { showSubtitle = !showSubtitle },
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = if (showSubtitle) "자막 끄기" else "자막 켜기",
                    fontSize = 12.sp
                )
            }
        }

        // (B) 준비물 화면: currentStepIndex == -1
        if (currentStepIndex == -1) {
            val prepData = getPreparationText(videoId)
            val prepText = prepData.ingredients

            // 문자열 줄 단위로 나눈 뒤 양쪽 두 열로 표시
            val lines = prepText.lines().filter { it.isNotBlank() }
            val mid = lines.size / 2
            val leftLines = lines.subList(0, mid).joinToString("\n")
            val rightLines = lines.subList(mid, lines.size).joinToString("\n")

            Box(modifier = Modifier.fillMaxSize()) {
                // 1) 배경: 유튜브 썸네일 + 반투명 검은 오버레이
                AsyncImage(
                    model = "https://img.youtube.com/vi/$videoId/0.jpg",
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))

                // 2) 상단 중앙 “요리재료” 제목
                Text(
                    text = "요리재료",
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .align(Alignment.TopCenter),
                    textAlign = TextAlign.Center
                )

                // 3) 준비물 내용 두 칼럼
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 56.dp, start = 16.dp, end = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(491.dp)
                            .heightIn(min = 330.dp)
                            .background(Color(0xFFFCECD7), RoundedCornerShape(10.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = leftLines,
                                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = rightLines,
                                style = MaterialTheme.typography.subtitle1.copy(fontWeight = FontWeight.Bold),
                                color = Color.Black,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

        } else {
            // (C) 챕터 재생 화면: currentStepIndex >= 0
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val screenHeight = maxHeight
                val calculatedWidth = screenHeight * 16f / 9f
                val videoModifier = if (isPortrait)
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                else
                    Modifier
                        .width(calculatedWidth)
                        .height(screenHeight)

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // (C1) YouTubePlayerView
                    Box(modifier = videoModifier.zIndex(0f)) {
                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { ctx ->
                                YouTubePlayerView(ctx).apply {
                                    addYouTubePlayerListener(
                                        object : AbstractYouTubePlayerListener() {
                                            override fun onReady(player: YouTubePlayer) {
                                                youTubePlayerRef.value = player
                                                player.addListener(tracker)
                                                if (currentStepIndex >= 0 && steps.isNotEmpty()) {
                                                    player.loadVideo(videoId, steps[currentStepIndex].startTime)
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        )

                        // (C2) 영상 왼쪽 상단: Barta 아이콘만 표시
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(12.dp)
                                .zIndex(1f)
                        ) {
                            BartaIcon(modifier = Modifier.size(40.dp))
                        }

                        // (C3) 하단: 자막은 showSubtitle에 따라, 프로그레스바는 항상 표시
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
    }

    // (D) 완료 알림 다이얼로그
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            backgroundColor = Color(0xFFEFEFEF),
            modifier = Modifier
                .width(289.dp)
                .heightIn(min = 130.dp),
            shape = RoundedCornerShape(15.dp),
            title = {
                Text(
                    text = "요리가 완성되었습니다!\n홈 화면으로 돌아가시겠어요?",
                    style = MaterialTheme.typography.subtitle2,
                    color = color.textBlack,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
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
                            backgroundColor = color.primaryOrange1,
                            contentColor = color.textWhite
                        ),
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .width(136.dp)
                            .height(36.dp)
                    ) {
                        Text("돌아가기", style = MaterialTheme.typography.subtitle2)
                    }
                }
            },
            dismissButton = {}
        )
    }
}
