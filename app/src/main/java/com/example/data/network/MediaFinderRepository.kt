package com.example.data.network

import com.example.data.local.CachedMediaEntity
import com.example.data.local.NaterDao
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

enum class MediaCategory(val label: String, val archiveFilter: String) {
    ALL("All Media", "(mediatype:audio OR mediatype:movies)"),
    MUSIC("Songs & Audio", "mediatype:audio"),
    MOVIE("Movies & Films", "mediatype:movies")
}

@JsonClass(generateAdapter = true)
data class MediaDownloadItem(
    val identifier: String,
    val title: String,
    val creator: String,
    val mediaType: String,
    val directUrl: String,
    val format: String,
    val sizeLabel: String,
    val sourcePageUrl: String,
    val thumbnailUrl: String,
    val isFromCache: Boolean = false
)

sealed interface MediaSearchResult {
    data class Success(val items: List<MediaDownloadItem>, val fromCache: Boolean) : MediaSearchResult
    data class Error(val message: String) : MediaSearchResult
}

class MediaFinderRepository(private val dao: NaterDao) {

    private val moshi: Moshi = Moshi.Builder()
        .add(FlexibleTextAdapter())
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val api: InternetArchiveApi = Retrofit.Builder()
        .baseUrl("https://archive.org/")
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
        .create(InternetArchiveApi::class.java)

    private val listAdapter = moshi.adapter<List<MediaDownloadItem>>(
        Types.newParameterizedType(List::class.java, MediaDownloadItem::class.java)
    )

    val cachedMediaFlow: Flow<List<CachedMediaEntity>> = dao.observeCachedMedia()

    fun serializeMediaList(items: List<MediaDownloadItem>): String? {
        if (items.isEmpty()) return null
        return listAdapter.toJson(items)
    }

    fun deserializeMediaList(json: String?): List<MediaDownloadItem> {
        if (json.isNullOrBlank()) return emptyList()
        return try {
            listAdapter.fromJson(json).orEmpty()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun searchFreeMedia(
        rawQuery: String,
        category: MediaCategory,
        isOnline: Boolean
    ): MediaSearchResult = withContext(Dispatchers.IO) {
        val cleanQuery = rawQuery.trim()
        if (cleanQuery.isBlank()) {
            return@withContext MediaSearchResult.Error("Enter a song, artist, or movie title to search.")
        }

        if (!isOnline) {
            val cached = dao.searchCachedMedia(cleanQuery).map { it.toDomain(isFromCache = true) }
            return@withContext if (cached.isNotEmpty()) {
                MediaSearchResult.Success(cached, fromCache = true)
            } else {
                MediaSearchResult.Error("You are offline and no cached media links matched \"$cleanQuery\".")
            }
        }

        try {
            val sanitizedTerms = cleanQuery
                .replace("\"", "")
                .replace(":", " ")
                .trim()
            val fullQuery = "($sanitizedTerms) AND ${category.archiveFilter} AND -collection:restricted"
            val searchEnvelope = api.searchMedia(query = fullQuery, rows = 6)
            val docs = searchEnvelope.response?.docs.orEmpty()
                .filter { it.identifier.isNotBlank() }
                .take(5)

            if (docs.isEmpty()) {
                val cached = dao.searchCachedMedia(cleanQuery).map { it.toDomain(isFromCache = true) }
                return@withContext if (cached.isNotEmpty()) {
                    MediaSearchResult.Success(cached, fromCache = true)
                } else {
                    MediaSearchResult.Success(emptyList(), fromCache = false)
                }
            }

            val resolvedItems = coroutineScope {
                docs.map { doc ->
                    async {
                        resolveDirectDownload(doc)
                    }
                }.awaitAll().filterNotNull()
            }

            if (resolvedItems.isNotEmpty()) {
                dao.upsertCachedMedia(
                    resolvedItems.map { item ->
                        CachedMediaEntity(
                            identifier = item.identifier,
                            title = item.title,
                            creator = item.creator,
                            mediaType = item.mediaType,
                            directUrl = item.directUrl,
                            format = item.format,
                            sizeLabel = item.sizeLabel,
                            sourcePageUrl = item.sourcePageUrl,
                            thumbnailUrl = item.thumbnailUrl,
                            queryTag = cleanQuery.lowercase()
                        )
                    }
                )
            }

            MediaSearchResult.Success(resolvedItems, fromCache = false)
        } catch (e: Exception) {
            val cached = dao.searchCachedMedia(cleanQuery).map { it.toDomain(isFromCache = true) }
            if (cached.isNotEmpty()) {
                MediaSearchResult.Success(cached, fromCache = true)
            } else {
                MediaSearchResult.Error(e.localizedMessage ?: "Unable to reach Internet Archive right now.")
            }
        }
    }

    private suspend fun resolveDirectDownload(doc: ArchiveDoc): MediaDownloadItem? {
        return try {
            val metadata = api.getItemMetadata(doc.identifier)
            val isMovie = doc.mediatype.value.equals("movies", ignoreCase = true)
            val preferredExtensions = if (isMovie) {
                listOf(".mp4", ".ogv", ".mkv", ".avi", ".webm")
            } else {
                listOf(".mp3", ".ogg", ".flac", ".m4a", ".wav")
            }

            val bestFile = preferredExtensions.firstNotNullOfOrNull { ext ->
                metadata.files.firstOrNull { file ->
                    file.name.endsWith(ext, ignoreCase = true) && !file.name.contains("_512kb", ignoreCase = true)
                } ?: metadata.files.firstOrNull { file ->
                    file.name.endsWith(ext, ignoreCase = true)
                }
            } ?: return null

            val encodedFile = URLEncoder.encode(bestFile.name, "UTF-8").replace("+", "%20")
            val directUrl = "https://archive.org/download/${doc.identifier}/$encodedFile"
            val extLabel = bestFile.name.substringAfterLast('.', "").uppercase().ifBlank {
                if (isMovie) "VIDEO" else "AUDIO"
            }

            MediaDownloadItem(
                identifier = doc.identifier,
                title = doc.title.value.ifBlank { doc.identifier },
                creator = doc.creator.value.ifBlank { "Public Domain / Open Archive" },
                mediaType = if (isMovie) "movies" else "audio",
                directUrl = directUrl,
                format = extLabel,
                sizeLabel = formatBytes(bestFile.size?.toLongOrNull()),
                sourcePageUrl = "https://archive.org/details/${doc.identifier}",
                thumbnailUrl = "https://archive.org/services/img/${doc.identifier}"
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun CachedMediaEntity.toDomain(isFromCache: Boolean) = MediaDownloadItem(
        identifier = identifier,
        title = title,
        creator = creator,
        mediaType = mediaType,
        directUrl = directUrl,
        format = format,
        sizeLabel = sizeLabel,
        sourcePageUrl = sourcePageUrl,
        thumbnailUrl = thumbnailUrl,
        isFromCache = isFromCache
    )

    private fun formatBytes(bytes: Long?): String {
        if (bytes == null || bytes <= 0L) return "Direct file"
        val mb = bytes / (1024.0 * 1024.0)
        return if (mb >= 1.0) {
            String.format("%.1f MB", mb)
        } else {
            val kb = bytes / 1024.0
            String.format("%.0f KB", kb)
        }
    }
}
