package com.example.barta.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.barta.data.getPreparationText
import com.example.barta.ui.component.BartaIcon
import com.example.barta.ui.theme.suiteFontTypography

@Composable
fun PreparationScreen(videoId: String, onMicClick: () -> Unit) {
    val preparationData = getPreparationText(videoId)
    val prepTitle = preparationData.title
    val prepText = preparationData.ingredients

    val thumbnailUrl = "https://img.youtube.com/vi/$videoId/0.jpg"

    val lines = prepText.lines().filter { it.isNotBlank() }
    val mid = lines.size / 2
    val leftText = lines.subList(0, mid).joinToString("\n")
    val rightText = lines.subList(mid, lines.size).joinToString("\n")

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(12.dp)
                .zIndex(2f)
        ) {
            BartaIcon(
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onMicClick() }
            )
        }

        AsyncImage(
            model = thumbnailUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(481.dp)
                    .heightIn(min = 300.dp)
                    .background(
                        color = Color(0xFFFCECD7).copy(alpha = 0.95f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(7.dp))

                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(width = 155.dp, height = 26.dp)
                                .graphicsLayer {
                                    rotationZ = -3f
                                    translationX = 3f
                                }
                                .background(Color(0xFFE5A77E))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        )

                        Text(
                            text = prepTitle,
                            style = suiteFontTypography.h3,
                            color = Color.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = leftText,
                            style = suiteFontTypography.subtitle1,
                            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.width(78.dp))

                        Text(
                            text = rightText,
                            style = suiteFontTypography.subtitle1,
                            modifier = Modifier.wrapContentWidth(Alignment.CenterHorizontally),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}
