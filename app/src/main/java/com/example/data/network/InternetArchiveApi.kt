package com.example.data.network

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonReader
import com.squareup.moshi.ToJson
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

@JvmInline
value class FlexibleText(val value: String)

class FlexibleTextAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): FlexibleText {
        return when (reader.peek()) {
            JsonReader.Token.BEGIN_ARRAY -> {
                val items = mutableListOf<String>()
                reader.beginArray()
                while (reader.hasNext()) {
                    if (reader.peek() == JsonReader.Token.STRING) {
                        items.add(reader.nextString())
                    } else {
                        reader.skipValue()
                    }
                }
                reader.endArray()
                FlexibleText(items.joinToString(", "))
            }
            JsonReader.Token.STRING -> FlexibleText(reader.nextString())
            JsonReader.Token.NUMBER -> FlexibleText(reader.nextString())
            JsonReader.Token.NULL -> {
                reader.nextNull<Unit>()
                FlexibleText("")
            }
            else -> {
                reader.skipValue()
                FlexibleText("")
            }
        }
    }

    @ToJson
    fun toJson(value: FlexibleText): String = value.value
}

@JsonClass(generateAdapter = true)
data class ArchiveSearchEnvelope(
    val response: ArchiveSearchResponse? = null
)

@JsonClass(generateAdapter = true)
data class ArchiveSearchResponse(
    val numFound: Int = 0,
    val docs: List<ArchiveDoc> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ArchiveDoc(
    val identifier: String = "",
    val title: FlexibleText = FlexibleText(""),
    val creator: FlexibleText = FlexibleText("Public Domain Contributor"),
    val mediatype: FlexibleText = FlexibleText(""),
    val downloads: Int = 0
)

@JsonClass(generateAdapter = true)
data class ArchiveMetadataResponse(
    val files: List<ArchiveFileEntry> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ArchiveFileEntry(
    val name: String = "",
    val format: String? = null,
    val size: String? = null,
    @Json(name = "source") val source: String? = null
)

interface InternetArchiveApi {
    @GET("advancedsearch.php")
    suspend fun searchMedia(
        @Query("q") query: String,
        @Query("fl[]") fields: List<String> = listOf("identifier", "title", "creator", "mediatype", "downloads"),
        @Query("sort[]") sort: String = "downloads desc",
        @Query("rows") rows: Int = 6,
        @Query("page") page: Int = 1,
        @Query("output") output: String = "json"
    ): ArchiveSearchEnvelope

    @GET("metadata/{identifier}")
    suspend fun getItemMetadata(
        @Path("identifier") identifier: String
    ): ArchiveMetadataResponse
}
