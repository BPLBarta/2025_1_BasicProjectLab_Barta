// SummaryModels.kt
package com.example.barta.util

import kotlinx.serialization.Serializable

@Serializable
data class SummaryRequest(
    val steps: List<String>
)

@Serializable
data class SummaryResponse(
    val summaries: List<String>
)
