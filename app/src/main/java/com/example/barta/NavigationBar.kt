package com.example.barta

sealed class NavigationBar(val route: String, val label: String, val icon: Int) {
    object Dashboard : NavigationBar("dashboard", "Dashboard", R.drawable.ic_dashboard, )
    object Home : NavigationBar("home", "Home", R.drawable.ic_home)
    object Profile : NavigationBar("profile", "Profile", R.drawable.ic_profile)
}