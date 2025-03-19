package com.pranavkd.bustracker_conductor.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pranavkd.bustracker_conductor.ui.theme.BusTrackerConductorTheme

@Composable
fun BusInfo(busId:String) {
    BusTrackerConductorTheme {
        BusManagementApp()
    }
}

@Composable
fun BusManagementApp() {
    val lightPurple = Color(0xFFB2A5FF)
    val veryLightPurple = Color(0xFFDAD2FF)
    val darkPurple = Color(0xFF493D9E)
    val yellow = Color(0xFFFFF2AF)
    val lightGray = Color(0xFFD9D9D9)

    var activeTab by remember { mutableStateOf("Routes") }
    var selectedRoute by remember { mutableStateOf("") }
    var stateText by remember { mutableStateOf("") }

    val routes = listOf("Thrissur", "Ollur", "Puthukad", "Chalakudi", "Angamali", "Perumbavour")
    val passengers = List(7) { "BK-6755 Pranav" }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(veryLightPurple)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Bus Info Card
            BusInfoCard(
                lightPurple = lightPurple,
                darkPurple = darkPurple,
                lightGray = lightGray,
                stateText = stateText,
                onStateTextChange = { stateText = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Navigation
            TabNavigation(
                activeTab = activeTab,
                onTabSelected = { activeTab = it },
                lightPurple = lightPurple,
                veryLightPurple = veryLightPurple
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Content based on active tab
            when (activeTab) {
                "Routes" -> RoutesContent(
                    routes = routes,
                    selectedRoute = selectedRoute,
                    onRouteSelected = { selectedRoute = it },
                    lightPurple = lightPurple,
                    darkPurple = darkPurple,
                    yellow = yellow
                )
                "Passengers" -> PassengersContent(
                    passengers = passengers,
                    lightPurple = lightPurple,
                    darkPurple = darkPurple
                )
            }
        }
    }
}

@Composable
fun BusInfoCard(
    lightPurple: Color,
    darkPurple: Color,
    lightGray: Color,
    stateText: String,
    onStateTextChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(lightPurple)
            .padding(20.dp)
    ) {
        Column {
            Text(
                text = "Bus ID : MC-2097",
                color = Color.Black,
                fontSize = 14.sp
            )
            Text(
                text = "Bus Name : TDC Thodupuzha",
                color = Color.Black,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "State",
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 16.dp)
                )

                TextField(
                    value = stateText,
                    onValueChange = onStateTextChange,
                    placeholder = { Text("Enter State") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = lightGray,
                        focusedContainerColor = lightGray,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = darkPurple),
                    shape = CircleShape,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
fun TabNavigation(
    activeTab: String,
    onTabSelected: (String) -> Unit,
    lightPurple: Color,
    veryLightPurple: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        val tabs = listOf("Routes", "Passengers")

        tabs.forEach { tab ->
            val isSelected = activeTab == tab

            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = if (tab == "Routes") 8.dp else 0.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) lightPurple else veryLightPurple)
                    .border(
                        width = if (isSelected) 0.dp else 1.dp,
                        color = if (isSelected) Color.Transparent else lightPurple,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tab,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun RoutesContent(
    routes: List<String>,
    selectedRoute: String,
    onRouteSelected: (String) -> Unit,
    lightPurple: Color,
    darkPurple: Color,
    yellow: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Transparent)
            .padding(16.dp)
    ) {
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(containerColor = darkPurple),
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(bottom = 16.dp)
        ) {
            Text("Save")
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(routes) { route ->
                val isSelected = selectedRoute == route

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(lightPurple)
                        .clickable { onRouteSelected(route) }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = route,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )

                    // Custom Radio Button
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) yellow else lightPurple)
                            .border(2.dp, yellow, CircleShape),

                    )
                }
            }
        }
    }
}

@Composable
fun PassengersContent(
    passengers: List<String>,
    lightPurple: Color,
    darkPurple: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Transparent)
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(passengers) { passenger ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(lightPurple)
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = passenger,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = darkPurple),
                        shape = CircleShape
                    ) {
                        Text("View")
                    }
                }
            }
        }
    }
}