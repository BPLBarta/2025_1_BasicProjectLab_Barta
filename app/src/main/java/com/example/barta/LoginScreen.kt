package com.example.barta

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.barta.ui.theme.LocalBartaPalette
import androidx.navigation.NavController

@Composable
fun CustomCheckIconBox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val imageRes = if (checked) R.drawable.ic_check_fill else R.drawable.ic_check

    Image(
        painter = painterResource(id = imageRes),
        contentDescription = "check",
        modifier = Modifier
            .size(16.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onCheckedChange(!checked)
            }
    )
}

@Composable
fun LoginScreen(navController: NavController, modifier: Modifier = Modifier) {
    val color = LocalBartaPalette.current
    val focusManager = LocalFocusManager.current

    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var keepLogin by remember { mutableStateOf(false) }

    val idInteraction = remember { MutableInteractionSource() }
    val idFocused by idInteraction.collectIsFocusedAsState()

    val pwInteraction = remember { MutableInteractionSource() }
    val pwFocused by pwInteraction.collectIsFocusedAsState()

    // ✅ 조건별 색상 설정
    val idUnderlineColor = if (id.isNotEmpty() || idFocused) color.primaryOrange1 else color.textGray1
    val pwUnderlineColor = if (password.isNotEmpty() || pwFocused) color.primaryOrange1 else color.textGray1

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color.backgroundWhite)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
            .padding(horizontal = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(180.dp))

        Text(
            text = "BARTA",
            style = MaterialTheme.typography.h2.copy(color = color.primaryOrange1),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(48.dp))

        // 아이디 입력창
        Box(modifier = Modifier.fillMaxWidth()) {
            val customTextSelectionColors = TextSelectionColors(
                handleColor = color.primaryOrange1,
                backgroundColor = color.primaryOrange1
            )
            CompositionLocalProvider(
                LocalTextSelectionColors provides customTextSelectionColors
            ) {
                TextField(
                    value = id,
                    onValueChange = { id = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    interactionSource = idInteraction,
                    textStyle = MaterialTheme.typography.body1.copy(color = color.primaryOrange1),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = color.backgroundWhite,
                        focusedIndicatorColor = idUnderlineColor,
                        unfocusedIndicatorColor = idUnderlineColor,
                        disabledIndicatorColor = idUnderlineColor,
                        textColor = color.primaryOrange1,
                        cursorColor = color.primaryOrange1
                    )
                )
            }

            if (!idFocused && id.isEmpty()) {
                Text(
                    text = "아이디",
                    style = MaterialTheme.typography.body1,
                    color = color.textGray1,
                    modifier = Modifier.padding(start = 7.dp, top = 20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 비밀번호 입력창
        Box(modifier = Modifier.fillMaxWidth()) {
            val customTextSelectionColors = TextSelectionColors(
                handleColor = color.primaryOrange1,
                backgroundColor = color.primaryOrange1
            )
            CompositionLocalProvider(
                LocalTextSelectionColors provides customTextSelectionColors
            ) {
                TextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    interactionSource = pwInteraction,
                    visualTransformation = PasswordVisualTransformation(),
                    trailingIcon = {
                        Box(modifier = Modifier.size(24.dp))
                    },
                    textStyle = MaterialTheme.typography.body1.copy(color = color.primaryOrange1),
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = color.backgroundWhite,
                        focusedIndicatorColor = pwUnderlineColor,
                        unfocusedIndicatorColor = pwUnderlineColor,
                        disabledIndicatorColor = pwUnderlineColor,
                        textColor = color.primaryOrange1,
                        cursorColor = color.primaryOrange1
                    )
                )
            }

            if (!pwFocused && password.isEmpty()) {
                Text(
                    text = "비밀번호",
                    style = MaterialTheme.typography.body1,
                    color = color.textGray1,
                    modifier = Modifier.padding(start = 7.dp, top = 20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            CustomCheckIconBox(
                checked = keepLogin,
                onCheckedChange = { keepLogin = it }
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "로그인 상태 유지",
                color = color.textGray1,
                style = MaterialTheme.typography.body2
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        val context = LocalContext.current
        val sharedPref = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)

        Button(
            onClick = {
                if (keepLogin) {
                    // ✅ "로그인 상태 유지"가 체크된 경우 — true 저장
                    sharedPref.edit()
                        .putBoolean("isLoggedIn", true)
                        .apply()
                } else {
                    // ✅ 체크 안 된 경우 — 플래그를 지우거나 false 로 설정
                    sharedPref.edit()
                        .putBoolean("isLoggedIn", false)   // 또는 .remove("isLoggedIn")
                        .apply()
                }

                // 공통 — 메인 화면으로 이동 & 로그인 화면 제거
                navController.navigate("bartaAppMain") {
                    popUpTo("login") { inclusive = true }
                }
            },
            enabled = id.isNotBlank() && password.length >= 8,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(30.dp)),
            colors = ButtonDefaults.buttonColors(backgroundColor = color.primaryOrange1)
        ) {
            Text("로그인", color = color.textWhite)
        }


        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "아이디 찾기 | 비밀번호 찾기 | 회원가입",
            color = color.textGray1,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}