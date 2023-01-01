package com.example.geofencesample.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.geofencesample.Destination
import com.example.geofencesample.MainViewModel

@Composable
fun Home(
    viewModel: MainViewModel = viewModel(),
    navController: NavController = rememberNavController(),
    onTestNotificationClicked: () -> Unit
) {
    val location by viewModel.currentLocation.observeAsState()
    val buttonModifier = Modifier.width(200.dp)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Home") }) }
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(100.dp),
                imageVector = Icons.Filled.LocationOn,
                contentDescription = null,
                tint = Color.Gray
            )
            Text(text = "Latitude: ${location?.latitude}")
            Text(text = "Longitude: ${location?.longitude}")
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                modifier = buttonModifier,
                onClick = {
                    navController.navigate(Destination.GEOFENCE_REGISTRATION.name) {
                        popUpTo("home")
                    }
                }
            ) {
                Text(text = "Geofence Registration")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                modifier = buttonModifier,
                onClick = {
                    navController.navigate(Destination.GEOFENCE_LIST.name) {
                        popUpTo("home")
                    }
                }
            ) {
                Text(text = "Geofence List")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                modifier = buttonModifier,
                onClick = onTestNotificationClicked
            ) {
                Text(text = "Notification Test")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                modifier = buttonModifier,
                onClick = {
                    navController.navigate(Destination.LOG_LIST.name) {
                        popUpTo("home")
                    }
                }
            ) {
                Text(text = "Log")
            }
        }
    }
}