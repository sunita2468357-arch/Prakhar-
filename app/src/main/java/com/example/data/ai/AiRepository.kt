package com.example.data.ai

import com.example.data.local.MessageEntity
import com.example.data.network.MediaDownloadItem

data class AiReplyResult(
    val text: String,
    val sourceLabel: String,
    val isSampleNotice: Boolean,
    val mediaItems: List<MediaDownloadItem> = emptyList()
)

interface AiRepository {
    suspend fun generateReply(
        query: String,
        history: List<MessageEntity>,
        isOnline: Boolean
    ): AiReplyResult
}
