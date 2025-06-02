package com.example.barta.model

import kotlinx.serialization.Serializable

@Serializable
data class TranscriptItem(
    val text: String,
    val start: Float,
    val duration: Float
)
