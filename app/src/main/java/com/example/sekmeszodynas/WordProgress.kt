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
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

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

data class CustomWord(
    val id: WordId,
    val lt: String,
    val ru: String,
    val type: String,
    val note: String = "",
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)

@Entity(tableName = "custom_words")
data class CustomWordEntity(
    @PrimaryKey val id: WordId,
    val lt: String,
    val ru: String,
    val type: String,
    val note: String,
    val createdAtEpochMillis: Long,
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

@Dao
interface CustomWordDao {
    @Query("SELECT * FROM custom_words ORDER BY lt COLLATE NOCASE")
    fun observeAll(): Flow<List<CustomWordEntity>>

    @Query("SELECT * FROM custom_words WHERE id = :id")
    suspend fun get(id: WordId): CustomWordEntity?

    @Upsert suspend fun upsert(word: CustomWordEntity)

    @Query("DELETE FROM custom_words WHERE id = :id")
    suspend fun delete(id: WordId)
}

@Database(
    entities = [WordProgressEntity::class, CustomWordEntity::class],
    version = 2,
    exportSchema = true,
)
@TypeConverters(WordProgressConverters::class)
abstract class SekmesDatabase : RoomDatabase() {
    abstract fun wordProgressDao(): WordProgressDao
    abstract fun customWordDao(): CustomWordDao

    companion object {
        @Volatile
        private var instance: SekmesDatabase? = null

        fun getInstance(context: Context): SekmesDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    SekmesDatabase::class.java,
                    "sekmes.db",
                ).addMigrations(MIGRATION_1_2).build().also { instance = it }
            }

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS custom_words (id TEXT NOT NULL, lt TEXT NOT NULL, ru TEXT NOT NULL, type TEXT NOT NULL, note TEXT NOT NULL, createdAtEpochMillis INTEGER NOT NULL, updatedAtEpochMillis INTEGER NOT NULL, PRIMARY KEY(id))")
            }
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
    suspend fun recordAnswer(wordId: WordId, correct: Boolean)
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

interface CustomWordRepository {
    fun observeAll(): Flow<List<CustomWord>>
    suspend fun save(word: CustomWord)
    suspend fun create(lt: String, ru: String, type: String, note: String = ""): CustomWord
    suspend fun delete(id: WordId)
}

class RoomCustomWordRepository(private val dao: CustomWordDao) : CustomWordRepository {
    override fun observeAll(): Flow<List<CustomWord>> = dao.observeAll().map { it.map(CustomWordEntity::toDomain) }
    override suspend fun save(word: CustomWord) { dao.upsert(word.toEntity()) }
    override suspend fun create(lt: String, ru: String, type: String, note: String): CustomWord {
        require(lt.isNotBlank() && ru.isNotBlank()) { "Lithuanian word and translation are required" }
        val now = System.currentTimeMillis()
        val word = CustomWord("user_${UUID.randomUUID()}", lt.trim(), ru.trim(), type, note.trim(), now, now)
        save(word)
        return word
    }
    override suspend fun delete(id: WordId) { dao.delete(id); ProgressStore.repository().reset(id) }
}

object CustomWordsStore {
    private var repository: CustomWordRepository? = null
    fun initialize(context: Context) { repository = RoomCustomWordRepository(SekmesDatabase.getInstance(context).customWordDao()) }
    fun repository(): CustomWordRepository = requireNotNull(repository)
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

    override suspend fun recordAnswer(wordId: WordId, correct: Boolean) {
        val current = get(wordId)
        val now = System.currentTimeMillis()
        dao.upsert(current.copy(
            status = if (current.status == WordLearningStatus.NEW) WordLearningStatus.LEARNING else current.status,
            correctCount = current.correctCount + if (correct) 1 else 0,
            errorCount = current.errorCount + if (correct) 0 else 1,
            streak = if (correct) current.streak + 1 else 0,
            lastSeenAtEpochMillis = now,
            updatedAtEpochMillis = now,
        ).toEntity())
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

private fun CustomWordEntity.toDomain() = CustomWord(id, lt, ru, type, note, createdAtEpochMillis, updatedAtEpochMillis)
private fun CustomWord.toEntity() = CustomWordEntity(id, lt, ru, type, note, createdAtEpochMillis, updatedAtEpochMillis)
