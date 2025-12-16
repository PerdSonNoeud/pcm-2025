package fr.uparis.jinzana.pcmprojet

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScoreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(score: ScoreEntity)


    @Query("""
        SELECT * FROM scores
        WHERE quizName = :quizName AND difficulty = :difficulty
        ORDER BY score DESC
        LIMIT 5
    """)
     fun getTop5Scores(
        quizName: String,
        difficulty: Difficulty
    ): Flow<List<ScoreEntity>>


    @Query("""
        DELETE FROM scores
        WHERE id NOT IN (
            SELECT id FROM scores
            WHERE quizName = :quizName AND difficulty = :difficulty
            ORDER BY score DESC
            LIMIT 5
        )
        AND quizName = :quizName
        AND difficulty = :difficulty
    """)
    suspend fun trimScores(
        quizName: String,
        difficulty: Difficulty
    )
}
