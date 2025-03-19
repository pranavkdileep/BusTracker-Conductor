package com.pranavkd.bustracker_conductor

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pranavkd.bustracker_conductor.screens.BusInfo
import com.pranavkd.bustracker_conductor.screens.ChatScreen
import com.pranavkd.bustracker_conductor.screens.LoginScreen
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
            val context = LocalContext.current
            val sharedPreferences = context.getSharedPreferences("bus_tracker", MODE_PRIVATE)
            var condutorId : MutableState<String> = remember { mutableStateOf("") }
            var busId : MutableState<String> = remember { mutableStateOf("") }
            var startDes by remember { mutableStateOf("login") }
            if(Authentication(sharedPreferences, condutorId, busId)){
                startDes = "bus_info"
            }
            val apiHelper = ApiHelper()
            val chatModel = ViewModelProvider(this).get(ChatModel::class.java)
            BusTrackerConductorTheme {
                val navController = rememberNavController()
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                        .windowInsetsPadding(
                            WindowInsets.systemBars.only(WindowInsetsSides.Bottom)
                        ),
                    bottomBar = {
                        if(startDes != "login"){
                            BottomNav(navController)
                        }
                    },
                    topBar = {
                        if(startDes != "login"){
                            TopAppBar(condutorId.value,sharedPreferences,navController)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDes,
                        modifier = Modifier.padding(innerPadding)
                    ){
                        composable("bus_info"){
                            BusInfo(condutorId.value)
                        }
                        composable("chat"){
                            ChatScreen(chatModel, condutorId.value)
                        }
                        composable("login") {
                            LoginScreen(sharedPreferences, apiHelper,
                                loginSuccess = {
                                    if(Authentication(sharedPreferences, condutorId, busId)){
                                        startDes = "bus_info"
                                    }
                                }
                            )
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    condutorId: String,
    sharedPreferences: SharedPreferences,
    navController: NavHostController
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Column {
                Text(
                    "Conductor : $condutorId",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = {
                sharedPreferences.edit().remove("token").apply()
                navController.navigate("login")
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Localized description"
                )
            }
        },
        actions = {
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    imageVector = Icons.Filled.AccountBox,
                    contentDescription = "Localized description"
                )
            }
        },
    )
}