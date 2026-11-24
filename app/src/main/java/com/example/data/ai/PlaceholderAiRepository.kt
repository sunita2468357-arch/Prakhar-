package com.example.data.ai

import com.example.data.local.MessageEntity
import com.example.data.local.NaterDao
import com.example.data.network.MediaCategory
import com.example.data.network.MediaFinderRepository
import com.example.data.network.MediaSearchResult

class PlaceholderAiRepository(
    private val dao: NaterDao,
    private val mediaFinderRepository: MediaFinderRepository,
    private val offlineKnowledgeEngine: OfflineKnowledgeEngine = OfflineKnowledgeEngine()
) : AiRepository {

    override suspend fun generateReply(
        query: String,
        history: List<MessageEntity>,
        isOnline: Boolean
    ): AiReplyResult {
        val trimmed = query.trim()
        val lower = trimmed.lowercase()

        val mediaCategory = detectMediaCategory(lower)
        if (mediaCategory != null) {
            val extractedTopic = extractMediaSearchTerms(trimmed)
            if (extractedTopic.isNotBlank()) {
                return when (val result = mediaFinderRepository.searchFreeMedia(extractedTopic, mediaCategory, isOnline)) {
                    is MediaSearchResult.Success -> {
                        if (result.items.isNotEmpty()) {
                            val cacheTag = if (result.fromCache) " (from offline cache)" else ""
                            AiReplyResult(
                                text = "Here are open-license media files from the **Internet Archive** matching **\"$extractedTopic\"**$cacheTag.\n\n" +
                                    "Use **Open / Download** or **Copy Link** on any card below. Results include public-domain and Creative Commons works.",
                                sourceLabel = if (result.fromCache) "Offline Media Cache" else "Internet Archive • Open Media",
                                isSampleNotice = false,
                                mediaItems = result.items
                            )
                        } else {
                            AiReplyResult(
                                text = "No open-license audio or video files matched **\"$extractedTopic\"** on the Internet Archive.\n\n" +
                                    "Try searching for classical compositions, jazz recordings, educational documentaries, or classic silent films.",
                                sourceLabel = "Internet Archive • No Match",
                                isSampleNotice = false
                            )
                        }
                    }
                    is MediaSearchResult.Error -> {
                        AiReplyResult(
                            text = "${result.message}\n\nOpen **Free Media** from the navigation menu when connected to browse open-license audio and video links.",
                            sourceLabel = "Media Finder • Offline",
                            isSampleNotice = false
                        )
                    }
                }
            }
        }

        val entries = dao.getAllKnowledgeOnce()
        val offlineMatch = offlineKnowledgeEngine.tryAnswerOffline(trimmed, entries)
        if (offlineMatch != null) {
            return AiReplyResult(
                text = offlineMatch.text,
                sourceLabel = offlineMatch.subjectLabel,
                isSampleNotice = false
            )
        }

        val statusDetail = if (isOnline) {
            "Firebase AI Logic is not enabled"
        } else {
            "the device is offline and Firebase AI Logic is not enabled"
        }

        return AiReplyResult(
            text = "**[Sample AI Response — Firebase Required for Live Gemini]**\n\n" +
                "Your question: *\"$trimmed\"*\n\n" +
                "Because $statusDetail, Nater is showing a sample preview response instead of a live Gemini reply. Meanwhile, you can use:\n" +
                "• **Offline GK & Subjects**: Ask about Physics, Chemistry, Biology, Math, History, Geography, Economics, or Computer Science.\n" +
                "• **Offline Math Solver**: Enter expressions such as `24 * 18 + 15`.\n" +
                "• **Open Media Finder**: Search public-domain music and films with direct links via the Internet Archive.",
            sourceLabel = "Sample Preview • Needs Firebase",
            isSampleNotice = true
        )
    }

    private fun detectMediaCategory(lower: String): MediaCategory? {
        val hasMediaRequest = lower.contains("download") ||
            lower.contains("link") ||
            lower.contains("mp3") ||
            lower.contains("mp4") ||
            lower.contains("song") ||
            lower.contains("music") ||
            lower.contains("movie") ||
            lower.contains("film")

        if (!hasMediaRequest) return null

        val wantsAudio = lower.contains("song") || lower.contains("music") || lower.contains("mp3") || lower.contains("audio")
        val wantsVideo = lower.contains("movie") || lower.contains("film") || lower.contains("mp4") || lower.contains("video")

        return when {
            wantsAudio && !wantsVideo -> MediaCategory.MUSIC
            wantsVideo && !wantsAudio -> MediaCategory.MOVIE
            else -> MediaCategory.ALL
        }
    }

    private fun extractMediaSearchTerms(raw: String): String {
        val stopPhrases = listOf(
            "direct download link",
            "download link",
            "download",
            "send me",
            "give me",
            "find me",
            "find",
            "please",
            "direct",
            "link",
            "song",
            "songs",
            "music",
            "movie",
            "movies",
            "film",
            "films",
            "free",
            "mp3",
            "mp4",
            "audio",
            "video",
            "for",
            "of",
            "a",
            "an",
            "the"
        )
        var cleaned = raw.lowercase()
        for (phrase in stopPhrases) {
            cleaned = cleaned.replace(Regex("\\b${Regex.escape(phrase)}\\b"), " ")
        }
        return cleaned.replace(Regex("\\s+"), " ").trim()
    }
}
