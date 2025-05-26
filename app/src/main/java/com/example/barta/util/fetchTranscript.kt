package com.example.barta.util

import android.util.Log
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        })
    }
}

@Serializable
data class TranscriptItem(
    val text: String,
    val start: Float,
    val duration: Float
)

suspend fun fetchTranscript(videoId: String): List<TranscriptItem> {
    val url = "http://10.0.2.2:8000/transcript/$videoId"
    return try {
        client.get(url).body() // 🔁 리스트 그대로 파싱
    } catch (e: Exception) {
        Log.e("fetchTranscript", "자막 파싱 실패: ${e.message}")
        emptyList()
    }
}
