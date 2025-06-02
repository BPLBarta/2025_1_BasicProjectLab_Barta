package com.example.barta.util

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import com.example.barta.util.SummaryRequest
import com.example.barta.util.SummaryResponse

private val summaryClient = HttpClient {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}

suspend fun fetchStepSummaries(stepTexts: List<String>): List<String> {
    val response = summaryClient.post("http://10.0.2.2:8001/summary") {
        contentType(ContentType.Application.Json)
        setBody(SummaryRequest(stepTexts))
    }
    val result = response.body<SummaryResponse>()
    return result.summaries
}
