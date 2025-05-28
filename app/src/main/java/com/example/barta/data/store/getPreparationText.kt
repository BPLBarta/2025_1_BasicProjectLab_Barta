
package com.example.barta.data

fun getPreparationText(videoId: String): String {
    return when (videoId) {
        "_VqTaMZwj7I" ->
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
        "def456" -> "🥩 준비물: 소고기, 마늘, 간장\n🍽 조리 도구: 칼, 도마"
        "gpt001" -> "🍝 준비물: 파스타면, 토마토소스, 양파\n🔪 조리 도구: 냄비, 팬, 칼"
        else -> "준비물이 등록되지 않았습니다."
    }
}
