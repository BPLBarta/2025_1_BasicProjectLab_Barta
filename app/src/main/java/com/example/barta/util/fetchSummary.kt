package com.example.barta.util

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

suspend fun fetchSummary(text: String): String {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val response = client.post("http://10.0.2.2:8001/summarize") {
        contentType(ContentType.Application.Json)
        setBody(mapOf("text" to text))
    }

    val json = response.body<JsonObject>()
    return json["summary"]?.jsonPrimitive?.content ?: ""
}
