package buelna.daniel.practica05_buelnadaniel.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {
    @Query("SELECT * FROM pokemon_table ORDER BY number ASC")
    fun getAll(): Flow<List<PokemonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(pokemon: PokemonEntity)

    @Delete
    suspend fun delete(pokemon: PokemonEntity)

    @Update
    suspend fun update(pokemon: PokemonEntity)

    @Query("SELECT * FROM pokemon_table WHERE name LIKE :text OR type LIKE :text")
    fun filterByNameOrType(text: String): Flow<List<PokemonEntity>>

    @Query("SELECT * FROM pokemon_table WHERE level >= :minLevel ORDER BY level DESC")
    fun filterByMinLevel(minLevel: Int): Flow<List<PokemonEntity>>
}