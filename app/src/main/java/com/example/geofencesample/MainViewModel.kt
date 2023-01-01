package com.example.geofencesample

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import java.util.Date

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context
        get() = getApplication<Application>().applicationContext

    private val helper: GeofencePreferences = GeofencePreferences(context)

    private val _event = MutableLiveData<Event>()
    val event: LiveData<Event> = _event

    private val _currentLocation = MutableLiveData<Location>()
    val currentLocation: LiveData<Location> = _currentLocation

    fun setCurrentLocation(location: Location?) {
        _currentLocation.value = location
    }

    enum class Event {
        STORE_NEW_GEOFENCE
    }

    private val _logs = MutableLiveData<MutableList<Pair<String, Date>>>(mutableListOf())
    val logs: LiveData<MutableList<Pair<String, Date>>> = _logs

    private val _entries = MutableLiveData<List<Entry>>()
    val entries: LiveData<List<Entry>> = _entries

    fun loadGeofences() {
        _entries.value = helper.loadGeofences()
    }

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
        Log.d(this::class.java.simpleName, "Entry: $entry")
        helper.store(entry)
        _event.value = Event.STORE_NEW_GEOFENCE
    }

    fun updateLogs() {
        _logs.value = (context as GeofenceApp).logs
    }

    private fun createGeofence(entry: Entry): Geofence {
        return Geofence.Builder()
            .setRequestId(entry.key)
            .setCircularRegion(entry.latitude, entry.longitude, entry.radius)
            .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS) // 有効期限なし
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()
    }

    fun removeGeofence(entry: Entry) {
        Log.d(this::class.java.simpleName, "Call removeGeofence")
        helper.remove(entry)
        _entries.value = helper.loadGeofences()
    }

    fun getGeofencingRequest(): GeofencingRequest? {

        val entries = helper.loadGeofences()

        if (entries.isEmpty()) {
            return null
        }

        val geofences = entries.map(::createGeofence)

        Log.d(this::class.java.simpleName, "geofences: ${geofences.size}")

        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(geofences)
        }.build()
    }

    fun hasPermissionsGranted(): Boolean {
        return locationPermissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        val locationPermissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        )
        private const val GEOFENCE_EXPIRATION_IN_HOURS: Long = 12
        const val GEOFENCE_EXPIRATION_IN_MILLISECONDS: Long = GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000
    }
}

data class Entry(
    val key: String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float
)
