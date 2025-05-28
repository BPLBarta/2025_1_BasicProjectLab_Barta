package com.example.barta.util

// 각 스텝 요약 + 타이머 초(seconds). null이면 타이머 없음
fun getDefaultStepSummaries(): List<Pair<String, Int?>> {
    return listOf(
        "재료 손질을 합니다." to null,
        "팬에 재료를 볶습니다." to null,
        "양념을 넣고 졸입니다." to null,
        "불을 끄고 마무리합니다." to null,  // 타이머 없음
        "접시에 담아 완성합니다." to 45
    )
}

// 요약만 따로 뽑기
fun getSummariesOnly(): List<String> = getDefaultStepSummaries().map { it.first }

// 타이머 시간만 따로 뽑기
fun getTimersOnly(): List<Int?> = getDefaultStepSummaries().map { it.second }
