package com.example

import com.example.data.ai.OfflineKnowledgeEngine
import com.example.data.local.KnowledgeSeeder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    private val engine = OfflineKnowledgeEngine()

    @Test
    fun offlineMathSolver_evaluatesExpression() {
        val result = engine.tryAnswerOffline("Calculate (12 + 8) * 5", KnowledgeSeeder.entries)
        assertNotNull(result)
        assertTrue(result!!.text.contains("100"))
    }

    @Test
    fun offlineKnowledgeEngine_matchesGkTopic() {
        val result = engine.tryAnswerOffline("Explain Newton's laws of motion", KnowledgeSeeder.entries)
        assertNotNull(result)
        assertEquals("Science & Physics • Offline Vault", result!!.subjectLabel)
    }

    @Test
    fun offlineKnowledgeEngine_identifiesCreator() {
        val result = engine.tryAnswerOffline("Who made you?", KnowledgeSeeder.entries)
        assertNotNull(result)
        assertTrue(result!!.text.contains("Prakhar Sumant"))
    }
}
