package fr.uparis.jinzana.pcmprojet



import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Book
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Screen : NavKey {
    val icon: ImageVector
}

@Serializable
data object HomeScreen : Screen {
    override val icon = Icons.Filled.Home
}

@Serializable
data object AuteursScreen : Screen {
    override val icon = Icons.Filled.People
}

@Serializable
data object ShadowQuiz : Screen {
    override val icon = Icons.Filled.Book
}

@Serializable
data object CrisQuiz : Screen {
    override val icon = Icons.Filled.Book
}



