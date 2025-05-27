package com.example.barta.util

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlinx.coroutines.*

@Serializable
data class YoutubeApiResponse(val items: List<YoutubeVideoItem>)

@Serializable
data class YoutubeVideoItem(val snippet: YoutubeSnippet)

@Serializable
data class YoutubeSnippet(
    val title: String,
    val description: String,
    val thumbnails: YoutubeThumbnails
)

@Serializable
data class YoutubeThumbnails(val medium: YoutubeThumbnailInfo)

@Serializable
data class YoutubeThumbnailInfo(val url: String)

data class YoutubeVideoMeta(
    val title: String,
    val thumbnailUrl: String
)

suspend fun fetchYoutubeMeta(videoId: String, apiKey: String): YoutubeVideoMeta? {
    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    val url = "https://www.googleapis.com/youtube/v3/videos?part=snippet&id=$videoId&key=$apiKey"

    return try {
        val response: HttpResponse = client.get(url)
        val body: YoutubeApiResponse = response.body()
        val snippet = body.items.firstOrNull()?.snippet
        snippet?.let {
            YoutubeVideoMeta(
                title = it.title,
                thumbnailUrl = it.thumbnails.medium.url
            )
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    } finally {
        client.close()
    }
}
