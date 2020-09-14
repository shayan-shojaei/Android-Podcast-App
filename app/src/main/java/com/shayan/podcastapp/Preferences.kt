package com.shayan.podcastapp

import android.content.Context
import androidx.datastore.DataStore
import androidx.datastore.preferences.*
import androidx.datastore.preferences.Preferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import java.io.Serializable

class Preferences(context: Context) {

    //DataStorage initialization
    private var dataStore: DataStore<Preferences> = context.createDataStore("prefs")

    companion object {
        //PreferenceKey declarations
        val DOUBLE_GRID = preferencesKey<Boolean>("double_grid")
    }

    //Double grid Getter and Setter
    suspend fun setDoubleGrid(doubleGrid: Boolean) {
        dataStore.edit {
            it[DOUBLE_GRID] = doubleGrid
        }
    }

    val doubleGrid: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preference ->
            preference[DOUBLE_GRID] ?: true
        }


}