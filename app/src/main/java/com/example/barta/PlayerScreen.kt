package com.example.barta

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PlayerScreen(videoId: String) {
    val tracker = remember { YouTubePlayerTracker() }
    val currentStep = remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    val steps = remember {
        listOf(
            Step("Step 1", 0f, 10f),
            Step("Step 2", 10f, 20f),
            Step("Step 3", 20f, 35f)
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(factory = { context ->
            YouTubePlayerView(context).apply {
                enableAutomaticInitialization = false

                addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(player: YouTubePlayer) {
                        player.addListener(tracker)
                        player.loadVideo(videoId, steps[currentStep.value].startTime)

                        scope.launch {
                            while (true) {
                                delay(1000)
                                val now = tracker.currentSecond
                                val (start, end) = steps[currentStep.value].let { it.startTime to it.endTime }
                                if (now >= end && now - end < 1f) {
                                    player.seekTo(start)
                                }
                            }
                        }
                    }
                })
            }
        })
    }
}

data class Step(
    val title: String,
    val startTime: Float,
    val endTime: Float
)
