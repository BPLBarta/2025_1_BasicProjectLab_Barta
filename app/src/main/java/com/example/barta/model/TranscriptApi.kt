package com.example.barta.network

import com.example.barta.model.TranscriptItem
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object TranscriptApi {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun fetchTranscript(videoId: String): List<TranscriptItem> {
        val url = "http://10.0.2.2:8000/transcript/$videoId" // 에뮬레이터에서 localhost는 10.0.2.2
        return client.get(url).body()
    }
}
