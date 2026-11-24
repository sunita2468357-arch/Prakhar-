package com.example.data.ai

import com.example.data.local.KnowledgeEntryEntity
import kotlin.math.pow

data class OfflineAnswer(
    val text: String,
    val subjectLabel: String
)

class OfflineKnowledgeEngine {

    fun tryAnswerOffline(
        query: String,
        knowledgeEntries: List<KnowledgeEntryEntity>
    ): OfflineAnswer? {
        val normalized = query.trim().lowercase()
        if (normalized.isBlank()) return null

        // 1. Conversational identity & creator queries
        if (normalized in setOf("hi", "hello", "hey", "namaste", "good morning", "good evening", "good afternoon")) {
            return OfflineAnswer(
                text = "Hello! I'm **Nater**, your conversational assistant.\n\n" +
                    "You can ask me **General Knowledge**, **Science**, **Math**, **History**, **Geography**, or **Computer Science** questions—even offline—or ask for **free public-domain song and movie download links**.",
                subjectLabel = "Nater Assistant"
            )
        }

        if (normalized.contains("who made you") ||
            normalized.contains("who created you") ||
            normalized.contains("your name") ||
            normalized.contains("about nater") ||
            normalized.contains("prakhar")
        ) {
            return OfflineAnswer(
                text = "I'm **Nater**, designed with a minimalist, voice-first experience for fast learning and accessibility—**made by Prakhar Sumant**.\n\n" +
                    "I include an offline GK & academic knowledge vault, hands-free voice recognition with spoken answers, and a legal free media finder via the Internet Archive.",
                subjectLabel = "About Nater"
            )
        }

        // 2. Local arithmetic evaluator
        evaluateMathQuery(normalized)?.let { mathAnswer ->
            return OfflineAnswer(
                text = mathAnswer,
                subjectLabel = "Mathematics • Offline Solver"
            )
        }

        // 3. Score against Room Knowledge Base
        val tokens = tokenize(normalized)
        if (tokens.isEmpty()) return null

        var bestEntry: KnowledgeEntryEntity? = null
        var bestScore = 0

        for (entry in knowledgeEntries) {
            val questionTokens = tokenize(entry.question)
            val keywordTokens = tokenize(entry.keywords)
            val subjectTokens = tokenize(entry.subject)

            var score = 0
            for (token in tokens) {
                if (keywordTokens.contains(token)) score += 4
                if (questionTokens.contains(token)) score += 3
                if (subjectTokens.contains(token)) score += 2
                if (entry.answer.lowercase().contains(token) && token.length >= 4) score += 1
            }

            if (score > bestScore) {
                bestScore = score
                bestEntry = entry
            }
        }

        if (bestEntry != null && bestScore >= 4) {
            return OfflineAnswer(
                text = "**${bestEntry.question}**\n\n${bestEntry.answer}",
                subjectLabel = "${bestEntry.subject} • Offline Vault"
            )
        }

        return null
    }

    private fun tokenize(text: String): Set<String> {
        val stopWords = setOf(
            "what", "is", "are", "the", "a", "an", "of", "in", "on", "to", "for",
            "and", "or", "how", "why", "when", "where", "who", "tell", "me", "about",
            "explain", "define", "give", "some", "with", "do", "does", "can", "you"
        )
        return text.lowercase()
            .replace(Regex("[^a-z0-9\\s]"), " ")
            .split(Regex("\\s+"))
            .filter { it.length >= 2 && it !in stopWords }
            .toSet()
    }

    private fun evaluateMathQuery(normalized: String): String? {
        val cleaned = normalized
            .replace("what is", "")
            .replace("calculate", "")
            .replace("solve", "")
            .replace("equals", "")
            .replace("plus", "+")
            .replace("minus", "-")
            .replace("times", "*")
            .replace("multiplied by", "*")
            .replace("divided by", "/")
            .replace("x", "*")
            .replace("×", "*")
            .replace("÷", "/")
            .replace("?", "")
            .trim()

        if (!cleaned.any { it in listOf('+', '-', '*', '/', '^') }) return null
        if (!cleaned.all { it.isDigit() || it in listOf('+', '-', '*', '/', '^', '(', ')', '.', ' ') }) {
            return null
        }

        return try {
            val result = ExpressionParser(cleaned).parse()
            val formatted = if (result % 1.0 == 0.0 && result in -1e12..1e12) {
                result.toLong().toString()
            } else {
                String.format("%.4f", result).trimEnd('0').trimEnd('.')
            }
            "Here is the calculation result:\n\n**`$cleaned = $formatted`**"
        } catch (_: Exception) {
            null
        }
    }

    private class ExpressionParser(private val input: String) {
        private var pos = -1
        private var ch = 0

        fun parse(): Double {
            nextChar()
            val x = parseExpression()
            if (pos < input.length) throw IllegalArgumentException("Unexpected character")
            return x
        }

        private fun nextChar() {
            ch = if (++pos < input.length) input[pos].code else -1
        }

        private fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        private fun parseExpression(): Double {
            var x = parseTerm()
            while (true) {
                when {
                    eat('+'.code) -> x += parseTerm()
                    eat('-'.code) -> x -= parseTerm()
                    else -> return x
                }
            }
        }

        private fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*'.code) -> x *= parseFactor()
                    eat('/'.code) -> {
                        val divisor = parseFactor()
                        if (divisor == 0.0) throw ArithmeticException("Division by zero")
                        x /= divisor
                    }
                    else -> return x
                }
            }
        }

        private fun parseFactor(): Double {
            if (eat('+'.code)) return parseFactor()
            if (eat('-'.code)) return -parseFactor()

            var x: Double
            val startPos = pos
            if (eat('('.code)) {
                x = parseExpression()
                if (!eat(')'.code)) throw IllegalArgumentException("Missing closing parenthesis")
            } else if ((ch in '0'.code..'9'.code) || ch == '.'.code) {
                while ((ch in '0'.code..'9'.code) || ch == '.'.code) nextChar()
                x = input.substring(startPos, pos).toDouble()
            } else {
                throw IllegalArgumentException("Unexpected symbol")
            }

            if (eat('^'.code)) x = x.pow(parseFactor())
            return x
        }
    }
}
