package com.example.barta

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.focus.onFocusChanged
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.barta.data.Store.LinkStore
import com.example.barta.ui.theme.LocalBartaPalette
import com.example.barta.ui.theme.suiteFontTypography
import com.example.barta.util.extractVideoId
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image




@Composable
fun DashboardScreen(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        LinkStore.loadFromDataStore(context)
        isLoading = false
    }

    val allRecipes = LinkStore.youtubeHistory
    val filteredRecipes = if (!isLoading) {
        allRecipes.filter {
            it.title.contains(searchQuery, ignoreCase = true)
        }
    } else emptyList()


    val color = LocalBartaPalette.current
    val focusManager = LocalFocusManager.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus() // ✅ 배경 터치 시 포커스 해제
                })
            }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .background(Color.White),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // ✅ 상단 제목
            item {
                Text(
                    "나의 레시피",
                    style = suiteFontTypography.h5,
                    modifier = Modifier.padding(start = 24.dp, top = 40.dp, bottom = 18.dp)
                )
            }

            // ✅ 검색창
            if (!isLoading && allRecipes.isNotEmpty()) {
                item {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .onFocusChanged {
                                isFocused = it.isFocused
                                if (!it.isFocused) {
                                    searchQuery = "" // ✅ 이 줄 추가!
                                }
                            }
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 18.dp)
                            .clip(RoundedCornerShape(50.dp)),
                        placeholder = {
                            if (!isFocused && searchQuery.isEmpty()) {
                                Text("검색", style = suiteFontTypography.subtitle2, color = color.textGray2)
                            }
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        singleLine = true,
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = color.backgroundGray2,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            cursorColor = color.primaryOrange1
                        ),
                    )
                }

                item { Spacer(Modifier.height(16.dp)) }
            }

            // ✅ 로딩 중
            if (isLoading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            // ✅ 검색 결과 없음
            else if (filteredRecipes.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("저장된 레시피가 없습니다", style = suiteFontTypography.body1)
                    }
                }
            }

            // ✅ 결과 카드들
            else {
                items(filteredRecipes) { saved ->
                    val videoId = extractVideoId(saved.url)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { navController.navigate("player/$videoId") },
                        elevation = 4.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(16f / 9f)
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(saved.thumbnailUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = saved.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.3f))
                            )

                            Image(
                                painter = painterResource(id = R.drawable.ic_play),
                                contentDescription = "Play",
                                modifier = Modifier
                                    .padding(12.dp)
                                    .size(24.dp)
                                    .align(Alignment.BottomStart)
                            )

                            val dateOnly = saved.savedAt.substringBefore(" ")

                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
                                horizontalAlignment = Alignment.End
                            ) {
                                Text(
                                    text = saved.title,
                                    style = suiteFontTypography.h4,
                                    color = Color.White,
                                    maxLines = 1
                                )
                                Text(
                                    text = dateOnly,
                                    style = suiteFontTypography.subtitle2,
                                    color = Color.White,
                                    overflow = TextOverflow.Ellipsis,
                                    softWrap = false
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}