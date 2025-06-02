package com.example.barta.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.barta.ui.theme.LocalBartaPalette

@Composable
fun RecipeSubtitle(
    stepNumber: Int,
    description: String,
    modifier: Modifier = Modifier
) {
    val color = LocalBartaPalette.current
    val stepBackgroundColor = color.primaryOrange2  // STEP 부분 배경색
    val descriptionBackgroundColor = Color.White    // 설명 부분 배경색

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(25.dp),
                clip = false
            )
            .clip(RoundedCornerShape(25.dp))
            .height(IntrinsicSize.Min) // 🔸 이 줄이 핵심입니다!
    ) {
        // STEP 부분
        Box(
            modifier = Modifier
                .background(stepBackgroundColor)
                .weight(0.17f)
                .fillMaxHeight(), // 🔸 높이 맞추기
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "STEP ${"%02d".format(stepNumber)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp)
            )
        }

        // 설명 부분
        Box(
            modifier = Modifier
                .background(descriptionBackgroundColor)
                .weight(0.83f)
                .fillMaxHeight(), // 🔸 높이 맞추기
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = description,
                fontSize = 12.sp,
                color = color.textBlack,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp)
            )
        }
    }
}
