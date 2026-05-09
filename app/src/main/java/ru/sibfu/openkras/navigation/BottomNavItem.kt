package ru.sibfu.openkras.navigation


import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavItem (
    val route: String,
    val title: String,
    val icon: ImageVector
){
    object Home : BottomNavItem(route = "home", title =  "Экскурсии", Icons.Default.LocationOn)
    object Favorite : BottomNavItem(route ="calendar", title = "Избранное", Icons.Default.FavoriteBorder)
    object Profile : BottomNavItem(route ="profile", title = "Профиль", Icons.Default.Person)
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.Favorite,
    BottomNavItem.Profile,
)

@SuppressLint("RestrictedApi")
@Composable
fun MyBottomNavigation(
    navController: NavController
){
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = {Icon(item.icon, contentDescription = item.title)},
                label = { Text(item.title) },
                selected = currentRoute?.hasRoute(item.route::class) == true,
                onClick = {
                    navController.navigate(MainScreenGraph.AllExcursionScreen){
                        popUpTo(navController.graph.findStartDestination().id){
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                )
            )
        }
    }

}