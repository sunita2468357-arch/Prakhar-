package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiRepository
import com.example.data.ai.PlaceholderAiRepository
import com.example.data.local.AppDatabase
import com.example.data.local.ConversationEntity
import com.example.data.local.KnowledgeEntryEntity
import com.example.data.local.MessageEntity
import com.example.data.local.PreferencesRepository
import com.example.data.local.UserPreferences
import com.example.data.network.ConnectivityObserver
import com.example.data.network.MediaCategory
import com.example.data.network.MediaDownloadItem
import com.example.data.network.MediaFinderRepository
import com.example.data.network.MediaSearchResult
import com.example.ui.theme.FontScaleLevel
import com.example.ui.theme.ThemeMode
import com.example.voice.VoiceAssistantManager
import com.example.voice.VoiceUiState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatMessageUi(
    val id: Long,
    val role: String,
    val content: String,
    val sourceLabel: String,
    val isSampleNotice: Boolean,
    val mediaItems: List<MediaDownloadItem>,
    val timestamp: Long
)

sealed interface MediaFinderUiState {
    data object Idle : MediaFinderUiState
    data object Loading : MediaFinderUiState
    data class Success(val items: List<MediaDownloadItem>, val fromCache: Boolean) : MediaFinderUiState
    data class Error(val message: String) : MediaFinderUiState
}

