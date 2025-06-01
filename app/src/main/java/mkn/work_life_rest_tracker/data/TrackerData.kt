package mkn.work_life_rest_tracker.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "tracker_prefs")

object TrackerKeys {
    val WORK = intPreferencesKey("work_minutes")
    val LIFE = intPreferencesKey("life_minutes")
    val REST = intPreferencesKey("rest_minutes")
    val TOTAL = intPreferencesKey("total_minutes")
}

class TrackerData(private val context: Context) {
    val stats: Flow<Triple<Int, Int, Int>> = context.dataStore.data.map { prefs ->
        Triple(
            prefs[TrackerKeys.WORK] ?: 0,
            prefs[TrackerKeys.LIFE] ?: 0,
            prefs[TrackerKeys.REST] ?: 0
        )
    }

    suspend fun addReport(work: Int, life: Int, rest: Int) {
        context.dataStore.edit { prefs ->
            prefs[TrackerKeys.WORK] = (prefs[TrackerKeys.WORK] ?: 0) + work
            prefs[TrackerKeys.LIFE] = (prefs[TrackerKeys.LIFE] ?: 0) + life
            prefs[TrackerKeys.REST] = (prefs[TrackerKeys.REST] ?: 0) + rest
            prefs[TrackerKeys.TOTAL] = (prefs[TrackerKeys.TOTAL] ?: 0) + (work + life + rest)
        }
    }
}