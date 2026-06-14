package ru.sibfu.openkras.navigation


import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavItem<T : Any>(
    val route: T,      // Теперь здесь объект маршрута
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem<MainScreenGraph.AllExcursionScreen>(
        route = MainScreenGraph.AllExcursionScreen,
        title = "Экскурсии",
        icon = Icons.Outlined.LocationOn
    )
    object Favorite : BottomNavItem<MainScreenGraph.FavoriteScreen>(
        route = MainScreenGraph.FavoriteScreen,
        title = "Избранное",
        icon = Icons.Default.FavoriteBorder
    )
    object Profile : BottomNavItem<ProfileScreenGraph.ProfileScreen>(
        route = ProfileScreenGraph.ProfileScreen,
        title = "Профиль",
        icon = Icons.Outlined.Person
    )
}

val bottomNavItems: List<BottomNavItem<*>> = listOf(
    BottomNavItem.Home,
    BottomNavItem.Favorite,
    BottomNavItem.Profile,
)

@SuppressLint("RestrictedApi")
@Composable
fun MyBottomNavigation(
    navController: NavController
){
    NavigationBar (
        containerColor = MaterialTheme.colorScheme.background
    ){
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        bottomNavItems.forEach { item ->
            val isSelected = currentDestination?.hasRoute(item.route::class) == true

            NavigationBarItem(
                icon = {Icon(item.icon, contentDescription = item.title)},
                label = { Text(item.title) },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route){
                        popUpTo(navController.graph.findStartDestination().id){
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                )
            )
        }
    }

}