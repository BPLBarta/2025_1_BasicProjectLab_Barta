package com.example.barta.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.barta.ui.theme.LocalBartaPalette

@Composable
fun BartaProgressBar(
    progress: Float = 0.5f,  // 기본 진행률, 0.0 ~ 1.0
    modifier: Modifier = Modifier
) {
    val color = LocalBartaPalette.current

    LinearProgressIndicator(
        progress = progress,
        color = color.progress,
        modifier = modifier
            .fillMaxWidth()
            .height(3.dp)
    )
}
