package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ui.theme.FontScaleLevel
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.naterDataStore: DataStore<Preferences> by preferencesDataStore(name = "nater_prefs")

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.DARK,
    val fontScaleLevel: FontScaleLevel = FontScaleLevel.STANDARD,
    val autoSpeakResponses: Boolean = false,
    val preferOfflineMode: Boolean = false
)

class PreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val FONT_SCALE = stringPreferencesKey("font_scale")
        val AUTO_SPEAK = booleanPreferencesKey("auto_speak")
        val PREFER_OFFLINE = booleanPreferencesKey("prefer_offline")
    }

    val preferences: Flow<UserPreferences> = context.naterDataStore.data.map { prefs ->
        val themeMode = prefs[Keys.THEME_MODE]?.let { raw ->
            ThemeMode.entries.firstOrNull { it.name == raw }
        } ?: ThemeMode.DARK

        val fontScale = prefs[Keys.FONT_SCALE]?.let { raw ->
            FontScaleLevel.entries.firstOrNull { it.name == raw }
        } ?: FontScaleLevel.STANDARD

        UserPreferences(
            themeMode = themeMode,
            fontScaleLevel = fontScale,
            autoSpeakResponses = prefs[Keys.AUTO_SPEAK] ?: false,
            preferOfflineMode = prefs[Keys.PREFER_OFFLINE] ?: false
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.naterDataStore.edit { it[Keys.THEME_MODE] = mode.name }
    }

    suspend fun setFontScaleLevel(level: FontScaleLevel) {
        context.naterDataStore.edit { it[Keys.FONT_SCALE] = level.name }
    }

    suspend fun setAutoSpeakResponses(enabled: Boolean) {
        context.naterDataStore.edit { it[Keys.AUTO_SPEAK] = enabled }
    }

    suspend fun setPreferOfflineMode(enabled: Boolean) {
        context.naterDataStore.edit { it[Keys.PREFER_OFFLINE] = enabled }
    }
}
