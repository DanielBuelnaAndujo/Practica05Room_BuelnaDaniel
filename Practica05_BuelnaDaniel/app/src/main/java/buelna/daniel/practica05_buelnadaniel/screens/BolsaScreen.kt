package buelna.daniel.practica05_buelnadaniel.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import buelna.daniel.practica05_buelnadaniel.viewModel.PokemonViewModel
import kotlinx.coroutines.launch

@Composable
fun BolsaScreen(pokemonViewModel: PokemonViewModel) {
    val pokemons by pokemonViewModel.pokemonsState.collectAsStateWithLifecycle()
    val query by pokemonViewModel.searchQuery.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var levelFilter by remember { mutableFloatStateOf(1f) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(20.dp))

            Text(
                "Bolsa de Pokemon",
                style = MaterialTheme.typography.headlineLarge
            )

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { pokemonViewModel.onSearchQueryChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar por min level, name o type...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { pokemonViewModel.onSearchQueryChanged("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Borrar")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(50),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )

            Spacer(Modifier.height(8.dp))

            Column(horizontalAlignment = Alignment.Start, modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Filtrar por nivel mínimo: ${levelFilter.toInt()}",
                    style = MaterialTheme.typography.labelLarge
                )

                Slider(
                    value = levelFilter,
                    onValueChange = {
                        levelFilter = it
                        pokemonViewModel.onLevelFilterChanged(it.toInt()) // Avisamos al ViewModel
                    },
                    valueRange = 1f..100f, // Rango de niveles Pokémon
                    steps = 98, // Esto crea "puntos" para que el slider se mueva de 1 en 1
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(pokemons) { pokemon ->
                    Card(
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ListItem(
                            headlineContent = { Text("${pokemon.name} (Nivel ${pokemon.level})") },
                            supportingContent = { Text(pokemon.type) },
                            trailingContent = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            pokemonViewModel.tryLevelUp(pokemon) { mensaje ->
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(mensaje)
                                                }
                                            }
                                        }
                                    ) {
                                        Text("Subir nivel")
                                    }

                                    Button(
                                        onClick = {
                                            pokemonViewModel.selectedPokemonToDelete(pokemon)
                                            showDeleteDialog = true
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Text("Eliminar")
                                    }
                                }
                            }
                        )
                    }
                }
            }

            if (showDeleteDialog && pokemonViewModel.pokemonToDelete != null) {
                AlertDialog(
                    onDismissRequest = {
                        showDeleteDialog = false
                    },
                    title = { Text("Confirmar eliminación") },
                    text = { Text("¿Estás seguro de que quieres liberar a ${pokemonViewModel.pokemonToDelete?.name}? No podrás deshacer esta acción.") },
                    confirmButton = {
                        Button(
                            onClick = {
                                pokemonViewModel.deletePokemon()
                                showDeleteDialog = false
                                pokemonViewModel.selectedPokemonToDelete(null)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Eliminar")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}