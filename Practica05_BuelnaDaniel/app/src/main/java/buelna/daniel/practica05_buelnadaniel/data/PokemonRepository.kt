package buelna.daniel.practica05_buelnadaniel.data

import kotlinx.coroutines.flow.Flow

class PokemonRepository(private val pokemonDao: PokemonDao) {
    val allPokemons = pokemonDao.getAll()

    suspend fun add(pokemon: PokemonEntity) {
        pokemonDao.add(pokemon)
    }

    suspend fun delete(pokemon: PokemonEntity) {
        pokemonDao.delete(pokemon)
    }

    suspend fun update(pokemon: PokemonEntity) {
        pokemonDao.update(pokemon)
    }

    fun filterByNameOrType(text: String): Flow<List<PokemonEntity>> {
        return pokemonDao.filterByNameOrType(text)
    }

    fun filterByMinLevel(minLevel: Int): Flow<List<PokemonEntity>> {
        return pokemonDao.filterByMinLevel(minLevel)
    }

    fun filterByNameOrTypeAndLevel(text: String, minLevel: Int = 1): Flow<List<PokemonEntity>> {
        return pokemonDao.filterByNameTypeAndLevel(text, minLevel)
    }
}