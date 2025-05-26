package com.example.barta.util

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import io.ktor.serialization.kotlinx.json.json

@Serializable
data class TranscriptLine(
    val text: String,
    val start: Float,
    val duration: Float
)

// 기존 transcript 기반 fetch
suspend fun fetchTranscript(videoId: String): List<TranscriptLine> {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }
    // ※※※ 여기에 자기 주소 해야함 중요 ※※※
    val response: HttpResponse = client.get("http://127.0.0.1:8000/transcript/$videoId")
    return response.body()
}

// 챕터 정보
data class Chapter(val title: String, val startTime: Float, val endTime: Float)

// 요약된 스텝
data class TranscriptStep(val title: String, val startTime: Float, val endTime: Float)

// 챕터 기준으로 자막 요약
fun transcriptLinesToStepsByChapters(
    lines: List<TranscriptLine>,
    chapters: List<Chapter>
): List<TranscriptStep> {
    return chapters.map { chapter ->
        val matchingLines = lines.filter {
            it.start >= chapter.startTime && it.start < chapter.endTime
        }
        val summary = summarizeTranscript(matchingLines)
        TranscriptStep(
            title = summary,
            startTime = chapter.startTime,
            endTime = chapter.endTime
        )
    }
}

// 자막 요약: 간단하게 텍스트를 합쳐서 앞부분 자르기
fun summarizeTranscript(lines: List<TranscriptLine>): String {
    return lines.joinToString(" ") { it.text }
        .replace("\n", " ")
        .take(30) + "..."
}
