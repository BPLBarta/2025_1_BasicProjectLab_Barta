
package com.example.barta.data

fun getPreparationText(videoId: String): String {
    return when (videoId) {
        "_VqTaMZwj7I" -> "🧂 준비물: 소금, 계란, 식용유\n🍳 조리 도구: 프라이팬, 주걱"
        "def456" -> "🥩 준비물: 소고기, 마늘, 간장\n🍽 조리 도구: 칼, 도마"
        "gpt001" -> "🍝 준비물: 파스타면, 토마토소스, 양파\n🔪 조리 도구: 냄비, 팬, 칼"
        else -> "준비물이 등록되지 않았습니다."
    }
}
