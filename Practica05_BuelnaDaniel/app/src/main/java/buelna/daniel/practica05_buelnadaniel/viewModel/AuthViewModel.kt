package buelna.daniel.practica05_buelnadaniel.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import buelna.daniel.practica05_buelnadaniel.data.DataStoreManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(private val dataStore: DataStoreManager) : ViewModel() {

    val isLoggedIn = dataStore.isLoggedInFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        false
    )

    val username = dataStore.usernameFlow.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ""
    )

    fun login(user: String, pass: String) {
        if (user == "admin" && pass == "1234") {
            viewModelScope.launch {
                dataStore.saveSession(user)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            dataStore.logout()
        }
    }
}