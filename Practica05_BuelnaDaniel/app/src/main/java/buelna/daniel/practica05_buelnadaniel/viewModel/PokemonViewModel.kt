package buelna.daniel.practica05_buelnadaniel.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import buelna.daniel.practica05_buelnadaniel.data.PokemonEntity
import buelna.daniel.practica05_buelnadaniel.data.PokemonRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PokemonViewModel(private val repository: PokemonRepository): ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val availablePokemons = listOf(
        PokemonEntity(name = "Pikachu", number = "025", type = "Electric"),
        PokemonEntity(name = "Bulbasaur", number = "001", type = "Grass"),
        PokemonEntity(name = "Charmander", number = "004", type = "Fire"),
        PokemonEntity(name = "Squirtle", number = "007", type = "Water"),
        PokemonEntity(name = "Caterpie", number = "010", type = "Bug"),
        PokemonEntity(name = "Weedle", number = "013", type = "Bug"),
        PokemonEntity(name = "Pidgey", number = "016", type = "Normal"),
        PokemonEntity(name = "Rattata", number = "019", type = "Normal"),
        PokemonEntity(name = "Spearow", number = "021", type = "Normal"),
        PokemonEntity(name = "Ekans", number = "023", type = "Poison"),
        PokemonEntity(name = "Arbok", number = "024", type = "Poison"),
        PokemonEntity(name = "Clefairy", number = "035", type = "Fairy")
    )
    var wildPokemon by mutableStateOf<PokemonEntity?>(value = null)
        private set

    var capturedPokemons by mutableStateOf(value = listOf<PokemonEntity>())
        private set

    var pokemonSeEscapo by mutableStateOf(value = false)
        private set

    fun searchPokemon() {
        wildPokemon = availablePokemons.random()
    }

    fun releaseCapturedPokemons() {
        capturedPokemons = emptyList()
    }

    fun capturePokemon() {
        wildPokemon?.let {

            val success = (1..100).random()
            if (success > 50) {
                capturedPokemons = capturedPokemons + it
                pokemonSeEscapo = false
                wildPokemon = null
            } else {
                pokemonSeEscapo = true
                wildPokemon = null
            }
        }
    }

//    val pokemonsState: StateFlow<List<PokemonEntity>> = repository.allPokemons
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
//            initialValue = emptyList()
//        )

    fun addPokemon(name: String, number: String, type: String, level: Int = 1){
        viewModelScope.launch {
            repository.add(
                PokemonEntity(
                    name = name,
                    number = number,
                    type = type,
                    level = level
                )
            )
            wildPokemon = null
            capturedPokemons = emptyList()
        }
    }

    val pokemonsState: StateFlow<List<PokemonEntity>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                repository.allPokemons
            } else {
                if (query.isDigitsOnly()) {
                    repository.filterByMinLevel(query.toInt())
                } else {
                    repository.filterByNameOrType(query)
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun deletePokemon(pokemon: PokemonEntity) {
        viewModelScope.launch {
            repository.delete(pokemon)
        }
    }

    fun tryLevelUp(pokemon: PokemonEntity, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val success = (1..100).random() > 50

            if (success) {
                val newLevel = pokemon.level + 1
                val newPokemon = pokemon.copy(level = newLevel)
                repository.update(newPokemon)
                onResult("¡Felicidades! ${pokemon.name} subió al nivel $newLevel")
            } else {
                onResult("Lástima, ${pokemon.name} no logró subir de nivel esta vez")
            }
        }
    }
}