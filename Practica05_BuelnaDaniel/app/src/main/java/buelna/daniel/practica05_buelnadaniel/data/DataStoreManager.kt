package buelna.daniel.practica05_buelnadaniel.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataStoreManager(private val context: Context) {

    private val Context.dataStore by preferencesDataStore(name = "session_prefs")

    companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USERNAME = stringPreferencesKey("username")
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data
        .map { it[IS_LOGGED_IN] ?: false }

    val usernameFlow: Flow<String> = context.dataStore.data
        .map { it[USERNAME] ?: "" }

    suspend fun saveSession(username: String) {
        context.dataStore.edit {
            it[IS_LOGGED_IN] = true
            it[USERNAME] = username
        }
    }

    suspend fun logout() {
        context.dataStore.edit {
            it.clear()
        }
    }
}