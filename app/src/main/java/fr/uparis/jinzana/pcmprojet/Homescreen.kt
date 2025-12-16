package fr.uparis.jinzana.pcmprojet

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Color

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog


import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp

val Pokemon_Police = FontFamily(Font(R.font.pokemonsolid, FontWeight.Bold))

@Composable
fun SelectMode(
    modifier: Modifier = Modifier,
    viewModel: HomescreenViewModel = viewModel(),
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCC5500))
    ) {
        val scope=this
        val isLandscape = maxWidth > maxHeight

        Column(
            modifier = modifier.fillMaxSize()
                .background(Color(0xFFCC5500)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val imageSize : Dp by remember(scope.maxWidth, scope.maxHeight) {
                mutableStateOf(if (isLandscape) scope.maxHeight * 0.4f else scope.maxWidth * 0.6f)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = viewModel.pokequiz),
                    contentDescription = "Pokequiz",
                    modifier = Modifier.size(imageSize)
                )
            }
            Card(
                modifier = if (isLandscape) {
                    Modifier
                        .fillMaxWidth()   // 60% de la largeur en paysage
                        .fillMaxHeight()      // toute la hauteur
                        .padding(16.dp)
                } else {
                    Modifier
                        .fillMaxWidth()       // toute la largeur en portrait
                        .fillMaxHeight(0.6f)  // 60% de la hauteur
                        .padding(16.dp)
                }
            ) {
                if (isLandscape) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFF8C00))  // fond
                            .padding(20.dp),                // marge intérieure
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ContentCard(true)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFF8C00))
                            .padding(20.dp), // espace intérieur
                        verticalArrangement = Arrangement.Center, // centre verticalement
                        horizontalAlignment = Alignment.CenterHorizontally // centre horizontalement
                    ) {
                        ContentCard(false)
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = if (isLandscape) Alignment.Top else Alignment.Bottom
        ) {
            Image(
                painter = painterResource(id = viewModel.option),
                contentDescription = "Option",
                modifier = Modifier.size(60.dp)
            )
        }
    }
}

@Composable
fun ContentCard(isLandscape: Boolean){
    var showQuizChoice by remember { mutableStateOf(false) }

    Button(
        onClick = { showQuizChoice=true },
        modifier = Modifier
            .width(250.dp)
            .height(60.dp),
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFD62929),
            contentColor = Color.White
        ),
        border = BorderStroke(2.dp, Color(0xFFFFD700)),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 10.dp,
            pressedElevation = 15.dp
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Quiz",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

    }
    Spacer(modifier = Modifier.height(30.dp))
    Button(
        onClick = {  },
        modifier = Modifier
            .fillMaxWidth(if (isLandscape) 0.4f else 0.8f) // 80% de la largeur parent
            .height(35.dp)
    ) {
        Text("Score")
    }
    Spacer(modifier = Modifier.height(30.dp))
    Button(
        onClick = { /* action 2 */ },
        modifier = Modifier
            .fillMaxWidth(if (isLandscape) 0.4f else 0.8f) // 80% de la largeur parent
            .height(35.dp)
    ) {
        Text("Credit")
    }

    Howquizchoice(
        isVisible = showQuizChoice,
        onDismiss = { showQuizChoice = false } // <-- Ici tu la fermes
    )
}

@Composable
fun Howquizchoice(
    isVisible: Boolean,
    onDismiss: () -> Unit,
) {
    if (isVisible) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            containerColor = Color(0xFFFFA500),

            title = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Quelle quiz ?",
                        fontSize = 20.sp,
                        fontFamily = Pokemon_Police
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // PREMIÈRE LIGNE
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OptionBox("Shadow","shadow")
                        OptionBox("Pokedex","pokedex")
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // DEUXIÈME LIGNE
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OptionBox("Stat","stat")
                        OptionBox("Cris","cris")
                    }
                }
            },

            confirmButton = {},
            dismissButton = {}
        )
    }
}

@Composable
fun OptionBox(label: String,image_type : String) {
    val backStack = LocalNavBackStack.current
    val viewModel: HomescreenViewModel = viewModel()
    val images = mapOf(
        "shadow" to viewModel.shadow,
        "pokedex" to viewModel.pokedex,
        "stat" to viewModel.stat,
        "cris" to viewModel.cris
    )
    val quizTypes = mapOf(
        "shadow" to Screen.PokedexScreen,
        "pokedex" to Screen.PokedexScreen,
        "stat" to Screen.PokedexScreen,
        "cris" to Screen.PokedexScreen
    )
    Box(
        modifier = Modifier
            .size(120.dp)
            .padding(4.dp)
            .background(Color(0xFFFFA500), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            images.get(image_type)?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = "Option",
                    modifier = Modifier.size(60.dp)
                )
            }

            // Bouton en bas
            Button(
                onClick = {
                    val quizScreen = quizTypes[image_type]
                    if (quizScreen != null) {
                        backStack.add(quizScreen)
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(2.dp)
            ) {
                Text(label)
            }
        }
    }
}












