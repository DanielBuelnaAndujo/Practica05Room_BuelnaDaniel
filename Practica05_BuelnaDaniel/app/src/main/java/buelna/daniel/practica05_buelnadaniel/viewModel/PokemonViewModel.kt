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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PokemonViewModel(private val repository: PokemonRepository): ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _minLevel = MutableStateFlow(1)
    val minLevel = _minLevel.asStateFlow()

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

    var pokemonToDelete by mutableStateOf<PokemonEntity?>(value = null)
        private set

    var wildPokemon by mutableStateOf<PokemonEntity?>(value = null)
        private set

    var capturedPokemons by mutableStateOf(value = listOf<PokemonEntity>())
        private set

    var pokemonSeEscapo by mutableStateOf(value = false)
        private set

    fun selectedPokemonToDelete(pokemon: PokemonEntity?) {
        pokemonToDelete = pokemon
    }

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

    val pokemonsState: StateFlow<List<PokemonEntity>> = combine(
        _searchQuery,
        _minLevel
    ) { query, level ->
        // Cada vez que cambie el texto O el nivel, se crea este par
        Pair(query, level)
    }.flatMapLatest { (query, level) ->
        // Llamamos al nuevo método del DAO que creamos antes
        repository.filterByNameOrTypeAndLevel("%$query%", level)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Función para que el Slider avise al ViewModel
    fun onLevelFilterChanged(newLevel: Int) {
        _minLevel.value = newLevel
    }
    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun deletePokemon() {
        viewModelScope.launch {
            pokemonToDelete?.let {
                repository.delete(it)
            }
        }
    }

    fun tryLevelUp(pokemon: PokemonEntity, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val success = (1..100).random() > 50

            if (pokemon.level >= 100) {
                onResult("El Pokemon: ${pokemon.name} ha alcanzado el nivel máximo")
            } else {
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
}