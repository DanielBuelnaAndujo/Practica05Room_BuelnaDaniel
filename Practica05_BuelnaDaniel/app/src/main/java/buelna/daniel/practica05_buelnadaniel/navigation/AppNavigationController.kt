package buelna.daniel.practica05_buelnadaniel.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import buelna.daniel.practica05_buelnadaniel.screens.BolsaScreen
import buelna.daniel.practica05_buelnadaniel.screens.CapturarScreen
import buelna.daniel.practica05_buelnadaniel.screens.HomeScreen
import buelna.daniel.practica05_buelnadaniel.screens.LoginScreen
import buelna.daniel.practica05_buelnadaniel.viewModel.AuthViewModel
import buelna.daniel.practica05_buelnadaniel.viewModel.PokemonViewModel

sealed class Screen(val route: String) {
    object Login: Screen("login")
    object Home: Screen("home")
    object Bolsa: Screen("bolsa")
    object Capturar: Screen("capturar")
}

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    pokemonViewModel: PokemonViewModel
) {
    val navController = rememberNavController()

    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val username by authViewModel.username.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if(isLoggedIn) Screen.Home.route else Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(authViewModel)
        }

        composable(Screen.Home.route) {
            HomeScreen(username,
                onLogout = {
                authViewModel.logout()
            },
                onBolsaClick = { navController.navigate(Screen.Bolsa.route)},
                onCapturarClick = { navController.navigate(Screen.Capturar.route)}
            )
        }
        composable(Screen.Bolsa.route) {
            BolsaScreen(pokemonViewModel)
        }
        composable(Screen.Capturar.route) {
            CapturarScreen(pokemonViewModel, onBack = { navController.popBackStack() })
        }
    }
}