@OptIn(ExperimentalCoroutinesApi::class)
class NaterViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val dao = database.naterDao()
    private val preferencesRepository = PreferencesRepository(application)
    private val connectivityObserver = ConnectivityObserver(application)
    private val mediaFinderRepository = MediaFinderRepository(dao)
    private val aiRepository: AiRepository = PlaceholderAiRepository(dao, mediaFinderRepository)

    private val voiceManager = VoiceAssistantManager(
        context = application,
        onFinalTranscript = { transcript ->
            _inputText.value = transcript
            sendMessage(promptOverride = transcript, fromVoice = true)
        }
    )

    val preferences: StateFlow<UserPreferences> = preferencesRepository.preferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    val isDeviceOnline: StateFlow<Boolean> = connectivityObserver.isOnline

    val effectiveOnline: StateFlow<Boolean> = combine(isDeviceOnline, preferences) { online, prefs ->
        online && !prefs.preferOfflineMode
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val voiceState: StateFlow<VoiceUiState> = voiceManager.state

    val conversations: StateFlow<List<ConversationEntity>> = dao.observeConversations()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _activeConversationId = MutableStateFlow<Long?>(null)
    val activeConversationId: StateFlow<Long?> = _activeConversationId.asStateFlow()

    val messages: StateFlow<List<ChatMessageUi>> = _activeConversationId
        .flatMapLatest { convId ->
            if (convId == null) {
                flowOf(emptyList())
            } else {
                dao.observeMessages(convId).map { list ->
                    list.map { entity ->
                        ChatMessageUi(
                            id = entity.id,
                            role = entity.role,
                            content = entity.content,
                            sourceLabel = entity.sourceLabel,
                            isSampleNotice = entity.isSampleNotice,
                            mediaItems = mediaFinderRepository.deserializeMediaList(entity.mediaJson),
                            timestamp = entity.timestamp
                        )
                    }
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _showFirebaseBanner = MutableStateFlow(true)
    val showFirebaseBanner: StateFlow<Boolean> = _showFirebaseBanner.asStateFlow()

    // Offline Knowledge Library
    private val _knowledgeSearch = MutableStateFlow("")
    val knowledgeSearch: StateFlow<String> = _knowledgeSearch.asStateFlow()

    private val _selectedSubject = MutableStateFlow("All")
    val selectedSubject: StateFlow<String> = _selectedSubject.asStateFlow()

    val allKnowledgeEntries: StateFlow<List<KnowledgeEntryEntity>> = dao.observeAllKnowledge()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredKnowledgeEntries: StateFlow<List<KnowledgeEntryEntity>> = combine(
        allKnowledgeEntries,
        _knowledgeSearch,
        _selectedSubject
    ) { entries, query, subject ->
        val q = query.trim().lowercase()
        entries.filter { entry ->
            val matchesSubject = subject == "All" || entry.subject == subject
            val matchesQuery = q.isBlank() ||
                entry.question.lowercase().contains(q) ||
                entry.subject.lowercase().contains(q) ||
                entry.keywords.lowercase().contains(q) ||
                entry.answer.lowercase().contains(q)
            matchesSubject && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Media Finder state
    private val _mediaSearchQuery = MutableStateFlow("")
    val mediaSearchQuery: StateFlow<String> = _mediaSearchQuery.asStateFlow()

    private val _mediaCategory = MutableStateFlow(MediaCategory.ALL)
    val mediaCategory: StateFlow<MediaCategory> = _mediaCategory.asStateFlow()

    private val _mediaFinderState = MutableStateFlow<MediaFinderUiState>(MediaFinderUiState.Idle)
    val mediaFinderState: StateFlow<MediaFinderUiState> = _mediaFinderState.asStateFlow()

    val cachedMediaItems: StateFlow<List<MediaDownloadItem>> = mediaFinderRepository.cachedMediaFlow
        .map { entities ->
            entities.map { entity ->
                MediaDownloadItem(
                    identifier = entity.identifier,
                    title = entity.title,
                    creator = entity.creator,
                    mediaType = entity.mediaType,
                    directUrl = entity.directUrl,
                    format = entity.format,
                    sizeLabel = entity.sizeLabel,
                    sourcePageUrl = entity.sourcePageUrl,
                    thumbnailUrl = entity.thumbnailUrl,
                    isFromCache = true
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    fun dismissFirebaseBanner() {
        _showFirebaseBanner.value = false
    }

    fun startNewChat() {
        voiceManager.stopSpeaking()
        _activeConversationId.value = null
        _inputText.value = ""
    }

    fun selectConversation(conversationId: Long) {
        voiceManager.stopSpeaking()
        _activeConversationId.value = conversationId
    }

    fun deleteConversation(conversationId: Long) {
        viewModelScope.launch {
            dao.deleteConversation(conversationId)
            if (_activeConversationId.value == conversationId) {
                _activeConversationId.value = null
            }
        }
    }

    fun sendMessage(promptOverride: String? = null, fromVoice: Boolean = false) {
        val text = (promptOverride ?: _inputText.value).trim()
        if (text.isBlank() || _isGenerating.value) return

        _inputText.value = ""
        _isGenerating.value = true

        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val convId = _activeConversationId.value ?: run {
                    val title = if (text.length > 42) text.take(42) + "…" else text
                    val newId = dao.insertConversation(
                        ConversationEntity(
                            title = title,
                            createdAt = now,
                            updatedAt = now
                        )
                    )
                    _activeConversationId.value = newId
                    newId
                }

                dao.insertMessage(
                    MessageEntity(
                        conversationId = convId,
                        role = "user",
                        content = text,
                        timestamp = now
                    )
                )

                val history = dao.getMessagesOnce(convId)
                val online = effectiveOnline.value
                val reply = aiRepository.generateReply(text, history, online)

                dao.insertMessage(
                    MessageEntity(
                        conversationId = convId,
                        role = "assistant",
                        content = reply.text,
                        sourceLabel = reply.sourceLabel,
                        isSampleNotice = reply.isSampleNotice,
                        mediaJson = mediaFinderRepository.serializeMediaList(reply.mediaItems),
                        timestamp = System.currentTimeMillis()
                    )
                )

                val existingConv = dao.getConversation(convId)
                val updatedTitle = existingConv?.title ?: text.take(42)
                dao.updateConversationMeta(
                    id = convId,
                    title = updatedTitle,
                    subjectTag = reply.sourceLabel.ifBlank { "General" },
                    updatedAt = System.currentTimeMillis()
                )

                if (fromVoice || preferences.value.autoSpeakResponses) {
                    voiceManager.speak(reply.text)
                }
            } finally {
                _isGenerating.value = false
            }
        }
    }

    fun openKnowledgeTopicInChat(entry: KnowledgeEntryEntity) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val title = if (entry.question.length > 42) entry.question.take(42) + "…" else entry.question
            val convId = dao.insertConversation(
                ConversationEntity(
                    title = title,
                    subjectTag = entry.subject,
                    createdAt = now,
                    updatedAt = now
                )
            )
            _activeConversationId.value = convId
            dao.insertMessage(
                MessageEntity(
                    conversationId = convId,
                    role = "user",
                    content = entry.question,
                    timestamp = now
                )
            )
            dao.insertMessage(
                MessageEntity(
                    conversationId = convId,
                    role = "assistant",
                    content = entry.answer,
                    sourceLabel = "${entry.subject} • Offline Vault",
                    isSampleNotice = false,
                    timestamp = now + 1
                )
            )
        }
    }

    fun updateKnowledgeSearch(query: String) {
        _knowledgeSearch.value = query
    }

    fun selectKnowledgeSubject(subject: String) {
        _selectedSubject.value = subject
    }

    fun updateMediaSearchQuery(query: String) {
        _mediaSearchQuery.value = query
    }

    fun selectMediaCategory(category: MediaCategory) {
        _mediaCategory.value = category
    }

    fun searchMedia(queryOverride: String? = null, categoryOverride: MediaCategory? = null) {
        val q = (queryOverride ?: _mediaSearchQuery.value).trim()
        val cat = categoryOverride ?: _mediaCategory.value
        if (queryOverride != null) _mediaSearchQuery.value = queryOverride
        if (categoryOverride != null) _mediaCategory.value = categoryOverride
        if (q.isBlank()) {
            _mediaFinderState.value = MediaFinderUiState.Error("Enter a song, artist, or classic film title.")
            return
        }

        _mediaFinderState.value = MediaFinderUiState.Loading
        viewModelScope.launch {
            when (val result = mediaFinderRepository.searchFreeMedia(q, cat, effectiveOnline.value)) {
                is MediaSearchResult.Success -> {
                    _mediaFinderState.value = MediaFinderUiState.Success(result.items, result.fromCache)
                }
                is MediaSearchResult.Error -> {
                    _mediaFinderState.value = MediaFinderUiState.Error(result.message)
                }
            }
        }
    }

    fun startVoiceListening() {
        voiceManager.startListening()
    }

    fun stopVoiceListening() {
        voiceManager.stopListening()
    }

    fun clearVoiceError() {
        voiceManager.clearError()
    }

    fun toggleSpeakMessage(text: String) {
        if (voiceState.value.isSpeaking) {
            voiceManager.stopSpeaking()
        } else {
            voiceManager.speak(text)
        }
    }

    fun stopSpeaking() {
        voiceManager.stopSpeaking()
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch { preferencesRepository.setThemeMode(mode) }
    }

    fun setFontScaleLevel(level: FontScaleLevel) {
        viewModelScope.launch { preferencesRepository.setFontScaleLevel(level) }
    }

    fun setAutoSpeak(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setAutoSpeakResponses(enabled) }
    }

    fun setPreferOfflineMode(enabled: Boolean) {
        viewModelScope.launch { preferencesRepository.setPreferOfflineMode(enabled) }
    }

    override fun onCleared() {
        super.onCleared()
        voiceManager.release()
        connectivityObserver.release()
    }
}
