import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import org.koin.compose.KoinContext
import androidx.navigation.compose.composable
import screens.Favourite
import screens.Profile
import screens.Settings
import screens.home.Home

@Composable
fun App() {
    val screens = listOf("Home", "Favourite", "Profile", "Settings")
    var selectedScreen by remember { mutableStateOf(screens.firstOrNull()) }
    MaterialTheme {
        KoinContext {
            NavHost(
                navController = rememberNavController(),
                startDestination = "Home"
            ) {
                composable("Home") {
                    Home()
                }
                composable("Favourite") {
                    Favourite()
                }
                composable("Profile") {
                    Profile()
                }
                composable("Settings") {
                    Settings()
                }
            }
        }

        Scaffold(
            bottomBar = {
                BottomNavigation(backgroundColor = Color.White) {
                    screens.forEach { screen ->
                        BottomNavigationItem(
                            icon = { Icon(getIconForScreen(screen), contentDescription = screen) },
                            label = { Text(screen) },
                            selected = screen == selectedScreen,
                            onClick = { selectedScreen = screen },
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            content = {
                if (selectedScreen == "Home") Home() else if (selectedScreen == "Favourite") Favourite() else if (selectedScreen == "Profile") Profile() else Settings()
            }
        )
    }
}

@Composable
fun getIconForScreen(screen: String): ImageVector {
    return when (screen) {
        "Home" -> Icons.Default.Home
        "Favourite" -> Icons.Default.Favorite
        "Settings" -> Icons.Default.Settings
        "Profile" -> Icons.Default.Person
        else -> Icons.Default.Home
    }
}