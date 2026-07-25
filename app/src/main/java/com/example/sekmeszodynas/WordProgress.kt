package com.example.sekmeszodynas

import android.content.Context
import androidx.room.Database
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class WordLearningStatus {
    NEW,
    LEARNING,
    HARD,
    KNOWN,
}

data class WordProgress(
    val wordId: WordId,
    val status: WordLearningStatus = WordLearningStatus.NEW,
    val correctCount: Int = 0,
    val errorCount: Int = 0,
    val streak: Int = 0,
    val lastSeenAtEpochMillis: Long? = null,
    val nextReviewAtEpochMillis: Long? = null,
    val updatedAtEpochMillis: Long = 0,
)

@Entity(tableName = "word_progress")
data class WordProgressEntity(
    @PrimaryKey val wordId: WordId,
    val status: WordLearningStatus,
    val correctCount: Int,
    val errorCount: Int,
    val streak: Int,
    val lastSeenAtEpochMillis: Long?,
    val nextReviewAtEpochMillis: Long?,
    val updatedAtEpochMillis: Long,
)

class WordProgressConverters {
    @TypeConverter
    fun statusToString(status: WordLearningStatus): String = status.name

    @TypeConverter
    fun stringToStatus(value: String): WordLearningStatus = WordLearningStatus.valueOf(value)
}

@Dao
interface WordProgressDao {
    @Query("SELECT * FROM word_progress ORDER BY wordId")
    fun observeAll(): Flow<List<WordProgressEntity>>

    @Query("SELECT * FROM word_progress WHERE wordId = :wordId")
    fun observe(wordId: WordId): Flow<WordProgressEntity?>

    @Query("SELECT * FROM word_progress WHERE wordId = :wordId")
    suspend fun get(wordId: WordId): WordProgressEntity?

    @Upsert
    suspend fun upsert(progress: WordProgressEntity)

    @Query("DELETE FROM word_progress WHERE wordId = :wordId")
    suspend fun delete(wordId: WordId)
}

@Database(
    entities = [WordProgressEntity::class],
    version = 1,
    exportSchema = true,
)
@TypeConverters(WordProgressConverters::class)
abstract class SekmesDatabase : RoomDatabase() {
    abstract fun wordProgressDao(): WordProgressDao

    companion object {
        @Volatile
        private var instance: SekmesDatabase? = null

        fun getInstance(context: Context): SekmesDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SekmesDatabase::class.java,
                    "sekmes.db",
                ).build().also { instance = it }
            }
    }
}

interface WordProgressRepository {
    fun observeAll(): Flow<Map<WordId, WordProgress>>
    fun observe(wordId: WordId): Flow<WordProgress>
    suspend fun get(wordId: WordId): WordProgress
    suspend fun setStatus(
        wordId: WordId,
        status: WordLearningStatus,
        updatedAtEpochMillis: Long = System.currentTimeMillis(),
    )
    suspend fun reset(wordId: WordId)
}

object ProgressStore {
    private var progressRepository: WordProgressRepository? = null

    fun initialize(context: Context) {
        progressRepository = RoomWordProgressRepository(
            SekmesDatabase.getInstance(context).wordProgressDao(),
        )
    }

    fun repository(): WordProgressRepository =
        requireNotNull(progressRepository) { "ProgressStore must be initialized before use" }
}

class RoomWordProgressRepository(
    private val dao: WordProgressDao,
) : WordProgressRepository {
    override fun observeAll(): Flow<Map<WordId, WordProgress>> =
        dao.observeAll().map { entities ->
            entities.associate { entity -> entity.wordId to entity.toDomain() }
        }

    override fun observe(wordId: WordId): Flow<WordProgress> =
        dao.observe(wordId).map { entity -> entity?.toDomain() ?: WordProgress(wordId) }

    override suspend fun get(wordId: WordId): WordProgress =
        dao.get(wordId)?.toDomain() ?: WordProgress(wordId)

    override suspend fun setStatus(
        wordId: WordId,
        status: WordLearningStatus,
        updatedAtEpochMillis: Long,
    ) {
        val current = get(wordId)
        dao.upsert(
            current.copy(
                status = status,
                updatedAtEpochMillis = updatedAtEpochMillis,
            ).toEntity(),
        )
    }

    override suspend fun reset(wordId: WordId) {
        dao.delete(wordId)
    }
}

private fun WordProgressEntity.toDomain(): WordProgress = WordProgress(
    wordId = wordId,
    status = status,
    correctCount = correctCount,
    errorCount = errorCount,
    streak = streak,
    lastSeenAtEpochMillis = lastSeenAtEpochMillis,
    nextReviewAtEpochMillis = nextReviewAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
)

private fun WordProgress.toEntity(): WordProgressEntity = WordProgressEntity(
    wordId = wordId,
    status = status,
    correctCount = correctCount,
    errorCount = errorCount,
    streak = streak,
    lastSeenAtEpochMillis = lastSeenAtEpochMillis,
    nextReviewAtEpochMillis = nextReviewAtEpochMillis,
    updatedAtEpochMillis = updatedAtEpochMillis,
)
