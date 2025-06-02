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
        "Havl-7iMCgQ" -> PreparationData(
            title = "김치찌개",
            ingredients =
                "준비재료\n" +
                        "잘 익은 김치 1.2kg\n" +
                        "돼지고기 꼬들살 400g\n" +
                        "두부 150g\n" +
                        "대파 1대\n" +
                        "양파 1개\n" +
                        "청양고추 3개\n" +
                        "붉은 고추 1개\n" +
                        "들기름 1큰술\n" +
                        "다진 생강 1작은술\n" +
                        "생수 2큰술\n\n" +

                        "고기양념\n" +
                        "국간장 1큰술\n" +
                        "맛술 3큰술\n" +
                        "매실청 1큰술\n\n" +

                        "찌개양념\n" +
                        "김치국물 1컵\n" +
                        "고춧가루 2큰술\n" +
                        "진간장 1큰술\n" +
                        "다진 마늘 1큰술\n" +
                        "들기름 1큰술\n" +
                        "진한 쌀뜨물 4컵\n" +
                        "생수 8컵\n\n" +

                        "*1컵=200cc"
        )
        "wDGMkKjSVUg" -> PreparationData(
            title = "까르보나라",
            ingredients =
                        "베이컨 5~6장\n" +
                        "스파게티 면\n" +
                        "양파 1/2개\n" +
                        "이탈리안 파슬리 30g\n" +
                        "노른자 1개\n" +
                        "파르마지아노 레지아노 치즈 80g\n" +
                        "생크림 250ml\n" +
                        "치킨스톡 1개\n" +
                        "화이트와인 약간\n" +
                        "올리브오일 약간\n\n" +
                        "* 생 파슬리가 없다면 건 파슬리 가루 사용 가능"
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