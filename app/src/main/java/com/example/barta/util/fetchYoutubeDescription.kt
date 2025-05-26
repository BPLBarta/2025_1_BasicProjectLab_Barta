package com.example.barta.util

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.*

suspend fun fetchYoutubeDescription(videoId: String, apiKey: String): String {
    val client = HttpClient(CIO)
    val url = "https://www.googleapis.com/youtube/v3/videos"

    return try {
        val response: HttpResponse = client.get(url) {
            url {
                parameters.append("part", "snippet")
                parameters.append("id", videoId)
                parameters.append("key", apiKey)
            }
        }

        val responseBody = response.bodyAsText()
        val json = Json.parseToJsonElement(responseBody).jsonObject

        val description = json["items"]
            ?.jsonArray?.firstOrNull()
            ?.jsonObject?.get("snippet")
            ?.jsonObject?.get("description")
            ?.jsonPrimitive?.content

        description ?: ""
    } catch (e: Exception) {
        ""
    } finally {
        client.close()
    }
}
