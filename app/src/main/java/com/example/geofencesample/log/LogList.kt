package com.example.geofencesample.log

import android.text.format.DateFormat
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.geofencesample.MainViewModel

@Composable
fun LogList(
    viewModel: MainViewModel = viewModel(),
    navController: NavController = rememberNavController()
) {
    viewModel.updateLogs()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Log") },
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
        val logs by viewModel.logs.observeAsState(mutableListOf())
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(logs) { log ->
                    Column(
                        modifier = Modifier.padding(all = 8.dp)
                    ) {
                        Text(text = log.first)
                        Spacer(modifier = Modifier.height(2.dp))
                        val df = DateFormat.format("yyyy/MM/dd hh:mm:ss", log.second)
                        Text(text = df.toString())
                    }
                    Divider()
                }
            }
        }
    }
}