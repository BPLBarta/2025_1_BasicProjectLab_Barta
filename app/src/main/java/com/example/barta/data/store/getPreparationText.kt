package com.example.barta.data

data class PreparationData(
    val title: String,
    val ingredients: String
)

fun getPreparationText(videoId: String): PreparationData {
    return when (videoId) {
        "_VqTaMZwj7I" -> PreparationData(
            title = "부대찌개",
            "고춧가루 60g \n" +
                    "후추 0.5g\n" +
                    "다진마늘 10g\n" +
                    "다진 생강 5g\n" +
                    "소금 1g\n" +
                    "미원 1g\n" +
                    "굴소스 5g\n" +
                    "사골육수 80g\n"+
                    "묵은지 50g\n" +
                    "숙성 양념장 50g\n" +
                    "양파 120g\n" +
                    "대파 300g\n" +
                    "소고기 민찌 120g\n" +
                    "다진 마늘 40g\n" +
                    "후랑크 소시지 70g\n" +
                    "칼바사 소시지 220g\n" +
                    "튤립햅 50g \n" +
                    "사골육수 900ml"
        )
        "rK3PdxlGTt8" -> PreparationData(
            title = "감바스",
            ingredients =
                "재료 (3~4인분)\n" +
                        "큰새우 12미\n" +
                        "대파 2단\n" +
                        "양송이버섯 200g\n" +
                        "마늘 120g\n" +
                        "바게트 1개\n\n" +

                        "초간단 토핑양념\n" +
                        "무염버터 100g\n" +
                        "타임 1.5tsp\n" +
                        "로즈마리 2tsp\n\n" +

                        "새우양념\n" +
                        "파프리카 파우더 0.5tsp\n" +
                        "미원 약간\n" +
                        "혼다시 1/4tsp\n" +
                        "레몬껍질 약간"
        )

        else -> PreparationData(title = "알 수 없는 요리","준비물이 등록되지 않았습니다.")
    }
}