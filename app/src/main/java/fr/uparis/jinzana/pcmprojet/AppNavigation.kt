package fr.uparis.jinzana.pcmprojet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier

import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import importPokemonsFromCSV

val LocalNavBackStack = compositionLocalOf<NavBackStack<NavKey>> {
    error("NavBackStack not provided")
}

@Composable
fun AppNavigation(
    innerPadding: PaddingValues,
    model: PokeViewModel = viewModel(),
) {
    model.addAllPokemons() // Populate the database with all Pokémon names
    val backStack = rememberNavBackStack(Screen.HomeScreen)
val context = LocalContext.current
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory( context)
    )
    val settings by settingsViewModel.settings.collectAsState()

    LaunchedEffect(Unit) {
        val db = PokeDB.getInstance(context)
        importPokemonsFromCSV(context, db.pokeDao())
    }
    CompositionLocalProvider(LocalNavBackStack provides backStack) {
    NavDisplay(
        backStack = backStack,
        entryProvider = entryProvider {
            entry<Screen.HomeScreen> {
                Greeting(modifier = Modifier.padding(innerPadding) )
            }
            entry<Screen.PokedexScreen> {
                PokedexQuiz(modifier = Modifier.padding(innerPadding))
            }

            entry<Screen.ShadowQuiz> {
                Shadow_Quiz(modifier = Modifier.padding(innerPadding) )
            }
            entry<Screen.CrisQuiz> {
                Cris_Quiz(modifier = Modifier.padding(innerPadding) )
            }

        }
    )
        }
}
