package fr.uparis.jinzana.pcmprojet


import android.annotation.SuppressLint
import android.widget.Toast

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Color

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf


import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.delay


val Pokemon_Police = FontFamily(Font(R.font.pokemonsolid, FontWeight.Bold))

@SuppressLint("DefaultLocale")
@Composable
fun SelectMode(
    modifier: Modifier = Modifier,
    musicManager: MusicViewModel = viewModel(),
    viewModel: HomescreenViewModel = viewModel(),
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFCC5500))
    ) {
        val scope=this
        var openSettings by remember { mutableStateOf(false) }
        val isLandscape = maxWidth > maxHeight
        val settingsViewModel: SettingsViewModel = viewModel(
            factory = SettingsViewModelFactory(LocalContext.current)
        )
        val settings by settingsViewModel.settings.collectAsState()
        val lifecycleOwner = LocalLifecycleOwner.current
        PageMusic(R.raw.menu, settings.musicVolume)
        if (openSettings) {
            SettingsDialog(
                settingsViewModel = settingsViewModel,
                onDismiss = { openSettings = false }
            )
        }

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
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(16.dp)
                } else {
                    Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.6f)
                        .padding(16.dp)
                }
            ) {
                if (isLandscape) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFFF8C00))
                            .padding(20.dp),
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
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
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
            .clickable { openSettings = true }
        )
    }
        if(!settings.premium){
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = if (isLandscape) Arrangement.Start else Arrangement.End,
            verticalAlignment = Alignment.Top
        ) {

            var secondsLeft by remember {
                mutableIntStateOf(
                    (settingsViewModel.timeUntilNextHeart() / 1000).toInt()
                )
            }

            LaunchedEffect(Unit) {
                while (true) {
                    secondsLeft = (settingsViewModel.timeUntilNextHeart() / 1000).toInt()
                    delay(1000)
                }
            }
            Image(
                painter = painterResource(id = viewModel.heart),
                contentDescription = "Heart",
                modifier = Modifier.size(30.dp)
            )
            Text(
                text = " : ${settings.currentHearts}/${settings.maxHearts}",
                fontSize = 24.sp
            )
            val minutes = secondsLeft / 60
            val seconds = secondsLeft % 60
            val formattedSeconds = seconds.toString().padStart(2, '0')

            if (settings.currentHearts != settings.maxHearts) {
                Text(
                    text = "($minutes:$formattedSeconds)",
                    fontSize = 16.sp
                )
            }
        }
        }
    }
}

@Composable
fun ContentCard(isLandscape: Boolean){
    val soundVM: SoundViewModel =  viewModel(
        factory = SoundViewModelFactory(LocalContext.current)
    )
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(LocalContext.current)
    )

    val settings by settingsViewModel.settings.collectAsState()
    var showQuizChoice by remember { mutableStateOf(false) }
    var showLeaderboard by remember { mutableStateOf(false) }


    Button(
        onClick = { showQuizChoice=true
        soundVM.play("click", settings.soundVolume) },modifier = Modifier
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
    ) { }
        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                showLeaderboard = true
                soundVM.play("click", settings.soundVolume)},
            modifier = Modifier
                .fillMaxWidth(if (isLandscape) 0.4f else 0.8f)
                .height(35.dp)
        ) {
            Text("Score")
        }
    if (showLeaderboard) {
        val db = PokeDB.getInstance(LocalContext.current)
        val dao = db.scoreDao()
        val scoreViewModel: ScoreViewModel = viewModel(
            factory = ScoreViewModelFactory(dao)
        )
        LeaderboardDialog(
            scoreViewModel = scoreViewModel,
            quizNames = listOf("Shadow", "Pokedex", "Stat","Cris"),
            onDismiss = { showLeaderboard = false }
        )
    }
    Spacer(modifier = Modifier.height(30.dp))
    Button(
        onClick = {

            soundVM.play("click", settings.soundVolume)
            if (settings.currentHearts > 0 && !settings.premium) {
                val newHeartCount = settings.currentHearts - 1
                settingsViewModel.updateNotif(true,newHeartCount)
            }
        },
        modifier = Modifier
            .fillMaxWidth(if (isLandscape) 0.4f else 0.8f)
            .height(35.dp)
    ) {
        Text("--Coeur?")
    }
    if(isLandscape){
        howquizchoice(
            isvisible = showQuizChoice,
            onDismiss = { showQuizChoice = false }
        )
    }else{
        howquizchoice(
            isvisible = showQuizChoice,
            onDismiss = { showQuizChoice = false }
        )
    }
}

