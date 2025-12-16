package fr.uparis.jinzana.pcmprojet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier

import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay

import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

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

    CompositionLocalProvider(LocalNavBackStack provides backStack) {
        NavDisplay(
            backStack = backStack,
            entryProvider = entryProvider {
                entry<Screen.HomeScreen> {
                    SelectMode(modifier = Modifier.padding(innerPadding))
                }
                entry<Screen.PokedexScreen> {
                    PokedexQuiz(modifier = Modifier.padding(innerPadding))
                }
                entry<Screen.LivresScreen> {
                    Text("Écran Livres")
                }
            }
        )
    }
}
