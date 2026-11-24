package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NaterDao {
    @Query("SELECT * FROM conversations ORDER BY updatedAt DESC")
    fun observeConversations(): Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :id LIMIT 1")
    suspend fun getConversation(id: Long): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConversation(conversation: ConversationEntity): Long

    @Query("UPDATE conversations SET title = :title, subjectTag = :subjectTag, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateConversationMeta(id: Long, title: String, subjectTag: String, updatedAt: Long)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversation(id: Long)

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC, id ASC")
    fun observeMessages(conversationId: Long): Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY timestamp ASC, id ASC")
    suspend fun getMessagesOnce(conversationId: Long): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("SELECT * FROM knowledge_entries ORDER BY subject ASC, question ASC")
    fun observeAllKnowledge(): Flow<List<KnowledgeEntryEntity>>

    @Query("SELECT * FROM knowledge_entries")
    suspend fun getAllKnowledgeOnce(): List<KnowledgeEntryEntity>

    @Query("SELECT COUNT(*) FROM knowledge_entries")
    suspend fun getKnowledgeCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKnowledgeEntries(entries: List<KnowledgeEntryEntity>)

    @Query("SELECT * FROM cached_media ORDER BY cachedAt DESC")
    fun observeCachedMedia(): Flow<List<CachedMediaEntity>>

    @Query("SELECT * FROM cached_media WHERE title LIKE '%' || :query || '%' OR creator LIKE '%' || :query || '%' OR queryTag LIKE '%' || :query || '%' ORDER BY cachedAt DESC LIMIT 12")
    suspend fun searchCachedMedia(query: String): List<CachedMediaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCachedMedia(items: List<CachedMediaEntity>)
}
