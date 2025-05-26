package com.example.barta

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.barta.ui.theme.LocalBartaPalette
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext

@Composable
fun ProfileScreen(navController: NavController, modifier: Modifier = Modifier) {
    val color = LocalBartaPalette.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color.backgroundWhite)
            .padding(horizontal = 30.dp)
    ) {
        Spacer(modifier = Modifier.height(60.dp)) // 마이페이지 위 빈공간

        // 상단 제목
        Text(
            text = "마이페이지",
            style = MaterialTheme.typography.h5,
            color = color.textBlack
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 프로필
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.profile), // 프로필 이미지
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(30.dp))
            Column {
                Text("최성재", style = MaterialTheme.typography.subtitle1, color = color.textBlack)
                Text("@chlthdwo0520", style = MaterialTheme.typography.body1, color = color.textBlack)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 프로필 편집 버튼
        Button(
            onClick = { /* 편집 동작 */ },
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(backgroundColor = color.primaryOrange1),
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
        ) {
            Text("프로필 편집하기", color = color.textWhite, style = MaterialTheme.typography.button)
        }

        Spacer(modifier = Modifier.height(24.dp))
        LongDivider() // 회색 실선 요리 설정 위

        Spacer(modifier = Modifier.height(12.dp))
        SettingCategory(title = "요리 설정")
        SettingItem(iconId = R.drawable.ic_script, title = "레시피 자막 설정")
        SettingItem(iconId = R.drawable.ic_repeat, title = "반복 재생 횟수 설정")
        SettingItem(iconId = R.drawable.ic_timer, title = "타이머 설정")

        Spacer(modifier = Modifier.height(12.dp))
        LongDivider()

        Spacer(modifier = Modifier.height(14.dp))
        SettingCategory(title = "시스템 설정")
        SettingItem(iconId = R.drawable.ic_headset, title = "고객센터")
        SettingItem(iconId = R.drawable.ic_bullhorn, title = "공지사항")
        SettingItem(iconId = R.drawable.ic_info, title = "서비스 이용 가이드")
        SettingItem(iconId = R.drawable.ic_flask, title = "실험실")

        Spacer(modifier = Modifier.height(15.dp))

        // 로그아웃, 계정탈퇴

        val context = LocalContext.current

        Text(
            text = "로그아웃",
            style = MaterialTheme.typography.subtitle2,
            color = color.highlightRed,
            modifier = Modifier
                .clickable {
                    val sharedPref = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
                    sharedPref.edit().putBoolean("isLoggedIn", false).apply()

                    // MainActivity 재시작
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                    (context as? Activity)?.finish()  // 현재 화면 종료
                }
                .padding(vertical = 8.dp)
        )


        Text(
            text = "계정탈퇴",
            style=MaterialTheme.typography.subtitle2,
            color = color.highlightRed,
            modifier = Modifier
                .clickable { /* 계정탈퇴 동작 */ }
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
fun SettingCategory(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.h6,
        color = LocalBartaPalette.current.textGray2,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun SettingItem(iconId: Int, title: String) {
    val palette = LocalBartaPalette.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { /* 클릭 동작 */ }
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = null,
            tint = palette.textBlack,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(14.dp)) // 아이콘 글자 사이
        Text( // 아이콘 옆 글자들
            text = title,
            style = MaterialTheme.typography.subtitle2,
            color = palette.textBlack,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun LongDivider(
    color: Color = LocalBartaPalette.current.dividerGray,
    thickness: Dp = 1.dp
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val dividerWidth = screenWidth - 36.dp // ✅ 양옆 18dp 여백을 위한 길이

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Divider(
            color = color,
            thickness = thickness,
            modifier = Modifier.width(dividerWidth)
        )
    }
}