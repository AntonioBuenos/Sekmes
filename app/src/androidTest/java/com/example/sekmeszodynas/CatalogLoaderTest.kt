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
    fun visualCardAssetsReferenceExistingConcreteNouns() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val catalog = AssetCatalogLoader(context.assets).load()

        assertEquals(204, VISUAL_CARD_ASSETS.size)
        assertEquals(VISUAL_CARD_ASSETS.size, VISUAL_CARD_ASSETS.map { it.wordId }.distinct().size)
        assertTrue(VISUAL_CARD_ASSETS.all { asset ->
            catalog.wordById[asset.wordId]?.partOfSpeech == PartOfSpeech.NOUN
        })
    }

    @Test
    fun audioCatalogMatchesPackagedRawResources() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        AudioCatalogStore.initialize(context.assets)

        val trackResourceNames = audioBooksForCourse(SEKMES_COURSE_ID)
            .flatMap { it.chapters }
            .flatMap { it.tracks }
            .map { "audio_${it.id}" }
            .toSet()
        val packagedAudioResources = R.raw::class.java.fields
            .map { it.name }
            .filter { it.startsWith("audio_") }
            .toSet()

        assertEquals(275, trackResourceNames.size)
        assertEquals(trackResourceNames, packagedAudioResources)
        val neDienosResources = audioBooksForCourse("ne-dienos-be-lietuviu-kalbos")
            .flatMap { it.chapters }
            .flatMap { it.tracks }
            .map(AudioTrack::resourceName)
            .toSet()
        val packagedNeDienosResources = R.raw::class.java.fields
            .map { it.name }
            .filter { it.startsWith("ne_dienos_") }
            .toSet()
        assertEquals(147, neDienosResources.size)
        assertEquals(neDienosResources, packagedNeDienosResources)
    }

    @Test
    fun fixtureCourseReferencesSharedDictionaryEntries() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val catalog = AssetCatalogLoader(context.assets).load()

        assertEquals(4, catalog.courses.size)
        assertEquals(12, catalog.lessonsForCourse("ne-dienos-be-lietuviu-kalbos").size)
        assertEquals(768, catalog.wordsForCourse("ne-dienos-be-lietuviu-kalbos").size)
        val lessonNine = catalog.wordsForLesson("ne-dienos-09")
        assertEquals(143, lessonNine.size)
        assertTrue(lessonNine.any { it.lt == "garbanoti plaukai" })
        assertTrue(lessonNine.any { it.lt == "greitoji pagalba" })
        assertTrue(lessonNine.any { it.lt == "mirti, miršta, mirė" })
        val lessonTen = catalog.wordsForLesson("ne-dienos-10")
        assertEquals(131, lessonTen.size)
        assertTrue(lessonTen.any { it.lt == "direktoriaus pavaduotojas, pavaduotoja" })
        assertTrue(lessonTen.any { it.lt == "elektroninis paštas" })
        assertTrue(lessonTen.any { it.lt == "banknotas" })
        assertEquals(CourseVisibility.LEARNING, catalog.courseById.getValue("ne-dienos-be-lietuviu-kalbos").visibility)
        val demoWords = catalog.wordsForLesson("shared-demo-01")
        assertEquals(listOf("word_000001", "word_000002", "word_000003", "word_000004"), demoWords.map { it.id })
        assertTrue(catalog.wordsForLesson("sekmes-01").any { it.id == "word_000001" })
        val sekmesWords = catalog.wordsForCourse(SEKMES_COURSE_ID)
        assertEquals(1039, sekmesWords.size)
        assertTrue(sekmesWords.map { it.id }.distinct().size == 1039)
        assertTrue(sekmesWords.none { it.id.startsWith("constitution_") })
        assertTrue(catalog.entries.all { it.partOfSpeech in PartOfSpeech.entries })
        assertEquals(11, catalog.lessonsForCourse("constitution").size)
        assertEquals(100, catalog.lessonsForCourse("constitution")
            .flatMap { catalog.wordsForLesson(it.id) }
            .map { it.id }
            .distinct()
            .size)
    }

    @Test
    fun constitutionAssetHasStructuredPreambleListsAndDictionaryLinks() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val catalog = AssetCatalogLoader(context.assets).load()
        val constitution = ConstitutionAssetLoader(context.assets).load(catalog)

        assertEquals(11, constitution.blocks.size)
        assertEquals(10, constitution.document.preamble.parts.size)
        assertEquals(154, constitution.document.articles.size)
        assertEquals(20, constitution.content("article-67").parts.size)
        assertEquals(24, constitution.content("article-84").parts.size)
        assertTrue(constitution.document.articles
            .flatMap { it.parts }
            .flatMap { it.termLinks }
            .all { catalog.wordById.containsKey(it.wordId) })
    }
}
