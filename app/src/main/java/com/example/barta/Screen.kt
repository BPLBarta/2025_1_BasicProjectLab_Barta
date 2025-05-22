package com.example.barta

sealed class Screen(val route: String, val label: String, val icon: Int) {
    object Home : Screen("home", "홈", R.drawable.ic_home)
    object Dashboard : Screen("dashboard", "대시보드", R.drawable.ic_dashboard)
    object Notifications : Screen("notifications", "알림", R.drawable.ic_notifications)
}
