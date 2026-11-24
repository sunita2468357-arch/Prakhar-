package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subjectTag: String = "General",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("conversationId")]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val conversationId: Long,
    val role: String, // "user" or "assistant"
    val content: String,
    val sourceLabel: String = "",
    val isSampleNotice: Boolean = false,
    val mediaJson: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "knowledge_entries")
data class KnowledgeEntryEntity(
    @PrimaryKey val id: String,
    val subject: String,
    val question: String,
    val answer: String,
    val keywords: String,
    val summary: String
)

@Entity(tableName = "cached_media")
data class CachedMediaEntity(
    @PrimaryKey val identifier: String,
    val title: String,
    val creator: String,
    val mediaType: String, // "audio" or "movies"
    val directUrl: String,
    val format: String,
    val sizeLabel: String,
    val sourcePageUrl: String,
    val thumbnailUrl: String,
    val queryTag: String,
    val cachedAt: Long = System.currentTimeMillis()
)
