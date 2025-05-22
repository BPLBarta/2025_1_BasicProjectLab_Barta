package com.example.barta.ui.player

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.barta.databinding.ActivityPlayerBinding
import com.example.barta.ui.step.Step
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker

class PlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayerBinding
    private lateinit var youTubePlayer: YouTubePlayer
    private val tracker = YouTubePlayerTracker()
    private val handler = Handler(Looper.getMainLooper())

    private lateinit var steps: List<Step>
    private var currentStepIndex = 0
    private var repeatCount = 0
    private val maxRepeatsPerStep = 2 // 🔁 각 step을 몇 번 반복할지

    private val repeatRunnable = object : Runnable {
        override fun run() {
            val currentTime = tracker.currentSecond
            val step = steps[currentStepIndex]

            if (currentTime < 0f) {
                handler.postDelayed(this, 1000)
                return
            }

            // 반복 조건
            if (currentTime >= step.endTime && (currentTime - step.endTime) < 1f) {
                if (repeatCount < maxRepeatsPerStep - 1) {
                    repeatCount++
                    youTubePlayer.seekTo(step.startTime)
                    Log.d("StepRepeat", "Step ${currentStepIndex + 1} 반복: $repeatCount 회")
                } else {
                    moveToNextStep()
                }
            }

            handler.postDelayed(this, 1000)
        }
    }

    private fun moveToNextStep() {
        repeatCount = 0
        currentStepIndex++

        if (currentStepIndex >= steps.size) {
            Toast.makeText(this, "모든 step을 완료했습니다.", Toast.LENGTH_SHORT).show()
            handler.removeCallbacks(repeatRunnable)
            return
        }

        val nextStep = steps[currentStepIndex]
        youTubePlayer.seekTo(nextStep.startTime)
        Log.d("StepMove", "➡️ 다음 step ${currentStepIndex + 1} 시작")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val youtubeUrl = intent.getStringExtra("youtube_url") ?: ""
        val videoId = extractVideoId(youtubeUrl)

        if (videoId.isEmpty()) {
            Toast.makeText(this, "유효하지 않은 유튜브 링크입니다", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // 하드코딩된 step 리스트
        steps = listOf(
            Step("Step 1: 재료 준비", 0f, 10f),
            Step("Step 2: 팬 달구기", 10f, 20f),
            Step("Step 3: 고기 굽기", 20f, 35f)
        )

        val playerView = binding.youtubePlayerView
        lifecycle.addObserver(playerView)

        playerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(player: YouTubePlayer) {
                Log.d("Player", "✅ YouTubePlayer is READY")
                youTubePlayer = player
                youTubePlayer.addListener(tracker)

                val step = steps[currentStepIndex]
                youTubePlayer.loadVideo(videoId, step.startTime)
                handler.postDelayed(repeatRunnable, 1000)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(repeatRunnable)
    }

    private fun extractVideoId(url: String): String {
        return try {
            val uri = android.net.Uri.parse(url)
            when {
                url.contains("youtu.be") -> uri.lastPathSegment ?: ""
                url.contains("youtube.com") -> uri.getQueryParameter("v") ?: ""
                else -> ""
            }
        } catch (e: Exception) {
            ""
        }
    }

}