@Composable
fun howquizchoice(
    isvisible: Boolean,
    onDismiss: () -> Unit,
) {
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(LocalContext.current)
    )


    val settings by settingsViewModel.settings.collectAsState()

    val sliderColor = when (settings.difficulty) {
        Difficulty.EASY -> Color.Green
        Difficulty.MEDIUM -> Color.Yellow
        Difficulty.HARD -> Color.Red
    }
    if (isvisible) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFFA500), shape = RoundedCornerShape(24.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = "Quelle quiz ?",
                        fontSize = 20.sp,
                        fontFamily = Pokemon_Police,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )

                    Spacer(modifier = Modifier.height(16.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OptionBox("Shadow","shadow",onDismiss)
                        OptionBox("Pokedex","pokedex",onDismiss)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OptionBox("Stat","stat", onDismiss)
                        OptionBox("Cris","cris",onDismiss)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // DIFFICULTÉ
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Difficulté : ",
                            fontSize = 18.sp,
                            fontFamily = Pokemon_Police
                        )
                        Text(
                            text = "${settings.difficulty}",
                            fontSize = 18.sp,
                            fontFamily = Pokemon_Police,
                            color = sliderColor
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    var sliderValue by remember(settings.difficulty) {
                        androidx.compose.runtime.mutableFloatStateOf(
                            when (settings.difficulty) {
                                Difficulty.EASY -> 0f
                                Difficulty.MEDIUM -> 1f
                                Difficulty.HARD -> 2f
                            }
                        )
                    }

                    Slider(
                        value = sliderValue,
                        onValueChange = { value ->
                            sliderValue = value
                            val difficulty = when (value) {
                                in 0f..0.5f -> Difficulty.EASY
                                in 0.5f..1.5f -> Difficulty.MEDIUM
                                else -> Difficulty.HARD
                            }
                            settingsViewModel.updateDifficulty(difficulty)
                        },
                        valueRange = 0f..2f,
                        steps = 1,
                        colors = SliderDefaults.colors(
                            thumbColor = sliderColor,
                            activeTrackColor = sliderColor,
                        )
                    )
                }
            }
        }
    }
}
@Composable
fun SettingsDialog(
    musicManager: MusicViewModel = viewModel(),
    settingsViewModel: SettingsViewModel,
    onDismiss: () -> Unit
) {
    val settings by settingsViewModel.settings.collectAsState()

    AlertDialog(
        containerColor = Color(0xFFFFA500),
        onDismissRequest = onDismiss,
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
             ,
                contentAlignment = Alignment.Center
            ) {
                Text("Paramètres",fontFamily = Pokemon_Police)
            }
        },

        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {


                var newVolume by remember { mutableStateOf(settings.musicVolume) }
                Text("Musique : ${(newVolume * 100).toInt()}%",fontFamily = Pokemon_Police, color = Color.Black)
                Slider(
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFD6492F),
                        activeTrackColor = Color(0xFFD6492F),
                    ),
                    value =newVolume,
                    onValueChange = { newValue ->
                        newVolume = newValue


                    },
                    onValueChangeFinished = {

                        settingsViewModel.updateMusicVolume(newVolume)
                        musicManager.setVolume(newVolume)
                    },
                    valueRange = 0f..1f

                )


                Text("Sons : ${(settings.soundVolume * 100).toInt()}%",fontFamily = Pokemon_Police, color = Color.Black)
                Slider(
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFD6492F),
                        activeTrackColor = Color(0xFFD6492F),
                    ),
                    value = settings.soundVolume,
                    onValueChange = { newValue ->
                        settingsViewModel.updateSoundVolume(newValue)
                    },
                    valueRange = 0f..1f
                )


                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Premium",fontFamily = Pokemon_Police, color = Color.Black)
                    Switch(
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.Yellow,
                            uncheckedThumbColor = Color.Gray,
                            checkedTrackColor = Color(0xFFFFF59D),
                            uncheckedTrackColor = Color.LightGray
                        ),
                        checked = settings.premium,
                        onCheckedChange = { enabled ->
                            settingsViewModel.updatePremium(enabled)
                        }
                    )
                }
            }
        },

        confirmButton = {

        }
    )
}



