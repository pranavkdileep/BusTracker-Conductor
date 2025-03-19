package com.pranavkd.bustracker_conductor.screens

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.pranavkd.bustracker_conductor.Businfo
import com.pranavkd.bustracker_conductor.Managers
import com.pranavkd.bustracker_conductor.Routes
import com.pranavkd.bustracker_conductor.passenger
import com.pranavkd.bustracker_conductor.ui.theme.BusTrackerConductorTheme



@Composable
fun BusInfo(conductorId: String) {
    BusTrackerConductorTheme {
        val managers = Managers()
        var loading by remember { mutableStateOf(false) }
        var busInfo by remember { mutableStateOf<Businfo?>(null) }
        var passengers by remember { mutableStateOf<List<passenger>?>(null) }
        if(conductorId != ""){
            if(busInfo == null && passengers == null){
                loading = true
                managers.loadDataHome(conductorId) { error, bus, pass ->
                    if(error != null){
                        loading = false
                    }
                    else{
                        busInfo = bus
                        passengers = pass
                        loading = false
                    }
                }
            }
        }
        if(loading) {
            // Show loading screen
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator()
            }
        }else{
            if(busInfo != null && passengers != null){
                BusManagementApp(conductorId,busInfo!!, passengers!!,managers,setLoading = {loading = it},
                    updateBusinfo = {
                        if(conductorId != ""){
                                loading = true
                                managers.loadDataHome(conductorId) { error, bus, pass ->
                                    if(error != null){
                                        loading = false
                                    }
                                    else{
                                        busInfo = bus
                                        passengers = pass
                                        loading = false
                                    }
                                }

                        }
                    }
                    )
            }
        }
    }
}

@Composable
fun BusManagementApp(conductorId: String,busInfo: Businfo, passengers: List<passenger>,managers: Managers,setLoading : (Boolean) -> Unit,updateBusinfo: () -> Unit) {
    val lightPurple = Color(0xFFB2A5FF)
    val veryLightPurple = Color(0xFFDAD2FF)
    val darkPurple = Color(0xFF493D9E)
    val yellow = Color(0xFFFFF2AF)
    val lightGray = Color(0xFFD9D9D9)



    var activeTab by remember { mutableStateOf("Routes") }

    var busInfo by remember { mutableStateOf(busInfo) }


    // Track completed routes count
    val completedRoutesCount = busInfo.routes.count { it.iscompleted }

    var passengers by remember { mutableStateOf(passengers) }
    var context = LocalContext.current

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
                busInfo = busInfo,
                onStateChange = { newState ->
                    busInfo = busInfo.copy(state = newState)
                },
                onSaveState = {
                    setLoading(true)
                    managers.updateState(conductorId,busInfo.state) { error ->
                        if(error != null){
                            //Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                        }
                        else{
                            Handler(Looper.getMainLooper()).post {
                                Toast.makeText(context, "State updated successfully", Toast.LENGTH_SHORT).show()
                            }                        }
                        setLoading(false)
                        updateBusinfo()
                    }
                },
                lightPurple = lightPurple,
                darkPurple = darkPurple,
                lightGray = lightGray
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
                    routes = busInfo.routes,
                    onRouteToggle = { routeName ->
                        // Update the route's completion status
                        val updatedRoutes = busInfo.routes.map { route ->
                            if (route.name == routeName) {
                                route.copy(iscompleted = !route.iscompleted)
                            } else {
                                route
                            }
                        }
                        busInfo = busInfo.copy(routes = updatedRoutes)
                    },
                    onSaveRoutes = {
                        setLoading(true)
                        managers.updateNoOfStopes(conductorId,busInfo.routes.count { it.iscompleted }) { error ->
                            if(error != null){
                                //Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                            }
                            else{
                                //Toast.makeText(context, "Routes updated successfully", Toast.LENGTH_SHORT).show()
                            }
                            setLoading(false)
                            updateBusinfo()
                        }
                    },
                    completedCount = completedRoutesCount,
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
    busInfo: Businfo,
    onStateChange: (String) -> Unit,
    onSaveState: () -> Unit,
    lightPurple: Color,
    darkPurple: Color,
    lightGray: Color
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
                text = "Bus ID : ${busInfo.busId}",
                color = Color.Black,
                fontSize = 14.sp
            )
            Text(
                text = "Bus Name : ${busInfo.busName}",
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
                    value = busInfo.state,
                    onValueChange = onStateChange,
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
                    onClick = onSaveState,
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
    routes: List<Routes>,
    onRouteToggle: (String) -> Unit,
    onSaveRoutes: () -> Unit,
    completedCount: Int,
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
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Completed: $completedCount/${routes.size}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = darkPurple
                )

                Button(
                    onClick = onSaveRoutes,
                    colors = ButtonDefaults.buttonColors(containerColor = darkPurple),
                    shape = CircleShape
                ) {
                    Text("Save Routes")
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(routes) { route ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(lightPurple)
                            .clickable { onRouteToggle(route.name) }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = route.name,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )

                        // Custom Radio Button
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (route.iscompleted) yellow else lightPurple)
                                .border(2.dp, yellow, CircleShape)
                                .clickable { onRouteToggle(route.name) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PassengersContent(
    passengers: List<passenger>,
    lightPurple: Color,
    darkPurple: Color
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedPassenger by remember { mutableStateOf<passenger?>(null) }

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
                        text = "${passenger.bookingId} ${passenger.fullname}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Button(
                        onClick = {
                            selectedPassenger = passenger
                            showDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = darkPurple),
                        shape = CircleShape
                    ) {
                        Text("View")
                    }
                }
            }
        }
    }

    // Show passenger details dialog if a passenger is selected
    if (showDialog && selectedPassenger != null) {
        PassengerDetailsDialog(
            passenger = selectedPassenger!!,
            onDismiss = { showDialog = false },
            lightPurple = lightPurple,
            darkPurple = darkPurple
        )
    }
}

@Composable
fun PassengerDetailsDialog(
    passenger: passenger,
    onDismiss: () -> Unit,
    lightPurple: Color,
    darkPurple: Color
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(lightPurple)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Passenger Details",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Passenger information
                PassengerInfoItem("Booking ID", passenger.bookingId)
                PassengerInfoItem("Full Name", passenger.fullname)
                PassengerInfoItem("Email", passenger.email)
                PassengerInfoItem("Phone", passenger.phone)
                PassengerInfoItem("Gender", passenger.gender)

                Spacer(modifier = Modifier.height(8.dp))

                // Journey details
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF0F0F0))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = "Journey Details",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "From",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = passenger.source,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Column {
                                Text(
                                    text = "To",
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Text(
                                    text = passenger.destination,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Close button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = darkPurple),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Close")
                }
            }
        }
    }
}

@Composable
fun PassengerInfoItem(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }

    HorizontalDivider(
        modifier = Modifier.padding(vertical = 8.dp),
        color = Color(0xFFEEEEEE)
    )
}