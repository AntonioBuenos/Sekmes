package com.example.sekmeszodynas

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CatalogLoaderTest {
    @Test
    fun fixtureCourseReferencesSharedDictionaryEntries() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val catalog = AssetCatalogLoader(context.assets).load()

        assertEquals(2, catalog.courses.size)
        val demoWords = catalog.wordsForLesson("shared-demo-01")
        assertEquals(listOf("word_000001", "word_000002", "word_000003", "word_000004"), demoWords.map { it.id })
        assertTrue(catalog.wordsForLesson("sekmes-01").any { it.id == "word_000001" })
    }
}
