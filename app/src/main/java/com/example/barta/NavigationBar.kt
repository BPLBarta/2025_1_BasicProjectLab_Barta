package com.example.barta

sealed class NavigationBar(val route: String, val label: String, val icon: Int, val selectedIcon: Int) {
    object Dashboard : NavigationBar("dashboard", "Dashboard", R.drawable.ic_dashboard, R.drawable.ic_dashboard_fill)
    object Home : NavigationBar("home", "Home", R.drawable.ic_home, R.drawable.ic_home_fill)
    object Profile : NavigationBar("profile", "Profile", R.drawable.ic_profile, R.drawable.ic_profile_fill)
}