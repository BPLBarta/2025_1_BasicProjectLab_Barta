package com.example.barta.util

// ✅ 클래스 없이 바로 top-level 함수로 작성
fun extractVideoId(url: String): String {
    val regex = Regex("(?:v=|be/|embed/)([\\w-]{11})")
    return regex.find(url)?.groupValues?.get(1) ?: ""
}

fun extractTitleFromDescription(description: String): String {
    return description.lines().firstOrNull()?.take(50) ?: "제목 없음"
}
