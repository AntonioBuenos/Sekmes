package com.example.sekmeszodynas

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {
    private val databaseName = "migration-test.db"

    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        requireNotNull(SekmesDatabase::class.java.canonicalName),
        FrameworkSQLiteOpenHelperFactory(),
    )

    @Test
    fun migrationFrom1To2PreservesExistingWordProgress() {
        helper.createDatabase(databaseName, 1).apply {
            execSQL("INSERT INTO word_progress (wordId, status, correctCount, errorCount, streak, lastSeenAtEpochMillis, nextReviewAtEpochMillis, updatedAtEpochMillis) VALUES ('legacy_word', 'HARD', 5, 2, 1, NULL, NULL, 123)")
            close()
        }

        helper.runMigrationsAndValidate(databaseName, 2, true, SekmesDatabase.MIGRATION_1_2).apply {
            query("SELECT status, correctCount, errorCount FROM word_progress WHERE wordId = 'legacy_word'").use { cursor ->
                cursor.moveToFirst()
                assertEquals("HARD", cursor.getString(0))
                assertEquals(5, cursor.getInt(1))
                assertEquals(2, cursor.getInt(2))
            }
            query("SELECT name FROM sqlite_master WHERE type='table' AND name='custom_words'").use { cursor ->
                assertEquals(true, cursor.moveToFirst())
            }
            close()
        }
    }
}
