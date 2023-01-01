package com.example.geofencesample.geofence_registration

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun GeofenceRegistration(
    viewModel: GeofenceRegistrationViewModel = viewModel(),
    navController: NavController = rememberNavController()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Geofence Registration") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(all = 10.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var latitude by remember { mutableStateOf("") }
            var longitude by remember { mutableStateOf("") }
            var radius by remember { mutableStateOf("") }

            var isLatitudeError by remember { mutableStateOf(false) }
            var isLongitudeError by remember { mutableStateOf(false) }
            var isRadiusError by remember { mutableStateOf(false) }
            OutlinedTextField(
                value = latitude,
                onValueChange = {
                    isLatitudeError = false
                    latitude = it
                },
                label = { Text("Latitude") },
                isError = isLatitudeError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = longitude,
                onValueChange = {
                    isLongitudeError = false
                    longitude = it
                },
                label = { Text("Longitude") },
                isError = isLongitudeError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = radius,
                onValueChange = {
                    isRadiusError = false
                    radius = it
                },
                label = { Text("Radius") },
                isError = isRadiusError,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                modifier = Modifier.width(200.dp),
                onClick = {
                    if (latitude.isBlank()) {
                        isLatitudeError = true
                        return@Button
                    }
                    if (longitude.isBlank()) {
                        isLongitudeError = true
                        return@Button
                    }
                    if (radius.isBlank()) {
                        isRadiusError = true
                        return@Button
                    }
                    viewModel.storeGeofence(latitude, longitude, radius)
                    navController.popBackStack()
                }) {
                Text(text = "Register")
            }
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                modifier = Modifier.width(200.dp),
                onClick = {
                    viewModel.getCurrentLocation { location ->
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                    }
                }) {
                Text(text = "Current Location")
            }
        }
    }
}