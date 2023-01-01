package com.example.geofencesample

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class GeofencePreferences(private val context: Context) {

    private val preferences: SharedPreferences
        get() = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun store(entry: Entry) {
        val gson = Gson()
        val geofences = preferences.getStringSet(Key.GEOFENCE.name, null)
        val entries = geofences?.map { gson.fromJson(it, Entry::class.java) }?.toMutableList()
        val jsons = HashSet<String>()

        if (entries.isNullOrEmpty()) {
            jsons.add(gson.toJson(entry))
        } else {
            entries.add(entry)
            entries.map {
                jsons.add(gson.toJson(it))
            }
        }

        preferences.edit().putStringSet(Key.GEOFENCE.name, jsons).apply()
    }

    fun loadGeofences(): List<Entry> {
        val gson = Gson()
        val jsons = preferences.getStringSet(Key.GEOFENCE.name, null)
        return jsons?.map { gson.fromJson(it, Entry::class.java) } ?: emptyList()
    }

    fun remove(entry: Entry) {
        val gson = Gson()
        val jsons = preferences.getStringSet(Key.GEOFENCE.name, null)
        val entries = jsons?.map { json -> gson.fromJson(json, Entry::class.java) }
        val newJsonSets = HashSet<String>()
        entries?.filter { it.key != entry.key }?.map {
            newJsonSets.add( gson.toJson(it))
        }
        preferences.edit().putStringSet(Key.GEOFENCE.name, newJsonSets).apply()
    }

    companion object {
        private const val PREFERENCE_NAME = "GeofenceSample"
    }

    enum class Key {
        GEOFENCE
    }
}
