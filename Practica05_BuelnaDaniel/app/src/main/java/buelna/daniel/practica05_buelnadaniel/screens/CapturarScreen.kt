package buelna.daniel.practica05_buelnadaniel.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import buelna.daniel.practica05_buelnadaniel.viewModel.PokemonViewModel

@Composable
fun CapturarScreen(
    pokemonViewModel: PokemonViewModel,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { pokemonViewModel.searchPokemon() }
        ) {
            Text("Buscar en la hierba")
        }

        Spacer(modifier = Modifier.height(16.dp))

        pokemonViewModel.wildPokemon?.let { pokemon ->

            Text("¡Apareció un ${pokemon.name}!")
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { pokemonViewModel.capturePokemon() }
            ) {
                Text("Capturar")
            }
        }

        if (pokemonViewModel.pokemonSeEscapo) {
            Text("El pokemon se escapó", color = Color.Red)
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text("Pokémons capturados: ${pokemonViewModel.capturedPokemons.size}")

        LazyColumn {

            items(pokemonViewModel.capturedPokemons) { pokemon ->

                Text("${pokemon.name} - ${pokemon.type}")

            }
        }
        Spacer(modifier = Modifier.height(28.dp))
        ElevatedButton(
            onClick = {
                for (pokemon in pokemonViewModel.capturedPokemons) {
                    pokemonViewModel.addPokemon(pokemon.name, pokemon.number, pokemon.type)
                }
                onBack()
            }
        ) {
            Text("Mandar a bolsa")
        }

        TextButton(
            onClick = {
                pokemonViewModel.releaseCapturedPokemons()
            }
        ) {
            Text("Liberar pokemones")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CapturarScreenPreview() {
    //CapturarScreen(onBack = {})
}