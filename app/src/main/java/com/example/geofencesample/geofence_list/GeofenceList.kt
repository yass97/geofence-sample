package com.example.geofencesample.geofence_list

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.geofencesample.MainViewModel

@Composable
fun GeofenceList(
    viewModel: MainViewModel = viewModel(),
    navController: NavController = rememberNavController()
) {
    viewModel.loadGeofences()
    val items by viewModel.entries.observeAsState(mutableListOf())
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Geofence List") },
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
        if (items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Empty")
            }
            return@Scaffold
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center
        ) {
            items(items) { item ->
                Row(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onLongPress = { viewModel.removeGeofence(item) }
                            )
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.padding(start = 10.dp),
                        imageVector = Icons.Outlined.Place,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                    Column(
                        modifier = Modifier.padding(start = 10.dp)
                    ) {
                        Text(text = "Latitude: ${item.latitude}")
                        Text(text = "Longitude: ${item.longitude}")
                        Text(text = "Radius: ${item.radius}")
                    }
                }
                Divider()
            }
        }
    }
}
