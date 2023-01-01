package com.example.geofencesample.geofence_registration

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import com.example.geofencesample.Entry
import com.example.geofencesample.GeofencePreferences
import com.example.geofencesample.LocationServiceImpl
import java.util.Date

class GeofenceRegistrationViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context
        get() = getApplication<Application>().applicationContext

    private val helper: GeofencePreferences = GeofencePreferences(context)

    fun storeGeofence(
        latitude: String,
        longitude: String,
        radius: String
    ) {
        val entry = Entry(
            key = Date().toString(),
            latitude = latitude.toDouble(),
            longitude = longitude.toDouble(),
            radius = radius.toFloat()
        )
        helper.store(entry)
    }

    fun getCurrentLocation(onResult: (Location) -> Unit) {
        val service = LocationServiceImpl()
        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
        val granted = locationPermissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        service.getCurrentLocation(context, granted, onResult)
    }
}
