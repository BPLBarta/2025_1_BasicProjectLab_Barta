package com.example.barta

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.barta.data.Store.LinkStore
import com.example.barta.ui.theme.suiteFontTypography
import com.example.barta.util.extractVideoId
import androidx.compose.ui.draw.clip

@Composable
fun DashboardScreen(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        LinkStore.loadFromDataStore(context)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("저장된 링크", style = suiteFontTypography.h4)
        Spacer(Modifier.height(16.dp))

        LinkStore.youtubeHistory.forEach { saved ->
            val videoId = extractVideoId(saved.url)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("player/$videoId") }
                    .padding(8.dp)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(saved.thumbnailUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = saved.title,
                    modifier = Modifier
                        .size(96.dp)
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(saved.title, style = suiteFontTypography.h6)
                    Text(saved.savedAt, style = suiteFontTypography.body2)
                }
            }
            Divider()
        }
    }
}
