package com.example.barta.util

// 각 스텝 요약 + 타이머 초(seconds). null이면 타이머 없음
fun getDefaultStepSummaries(): List<Pair<String, Int?>> {
    return listOf(
        "양념장을 하루 전날 미리 만들어 숙성시킵니다." to null,
        "부대찌개에 들어갈 재료들을 준비하고 손질합니다." to null,
        "재료들을 냄비에 보기 좋게 정리하여 담습니다." to null,
        "사골육수를 부어 센불에 끓인 뒤 약불로 줄여 5분간 더 끓입니다." to 300,
        "취향에 따라 다데기, 치즈, 김치를 추가하여 간을 맞춥니다." to null,
        "대파와 채소가 충분히 익어 흐물해지면 완성입니다." to null
    )
}


// 요약만 따로 뽑기
fun getSummariesOnly(): List<String> = getDefaultStepSummaries().map { it.first }

// 타이머 시간만 따로 뽑기
fun getTimersOnly(): List<Int?> = getDefaultStepSummaries().map { it.second }
