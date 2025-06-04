package com.example.barta.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import com.example.barta.R

@Composable
fun BartaIcon(
    modifier: Modifier = Modifier,
    contentDescription: String? = "Barta Icon"
) {
    Image(
        painter = painterResource(id = R.drawable.barta_logo),
        contentDescription = contentDescription,
        modifier = modifier
    )
}
