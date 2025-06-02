package com.example.barta

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.barta.ui.theme.LocalBartaPalette
import com.example.barta.ui.theme.suiteFontTypography
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.animation.core.animateDpAsState
import android.view.ViewTreeObserver
import androidx.compose.ui.platform.LocalContext
import com.example.barta.data.store.LinkStore
import com.example.barta.ui.player.PlayerActivity
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.barta.data.getPreparationText

fun extractVideoId(url: String): String {
    val regex = Regex("(?:v=|be/|embed/)([\\w-]{11})")
    return regex.find(url)?.groupValues?.get(1) ?: ""
}

@Composable
fun HomeScreen(navController: NavController, initialUrl: String = "", modifier: Modifier = Modifier) {
    val color = LocalBartaPalette.current
    val context = LocalContext.current
    var url by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val imeBottomPx = WindowInsets.ime.getBottom(LocalDensity.current)
    val imeBottomDp = with(LocalDensity.current) { imeBottomPx.toDp() }
    val isKeyboardVisible = imeBottomPx > 0
    val localView = LocalView.current

    // 키보드 닫힘 감지하여 포커스 제거 + 입력 초기화
    DisposableEffect(localView) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            val r = android.graphics.Rect()
            localView.getWindowVisibleDisplayFrame(r)
            val screenHeight = localView.rootView.height
            val keypadHeight = screenHeight - r.bottom
            if (keypadHeight < screenHeight * 0.15) {
                focusManager.clearFocus()
                url = ""
            }
        }
        localView.viewTreeObserver.addOnGlobalLayoutListener(listener)
        onDispose {
            localView.viewTreeObserver.removeOnGlobalLayoutListener(listener)
        }
    }

    LaunchedEffect(isKeyboardVisible) {
        if (!isKeyboardVisible) {
            focusManager.clearFocus()
            url = ""
        }
    }

    // 🔸 애니메이션 스페이서 추적 → 버튼 타이밍 제어
    val animatedSpacerHeight by animateDpAsState(
        targetValue = if (isFocused) 230.dp else 400.dp,
        label = "SpacerAnimation"
    )

    var allowShowButton by remember { mutableStateOf(false) }
    LaunchedEffect(animatedSpacerHeight) {
        allowShowButton = animatedSpacerHeight <= 350.dp
    }

    val showButton = isFocused && allowShowButton
    val buttonBottomPadding = if (showButton) imeBottomDp + 350.dp else 100.dp

    val contentModifier = if (isFocused) {
        Modifier.fillMaxSize()
    } else {
        Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
    ) {
        Column(
            modifier = contentModifier
                .padding(bottom = if (showButton) 100.dp else 0.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(600.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.hsbackground),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Spacer(modifier = Modifier.height(40.dp))
                    if (!isFocused) {
                        Text(
                            "BARTA",
                            style = suiteFontTypography.h3,
                            color = Color.White,
                            modifier = Modifier.padding(start = 24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(animatedSpacerHeight))

                    Text(
                        "레시피 불러오기",
                        style = suiteFontTypography.h4,
                        color = Color.White,
                        modifier = Modifier.padding(start = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val customTextSelectionColors = TextSelectionColors(
                        handleColor = color.primaryOrange1,
                        backgroundColor = color.primaryOrange1
                    )

                    CompositionLocalProvider(
                        LocalTextSelectionColors provides customTextSelectionColors
                    ) {
                        OutlinedTextField(
                            value = url,
                            onValueChange = { url = it },
                            placeholder = {
                                if (!isFocused && url.isEmpty()) {
                                    Text(
                                        "유튜브 URL을 입력해주세요.",
                                        color = color.textGray1,
                                        style = suiteFontTypography.body1
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .height(48.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color.White),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                            interactionSource = interactionSource,
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                backgroundColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                cursorColor = color.primaryOrange1,
                                textColor = color.textBlack
                            )
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 18.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                if (!isFocused) {
                    Text(
                        "최근 기록",
                        style = suiteFontTypography.h4,
                        color = color.textBlack,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                var hasLaunchedOnce by remember { mutableStateOf(false) }
                var hasRecipes by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    LinkStore.loadFromDataStore(context) // ✅ 추가
                    hasLaunchedOnce = true
                    hasRecipes = LinkStore.youtubeHistory.isNotEmpty()
                }

                if (hasLaunchedOnce && !hasRecipes) {
                    // 🔹 레시피가 없을 때만 처음에 보여줌
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(color.backgroundGray2),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "최근 기록이 없습니다",
                            style = suiteFontTypography.body1,
                            color = color.textBlack
                        )
                    }
                } else if (LinkStore.youtubeHistory.isNotEmpty()) {
                    val recentRecipe = LinkStore.youtubeHistory.first()
                    val videoId = extractVideoId(recentRecipe.url)


                    Box(
                        modifier = Modifier
                            .clickable {
                                navController.navigate("player/$videoId")
                            }
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(recentRecipe.thumbnailUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = recentRecipe.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.5f))
                        )

                        Icon(
                            painter = painterResource(id = R.drawable.ic_play),
                            contentDescription = "Play",
                            tint = Color.White,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp)
                                .size(24.dp)
                        )

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp),
                            horizontalAlignment = Alignment.End
                        ) {
                            val prepTitle = getPreparationText(videoId).title
                            Text(
                                text =prepTitle ,
                                style = suiteFontTypography.h4,
                                color = Color.White,
                                maxLines = 1
                            )
                            Text(
                                text = recentRecipe.savedAt.substringBefore(" "),
                                style = suiteFontTypography.body1,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        if (showButton) {
            Button(
                onClick = {
                    val videoId = extractVideoId(url)
                    if (videoId.isNotEmpty()) {
                        LinkStore.addUrl(url, context)  // ✅ 저장
                        focusManager.clearFocus()

                        // 🔽 새로 만든 PlayerActivity 실행 (가로 고정됨)
                        val intent = Intent(context, PlayerActivity::class.java).apply {
                            putExtra("videoId", videoId)
                        }
                        context.startActivity(intent)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 12.dp, end = 12.dp, bottom = buttonBottomPadding)
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = color.primaryOrange1,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text("불러오기", style = suiteFontTypography.button)
            }
        }
    }
}
