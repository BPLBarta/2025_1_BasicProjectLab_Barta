package com.example.barta.util

import java.util.concurrent.TimeUnit

// Step 데이터 클래스
data class Step(
    val title: String,
    val startTime: Float,
    val endTime: Float
)

// 챕터 파싱 함수
fun parseChaptersFromDescription(description: String): List<Step> {
    val regex = Regex("""(\d{1,2}:\d{2}(?::\d{2})?)\s+(.+)""")
    val matches = regex.findAll(description).map {
        val (timeStr, title) = it.destructured
        timeToSeconds(timeStr) to title
    }.toList()

    val steps = mutableListOf<Step>()
    for (i in matches.indices) {
        val start = matches[i].first
        val title = matches[i].second
        val end = if (i < matches.lastIndex) matches[i + 1].first else Float.MAX_VALUE
        steps.add(Step(title, start, end))
    }
    return steps
}

// 시간 문자열(HH:MM:SS 또는 MM:SS)을 초 단위 Float로 변환
fun timeToSeconds(time: String): Float {
    val parts = time.split(":").map { it.toInt() }
    return when (parts.size) {
        2 -> (parts[0] * 60 + parts[1]).toFloat()
        3 -> (parts[0] * 3600 + parts[1] * 60 + parts[2]).toFloat()
        else -> 0f
    }
}
