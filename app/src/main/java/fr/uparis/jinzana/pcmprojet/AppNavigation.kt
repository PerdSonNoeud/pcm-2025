package fr.uparis.jinzana.pcmprojet

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import fr.uparis.jinzana.pcmprojet.database.PokeDB
import fr.uparis.jinzana.pcmprojet.quiz.Cris_Quiz
import fr.uparis.jinzana.pcmprojet.quiz.PokedexQuiz
import fr.uparis.jinzana.pcmprojet.quiz.Shadow_Quiz
import fr.uparis.jinzana.pcmprojet.quiz.StatQuiz
import fr.uparis.jinzana.pcmprojet.utils.importPokemonsFromCSV


val LocalNavBackStack = compositionLocalOf<NavBackStack<NavKey>> {
    error("NavBackStack not provided")
}


@Composable
fun AppNavigation(
    innerPadding: PaddingValues,
) {
    val backStack = rememberNavBackStack(Screen.HomeScreen)
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val db = PokeDB.getInstance(context)
        importPokemonsFromCSV(context, db.pokeDao())
    }
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
                entry<Screen.StatScreen> {
                    StatQuiz(modifier = Modifier.padding(innerPadding))
                }
                entry<Screen.ShadowQuiz> {
                    Shadow_Quiz(modifier = Modifier.padding(innerPadding))
                }
                entry<Screen.CrisQuiz> {
                    Cris_Quiz(modifier = Modifier.padding(innerPadding))
                }
            }
        )
    }
}
