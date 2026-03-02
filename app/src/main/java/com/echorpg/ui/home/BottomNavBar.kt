package com.echorpg.ui.home

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.echorpg.R

sealed class BottomNavItem(val route: String, val label: String, val iconRes: Int? = null) {
    data object Home : BottomNavItem("home", "Home")
    data object MyStories : BottomNavItem("my_stories", "My Stories")
    data object Characters : BottomNavItem("characters", "Girls")
    data object Profile : BottomNavItem("profile", "Me")
}

@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String
) {
    NavigationBar(
        containerColor = Color(0xFF1A0B38)
    ) {
        listOf(
            BottomNavItem.Home,
            BottomNavItem.MyStories,
            BottomNavItem.Characters,
            BottomNavItem.Profile
        ).forEach { item ->
            NavigationBarItem(
                icon = { Text(item.label.first().toString(), fontSize = 24.sp) }, // emoji placeholder for now
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFF4D94),
                    unselectedIconColor = Color(0xFF777799),
                    indicatorColor = Color(0xFF2A1A4A)
                )
            )
        }
    }
}