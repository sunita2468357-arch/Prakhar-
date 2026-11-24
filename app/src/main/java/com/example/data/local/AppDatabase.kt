package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        KnowledgeEntryEntity::class,
        CachedMediaEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun naterDao(): NaterDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nater_database"
                ).build()
                INSTANCE = instance
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = instance.naterDao()
                    if (dao.getKnowledgeCount() == 0) {
                        dao.insertKnowledgeEntries(KnowledgeSeeder.entries)
                    }
                }
                instance
            }
        }
    }
}
