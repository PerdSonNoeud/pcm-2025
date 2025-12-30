package fr.uparis.jinzana.pcmprojet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ScoreViewModel(
    private val dao: ScoreDao
) : ViewModel() {

    fun addScore(
        quizName: String,
        difficulty: Difficulty,
        playerTag: String,
        score: Int
    ) {
        viewModelScope.launch {
            dao.insertScore(
                ScoreEntity(
                    quizName = quizName,
                    difficulty = difficulty,
                    playerTag = playerTag,
                    score = score
                )
            )

            dao.trimScores(quizName, difficulty)
        }
    }

    fun getTop5Scores(
        quizName: String,
        difficulty: Difficulty
    ): Flow<List<ScoreEntity>> {
        return dao.getTop5Scores(quizName,difficulty)
            .catch { emit(emptyList()) }
    }
}
