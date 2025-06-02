package com.example.barta.data.store

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import com.example.barta.util.fetchYoutubeMeta
import com.example.barta.util.YoutubeVideoMeta
import com.example.barta.util.extractVideoId
import com.example.barta.BuildConfig


object LinkStore {
    private val Context.dataStore by preferencesDataStore(name = "barta_links")
    private val KEY_LINK_SET = stringSetPreferencesKey("youtube_links")

    var youtubeHistory = mutableListOf<SavedLink>()

    fun addUrl(url: String, context: Context) {
        val meta = fetchYoutubeMetaBlocking(url)
        val time = getCurrentFormattedTime()
        val saved = SavedLink(
            url = url,
            title = meta?.title ?: "제목 불러오기 실패",
            savedAt = time,
            thumbnailUrl = meta?.thumbnailUrl ?: ""
        )

        if (!youtubeHistory.any { it.url == url }) {
            youtubeHistory.add(0, saved)
            saveToDataStore(context)
        }
    }

    private fun saveToDataStore(context: Context) {
        runBlocking {
            context.dataStore.edit { prefs ->
                val set = youtubeHistory.map {
                    "${it.url}||${it.title}||${it.savedAt}||${it.thumbnailUrl}"
                }.toSet()
                prefs[KEY_LINK_SET] = set
            }
        }
    }

    fun loadFromDataStore(context: Context) {
        runBlocking {
            val prefs = context.dataStore.data.first()
            youtubeHistory = prefs[KEY_LINK_SET]?.map { str: String ->
                val parts = str.split("||")
                SavedLink(
                    url = parts.getOrElse(0) { "" },
                    title = parts.getOrElse(1) { "제목 없음" },
                    savedAt = parts.getOrElse(2) { "시간 없음" },
                    thumbnailUrl = parts.getOrElse(3) { "" }
                )
            }?.toMutableList() ?: mutableListOf()
        }
    }

    private fun getCurrentFormattedTime(): String {
        val formatter = java.text.SimpleDateFormat("yyyy.MM.dd HH:mm", java.util.Locale.getDefault())
        return formatter.format(java.util.Date())
    }

    private fun fetchYoutubeMetaBlocking(url: String): YoutubeVideoMeta? {
        val videoId = extractVideoId(url)
        return try {
            runBlocking {
                fetchYoutubeMeta(videoId, BuildConfig.YOUTUBE_API_KEY)
            }
        } catch (e: Exception) {
            null
        }
    }
}
