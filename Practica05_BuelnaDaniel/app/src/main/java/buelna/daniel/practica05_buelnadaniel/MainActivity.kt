package buelna.daniel.practica05_buelnadaniel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import buelna.daniel.practica05_buelnadaniel.data.DataStoreManager
import buelna.daniel.practica05_buelnadaniel.data.PokemonDatabase
import buelna.daniel.practica05_buelnadaniel.data.PokemonRepository
import buelna.daniel.practica05_buelnadaniel.navigation.AppNavigation
import buelna.daniel.practica05_buelnadaniel.ui.theme.Practica05_BuelnaDanielTheme
import buelna.daniel.practica05_buelnadaniel.viewModel.AuthViewModel
import buelna.daniel.practica05_buelnadaniel.viewModel.PokemonViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val authViewModel = AuthViewModel(DataStoreManager(this))
        val database by lazy { PokemonDatabase.getDatabase(this) }
        val repository by lazy { PokemonRepository(database.pokemonDao()) }
        val pokemonViewModel: PokemonViewModel by viewModels { PokemonViewModelFactory(repository) }

        setContent {
            Practica05_BuelnaDanielTheme {
                AppNavigation(authViewModel, pokemonViewModel)
            }
        }
    }
}

class PokemonViewModelFactory(private val repository: PokemonRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PokemonViewModel(repository) as T
    }
}