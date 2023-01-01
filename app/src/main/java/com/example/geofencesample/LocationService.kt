package com.example.geofencesample

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

interface LocationService {
    fun getCurrentLocation(
        context: Context,
        granted: Boolean,
        onResult: (Location) -> Unit
    )
}

class LocationServiceImpl : LocationService {

    @SuppressLint("MissingPermission")
    override fun getCurrentLocation(
        context: Context,
        granted: Boolean,
        onResult: (Location) -> Unit
    ) {
        val fusedClient = LocationServices.getFusedLocationProviderClient(context)
        val request = LocationRequest.Builder(10000).build()
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.lastLocation?.let(onResult)
            }
        }
        if (granted) {
            fusedClient.lastLocation.addOnCompleteListener { task ->
                task.result?.let(onResult)
            }
            fusedClient.requestLocationUpdates(
                request,
                callback,
                Looper.getMainLooper()
            )
        }
    }
}
