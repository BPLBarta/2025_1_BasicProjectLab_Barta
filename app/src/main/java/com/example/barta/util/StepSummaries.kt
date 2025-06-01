package com.example.barta.util

// 각 영상 ID별 스텝 요약 및 타이머를 반환하는 함수
fun getStepSummaries(videoId: String): List<Pair<String, Int?>> {
    return when (videoId) {
        "_VqTaMZwj7I" -> listOf(
            "고춧가루, 후추, 다진마늘, 생강, 소금, 미원을 섞어 양념장을 만듭니다." to null,
            "양파는 길게 채썰고, 소시지는 어슷썰기로 썬다." to null,
            "재료들을 냄비에 보기 좋게 정리하여 담습니다." to null,
            "사골육수를 부어 센불에 끓인 뒤 약불로 줄여 5분간 더 끓입니다." to 300,
            "취향에 따라 다데기, 치즈, 김치를 추가하여 간을 맞춥니다." to null,
            "대파와 채소가 충분히 익어 흐물해지면 완성입니다." to null,
            "식성에 따라 치즈, 김치, 다데기를 추가해 더 진한 맛으로 완성합니다." to null,
            "완성된 부대찌개는 간을 보며 치즈나 김치를 더해 입맛에 맞게 즐깁니다." to null
        )
        "rK3PdxlGTt8" -> listOf(
            "감바스 요리에 대한 인트로 및 특징 설명" to null,
            "파 손질 및 연한 부분만 사용하여 재료 준비" to null,
            "양송이버섯과 마늘 등 재료 추가 준비" to null,
            "바게트 보관법과 오일 간 맞추기 팁 소개" to null,
            "새우 해동 및 전분으로 불순물 제거" to null,
            "버터·허브·마늘로 간단한 토핑 양념 만들기" to null,
            "새우에 파프리카 파우더와 액젓 등 양념" to null,
            "대파에 소금과 조미료로 간하기" to null,
            "올리브유에 재료 익히며 먹기 준비" to null,
            "바게트에 감바스를 올려 본격 먹방 시작" to null,
            "남은 오일로 파스타 만들고 치즈와 바질 추가" to null,
            "요리 마무리 및 인생과 행복에 대한 이야기" to null,
            "바게트에 감바스를 올려 본격 먹방 시작" to null,
            "요리 마무리 및 인생과 행복에 대한 이야기" to null,
            )
        "Havl-7iMCgQ" -> listOf(
            "김치찌개 소개 및 재료 개요 설명" to null,
            "돼지고기 꼬들살 소개 및 썰기" to null,
            "고기 밑간 (맛술, 매실청, 국간장)" to null,
            "김치 찢어 냄비에 담기 및 김칫국물 준비" to null,
            "양파 썰고 들기름 넣은 후 고기 볶기" to null,
            "대파, 청양고추, 붉은 고추, 두부 썰기" to null,
            "쌀뜨물, 김칫국물, 마늘, 진간장, 고춧가루 넣기" to null,
            "생수 8컵 넣고 대파 넣어 30~40분 끓이기" to 1800,
            "생강물, 두부, 고추, 들기름 추가 후 마무리" to 180,
            "시식 및 김치찌개 맛 설명, 마무리 인사" to null
        )
        "wDGMkKjSVUg" -> listOf(
            "오늘의 메뉴 소개 및 요리 개요" to null,
            "재료 손질 및 양파/베이컨 볶기" to null,
            "면 삶기 및 크림소스 완성" to null,
            "플레이팅 및 시식" to null
        )

        "gpt001" -> listOf(
            "양파를 채썰고 토마토소스를 준비합니다." to null,
            "파스타면을 삶고 팬에 토마토소스를 끓입니다." to null,
            "소스를 팬에 넣고 재료를 섞습니다." to 180,
            "간을 보고 바질과 치즈로 마무리합니다." to null
        )
        else -> listOf("기본적인 요리 과정입니다." to null)
    }
}

// 요약만 따로 뽑기
fun getSummariesOnly(videoId: String): List<String> = getStepSummaries(videoId).map { it.first }

// 타이머만 따로 뽑기
fun getTimersOnly(videoId: String): List<Int?> = getStepSummaries(videoId).map { it.second }
