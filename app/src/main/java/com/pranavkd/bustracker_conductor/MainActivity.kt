package com.pranavkd.bustracker_conductor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pranavkd.bustracker_conductor.screens.BusInfo
import com.pranavkd.bustracker_conductor.screens.ChatScreen
import com.pranavkd.bustracker_conductor.ui.theme.BusTrackerConductorTheme
sealed class BottomNavScreens(val titie:String, val route:String, val icon: ImageVector){
    object BusInfo: BottomNavScreens("Bus Info", "bus_info", Icons.Default.Info)
    object Chat: BottomNavScreens("Chat", "chat", Icons.Default.MailOutline)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BusTrackerConductorTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                        .windowInsetsPadding(
                            WindowInsets.systemBars.only(WindowInsetsSides.Bottom)
                        ),
                    bottomBar = {
                        BottomNav(navController = navController)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "bus_info",
                        modifier = Modifier.padding(innerPadding)
                    ){
                        composable("bus_info"){
                            BusInfo()
                        }
                        composable("chat"){
                            ChatScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNav(navController: NavController) {
    val items = listOf(
        BottomNavScreens.BusInfo,
        BottomNavScreens.Chat
    )
    BottomNavigation (
        backgroundColor = colorResource(id = R.color.Light),
    ) {
        val backStackEntry = navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry.value?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(item.icon, contentDescription = null, modifier = Modifier.padding(8.dp)) },
                label = { Text(item.titie) },
                selected = currentRoute == item.route,
                selectedContentColor = colorResource(id = R.color.Mid),
                unselectedContentColor = colorResource(id = R.color.Mid).copy(alpha = 0.5f),
                onClick = {
                    if(currentRoute != item.route){
                        navController.navigate(item.route)
                    }
                }
            )
        }
    }
}