@Composable
fun OptionBox(label: String,image_type : String,    onDismiss: () -> Unit) {

    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(LocalContext.current)
    )
    val soundVM: SoundViewModel =  viewModel(
        factory = SoundViewModelFactory(LocalContext.current)
    )
    val backStack =  LocalNavBackStack.current


    val settings by settingsViewModel.settings.collectAsState()
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

            images[image_type]?.let {
                Image(
                    painter = painterResource(id = it),
                    contentDescription = "Option",
                    modifier = Modifier.size(60.dp)
                )
            }
           val context =LocalContext.current

            // Bouton en bas
            Button(
                onClick = {
                    soundVM.play("click", settings.soundVolume)
                    if (settings.currentHearts > 0 || settings.premium) {
                        if(!settings.premium){
                            val newHeartCount = settings.currentHearts - 1
                            settingsViewModel.updateNotif(true, newHeartCount)
                        }
                        onDismiss()
                        val quizScreen = quizTypes[image_type]
                        if (quizScreen != null) {
                            backStack.add(quizScreen)
                        }
                    }else{
                        Toast.makeText(context, "Vous n'avez pas assez de coeur, vous connaissez la carte bleu?", Toast.LENGTH_SHORT).show()
                    }
                 },
                modifier = Modifier.fillMaxWidth().padding(2.dp)
            ) {
                Text(label)
            }
        }
    }
}



@Composable
fun LeaderboardDialog(
    scoreViewModel: ScoreViewModel,
    quizNames: List<String>,
    onDismiss: () -> Unit
) {

    var quizIndex by remember { mutableStateOf(0) }
    var selectedDifficulty by remember { mutableStateOf(Difficulty.EASY) }

    val currentQuiz = quizNames[quizIndex]


    val scores by scoreViewModel
        .getTop5Scores(currentQuiz, selectedDifficulty)
        .collectAsState(initial = emptyList())

    AlertDialog(
        containerColor = Color(0xFFFFA500),
        onDismissRequest = onDismiss,

        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    ,
                contentAlignment = Alignment.TopEnd
            ) {
                Button(
                    onClick = {
                        val difficulties = Difficulty.values()
                        val currentIndex = difficulties.indexOf(selectedDifficulty)
                        val nextIndex = (currentIndex + 1) % difficulties.size
                        selectedDifficulty = difficulties[nextIndex]
                    },  colors = ButtonDefaults.buttonColors(
                        containerColor = when (selectedDifficulty) {
                            Difficulty.EASY -> Color(0xFF006400)
                            Difficulty.MEDIUM -> Color(0xFFCCCC00)
                            Difficulty.HARD -> Color(0xFF8B0000)
                        }
                    )
                ) {
                    Text(
                        "${selectedDifficulty.name}",
                        fontFamily = Pokemon_Police
                    )
                }

            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Classement:", fontFamily = Pokemon_Police,modifier = Modifier.padding(top = 8.dp))
                Text(
                    currentQuiz,
                    fontFamily = Pokemon_Police,
                    color = Color.DarkGray
                )

            }
        },

        text = {

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                val top5 = (0 until 5).map { index ->
                    scores.getOrNull(index)
                }

                top5.forEachIndexed { index, score ->
                    if (score != null) {
                        Text(
                            "${index + 1}. ${score.playerTag} - ${score.score}",
                            fontFamily = Pokemon_Police,
                            color = Color.Black
                        )
                    } else {
                        Text(
                            "${index + 1}. ---",
                            fontFamily = Pokemon_Police,
                            color = Color.Gray
                        )
                    }
                }


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            quizIndex =
                                if (quizIndex > 0) quizIndex - 1
                                else quizNames.lastIndex
                        }
                    ) {
                        Text("<-", fontSize = 30.sp,     fontFamily = Pokemon_Police)
                    }

                    IconButton(
                        onClick = {
                            quizIndex =
                                if (quizIndex < quizNames.lastIndex) quizIndex + 1
                                else 0
                        }
                    ) {
                        Text("->", fontSize = 30.sp  ,   fontFamily = Pokemon_Police)
                    }
                }
            }
        },

        confirmButton = {}
    )
}












