package com.example.geofencesample

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.SideEffect
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.geofencesample.geofence_list.GeofenceList
import com.example.geofencesample.geofence_registration.GeofenceRegistration
import com.example.geofencesample.home.Home
import com.example.geofencesample.log.LogList
import com.example.geofencesample.ui.theme.GeoFenceSampleTheme
import com.example.geofencesample.ui.theme.StatusbarColor
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.Date

@RequiresApi(Build.VERSION_CODES.S)
class MainActivity : ComponentActivity() {

    private lateinit var geofencingClient: GeofencingClient

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(this, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private val receiver: GeofenceBroadcastReceiver by lazy {
        GeofenceBroadcastReceiver()
    }

    private val requestLocationPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val hasFineLocationGranted =
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val hasBackgroundGranted =
            permissions[Manifest.permission.ACCESS_BACKGROUND_LOCATION] == true
        if (hasFineLocationGranted || hasBackgroundGranted) {
            addLog("hasFineLocationGranted: $hasFineLocationGranted")
            addLog("hasBackgroundGranted: $hasBackgroundGranted")
            setupGeofenceClient()
            setupCurrentLocation(viewModel.hasPermissionsGranted())
        } else {
            Toast.makeText(
                this,
                "Please allow location permissions",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupBroadcastReceiver()

        setupCurrentLocation(viewModel.hasPermissionsGranted())

        setContent {
            GeoFenceSampleTheme {
                val systemUiController = rememberSystemUiController()
                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = StatusbarColor,
                        darkIcons = false
                    )
                }
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Destination.HOME.name) {
                    composable(route = Destination.HOME.name) {
                        Home(
                            viewModel = viewModel,
                            navController = navController,
                            onTestNotificationClicked = ::testNotification,
                        )
                    }
                    composable(route = Destination.GEOFENCE_LIST.name) {
                        GeofenceList(
                            viewModel = viewModel,
                            navController = navController
                        )
                    }
                    composable(route = Destination.LOG_LIST.name) {
                        LogList(navController = navController)
                    }
                    composable(route = Destination.GEOFENCE_REGISTRATION.name) {
                        GeofenceRegistration(navController = navController)
                    }
                }
            }
        }

        viewModel.event.observe(this) { event ->
            if (event == MainViewModel.Event.STORE_NEW_GEOFENCE) {
                setupGeofenceClient()
            }
        }
    }

    private fun setupBroadcastReceiver() {
        val filter = IntentFilter(RECEIVE_TEST_ACTION)
        registerReceiver(receiver, filter)
    }

    private fun testNotification() {
        val intent = Intent(RECEIVE_TEST_ACTION)
        sendBroadcast(intent)
    }

    private fun addLog(log: String) {
        val app = application as GeofenceApp
        app.logs.add(log to Date())
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.hasPermissionsGranted()) {
            addLog("Location permissions granted")
            setupGeofenceClient()
        } else {
            addLog("Request location permissions")
            requestLocationPermission.launch(MainViewModel.locationPermissions)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        geofencingClient.removeGeofences(geofencePendingIntent)
    }

    @SuppressLint("MissingPermission")
    private fun setupGeofenceClient() {
        val request = viewModel.getGeofencingRequest() ?: run {
            addLog("Geofence request is null")
            return
        }
        geofencingClient = LocationServices.getGeofencingClient(this)
        geofencingClient.addGeofences(request, geofencePendingIntent).run {
            addOnSuccessListener {
                addLog("addGeofences success")
            }
            addOnFailureListener {
                addLog("addGeofences failure")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setupCurrentLocation(granted: Boolean) {
        val fusedClient = LocationServices.getFusedLocationProviderClient(this)
        val request = LocationRequest.Builder(10000).build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                val location = result.lastLocation
                viewModel.setCurrentLocation(location)
            }
        }
        if (granted) {
            fusedClient.lastLocation.addOnCompleteListener { task ->
                val location = task.result
                viewModel.setCurrentLocation(location)
            }
            fusedClient.requestLocationUpdates(
                request,
                callback,
                Looper.getMainLooper()
            )
        }
    }
}
