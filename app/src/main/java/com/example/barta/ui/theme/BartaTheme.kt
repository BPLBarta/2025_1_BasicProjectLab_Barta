package com.example.barta.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.barta.R

private val DarkColorPalette = darkColors(
    primary = Color(0xFFBB86FC),
    primaryVariant = Color(0xFF3700B3),
    secondary = Color(0xFF03DAC6)
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
    MaterialTheme(
        colors = DarkColorPalette,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        content = content
    )
}
