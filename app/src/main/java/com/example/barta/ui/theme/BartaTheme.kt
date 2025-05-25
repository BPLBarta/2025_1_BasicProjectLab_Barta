package com.example.barta.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.barta.R

data class BartaPalette(
    val primaryOrange: Color,
    val textWhite: Color,
    val textBlack: Color,
    val textGray1: Color,
    val textGray2: Color,
    val backgroundGray1: Color,
    val backgroundGray2: Color,
    val backgroundWhite: Color,
    val recipeBoxGray: Color,
    val backgroundOverlayOrange: Color,
    val focusOverlayBlack50: Color,
    val focusOverlayBlack60: Color,
    val highlightRed: Color,
    val dividerGray: Color,
    val progress: Color,
    val timer1: Color,
    val timer2: Color,
    val timerProgress: Color
)

val LocalBartaPalette = staticCompositionLocalOf {
    BartaPalette(
        primaryOrange = Color(0xFFFBAA73),
        textWhite = Color(0xFFFFFFFF),
        textBlack = Color(0xFF000000),
        textGray1 = Color(0xFFBFBFBF),
        textGray2 = Color(0xFF5C5C5C),
        backgroundGray1 = Color(0xFFFBFBFB),
        backgroundGray2 = Color(0xFFF2F2F2),
        backgroundWhite = Color(0xFFFFFFFF),
        recipeBoxGray = Color(0xFF7A7A7A),
        backgroundOverlayOrange = Color(0xE6FCECD7),
        focusOverlayBlack50 = Color(0x7F000000),
        focusOverlayBlack60 = Color(0x99000000),
        highlightRed = Color(0xFFC70000),
        dividerGray = Color(0xFFE8E8E8),
        progress = Color(0xFF00FF88),
        timer1 = Color(0xFFD9D9D9),
        timer2 = Color(0xFFA2A2A2),
        timerProgress = Color(0xFFFF686B)
    )
}

val suiteFont = FontFamily(
    Font(R.font.suite_light, FontWeight.Light),
    Font(R.font.suite_regular, FontWeight.Normal),
    Font(R.font.suite_medium, FontWeight.Medium),
    Font(R.font.suite_semibold, FontWeight.SemiBold),
    Font(R.font.suite_bold, FontWeight.Bold),
    Font(R.font.suite_extrabold, FontWeight.ExtraBold),
    Font(R.font.suite_heavy, FontWeight.Black)
)

val suiteFontTypography = Typography(
    h1 = TextStyle(
        fontFamily = suiteFont,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 20.sp
    ),
    h2 = TextStyle(
        fontFamily = suiteFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp
    ),
    h3 = TextStyle(
        fontFamily = suiteFont,
        fontWeight = FontWeight.Black,
        fontSize = 36.sp
    ),
    h4 = TextStyle(
        fontFamily = suiteFont,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    h5 = TextStyle(
        fontFamily = suiteFont,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    h6 = TextStyle(
        fontFamily = suiteFont,
        fontWeight = FontWeight.Bold,
        fontSize = 12.sp
    ),
    subtitle1 = TextStyle(
        fontFamily = suiteFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    subtitle2 = TextStyle(
        fontFamily = suiteFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    body1 = TextStyle(
        fontFamily = suiteFont,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp
    ),
    body2 = TextStyle(
        fontFamily = suiteFont,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    button = TextStyle(
        fontFamily = suiteFont,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    caption = TextStyle(
        fontFamily = suiteFont,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp
    )
)

@Composable
fun BartaTheme(content: @Composable () -> Unit) {
    val bartaPalette = BartaPalette(
        primaryOrange = Color(0xFFFBAA73),
        textWhite = Color(0xFFFFFFFF),
        textBlack = Color(0xFF000000),
        textGray1 = Color(0xFFBFBFBF),
        textGray2 = Color(0xFF5C5C5C),
        backgroundGray1 = Color(0xFFFBFBFB),
        backgroundGray2 = Color(0xFFF2F2F2),
        backgroundWhite = Color(0xFFFFFFFF),
        recipeBoxGray = Color(0xFF7A7A7A),
        backgroundOverlayOrange = Color(0xE6FCECD7),
        focusOverlayBlack50 = Color(0x7F000000),
        focusOverlayBlack60 = Color(0x99000000),
        highlightRed = Color(0xFFC70000),
        dividerGray = Color(0xFFE8E8E8),
        progress = Color(0xFF00FF88),
        timer1 = Color(0xFFD9D9D9),
        timer2 = Color(0xFFA2A2A2),
        timerProgress = Color(0xFFFF686B)
    )

    CompositionLocalProvider(LocalBartaPalette provides bartaPalette) {
        MaterialTheme(
            typography = suiteFontTypography,
            shapes = MaterialTheme.shapes,
            content = content
        )
    }
}