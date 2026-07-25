package com.example.sekmeszodynas

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ConstitutionTextAnnotationTest {
    @Test
    fun termsRemainClickableAndFractionsReceiveAnAccentSpan() {
        val text = "Tauta turi 1/5 balsų"
        val annotated = constitutionAnnotatedText(
            text = text,
            numericFragments = listOf(NumericFragment(11, 14, NumericFragmentType.FRACTION)),
            termLinks = listOf(ConstitutionTermLink("word_tauta", 0, 5)),
        )

        assertEquals("word_tauta", annotated.getStringAnnotations("word", 1, 1).single().item)
        assertTrue(annotated.spanStyles.any { range -> range.start == 11 && range.end == 14 })
        assertTrue(annotated.spanStyles.any { range -> range.start == 0 && range.end == 5 })
    }
}
