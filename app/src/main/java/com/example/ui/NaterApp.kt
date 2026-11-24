package com.example.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.R
import com.example.ui.components.ConnectivityBadge
import com.example.ui.components.NaterFooter
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.KnowledgeLibraryScreen
import com.example.ui.screens.MediaFinderScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.NaterTheme
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.launch

const val ROUTE_CHAT = "chat"
const val ROUTE_KNOWLEDGE = "knowledge"
const val ROUTE_MEDIA = "media"
const val ROUTE_SETTINGS = "settings"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NaterApp(viewModel: NaterViewModel) {
    val context = LocalContext.current
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val effectiveOnline by viewModel.effectiveOnline.collectAsStateWithLifecycle()
    val conversations by viewModel.conversations.collectAsStateWithLifecycle()
    val activeConversationId by viewModel.activeConversationId.collectAsStateWithLifecycle()
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val inputText by viewModel.inputText.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val showFirebaseBanner by viewModel.showFirebaseBanner.collectAsStateWithLifecycle()
    val voiceState by viewModel.voiceState.collectAsStateWithLifecycle()

    val knowledgeEntries by viewModel.filteredKnowledgeEntries.collectAsStateWithLifecycle()
    val allKnowledgeEntries by viewModel.allKnowledgeEntries.collectAsStateWithLifecycle()
    val knowledgeSearch by viewModel.knowledgeSearch.collectAsStateWithLifecycle()
    val selectedSubject by viewModel.selectedSubject.collectAsStateWithLifecycle()

    val mediaQuery by viewModel.mediaSearchQuery.collectAsStateWithLifecycle()
    val mediaCategory by viewModel.mediaCategory.collectAsStateWithLifecycle()
    val mediaState by viewModel.mediaFinderState.collectAsStateWithLifecycle()
    val cachedMedia by viewModel.cachedMediaItems.collectAsStateWithLifecycle()

    val allSubjects = remember(allKnowledgeEntries) {
        listOf("All") + allKnowledgeEntries.map { it.subject }.distinct()
    }

    var showMicPermissionDialog by remember { mutableStateOf(false) }

    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.startVoiceListening()
        } else {
            showMicPermissionDialog = true
        }
    }

    val requestVoiceInput: () -> Unit = {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            viewModel.startVoiceListening()
        } else {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: ROUTE_CHAT
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navigateTo: (String) -> Unit = { route ->
        scope.launch { drawerState.close() }
        if (currentRoute != route) {
            navController.navigate(route) {
                popUpTo(ROUTE_CHAT) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    NaterTheme(
        themeMode = preferences.themeMode,
        fontScaleLevel = preferences.fontScaleLevel
    ) {
        if (showMicPermissionDialog) {
            AlertDialog(
                onDismissRequest = { showMicPermissionDialog = false },
                title = { Text("Microphone Permission") },
                text = { Text(stringResource(R.string.voice_permission_rationale)) },
                confirmButton = {
                    TextButton(onClick = { showMicPermissionDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.surface
                ) {
                    Column(modifier = Modifier.fillMaxHeight()) {
                        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)) {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Minimalist AI & Offline Knowledge",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        NavigationDrawerItem(
                            label = { Text(stringResource(R.string.nav_chat)) },
                            selected = currentRoute == ROUTE_CHAT,
                            onClick = { navigateTo(ROUTE_CHAT) },
                            icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null) },
                            modifier = Modifier
                                .padding(NavigationDrawerItemDefaults.ItemPadding)
                                .testTag("drawer_nav_chat")
                        )
                        NavigationDrawerItem(
                            label = { Text(stringResource(R.string.nav_knowledge)) },
                            selected = currentRoute == ROUTE_KNOWLEDGE,
                            onClick = { navigateTo(ROUTE_KNOWLEDGE) },
                            icon = { Icon(Icons.Default.School, contentDescription = null) },
                            modifier = Modifier
                                .padding(NavigationDrawerItemDefaults.ItemPadding)
                                .testTag("drawer_nav_knowledge")
                        )
                        NavigationDrawerItem(
                            label = { Text(stringResource(R.string.nav_media)) },
                            selected = currentRoute == ROUTE_MEDIA,
                            onClick = { navigateTo(ROUTE_MEDIA) },
                            icon = { Icon(Icons.Default.Download, contentDescription = null) },
                            modifier = Modifier
                                .padding(NavigationDrawerItemDefaults.ItemPadding)
                                .testTag("drawer_nav_media")
                        )
                        NavigationDrawerItem(
                            label = { Text(stringResource(R.string.nav_settings)) },
                            selected = currentRoute == ROUTE_SETTINGS,
                            onClick = { navigateTo(ROUTE_SETTINGS) },
                            icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            modifier = Modifier
                                .padding(NavigationDrawerItemDefaults.ItemPadding)
                                .testTag("drawer_nav_settings")
                        )

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp, horizontal = 16.dp))

                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Recent Chats",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(
                                onClick = {
                                    viewModel.startNewChat()
                                    navigateTo(ROUTE_CHAT)
                                },
                                modifier = Modifier.testTag("drawer_new_chat_button")
                            ) {
                                Text("+ New")
                            }
                        }

                        LazyColumn(modifier = Modifier.weight(1f)) {
                            if (conversations.isEmpty()) {
                                item {
                                    Text(
                                        text = "No saved conversations yet.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                                    )
                                }
                            } else {
                                items(conversations, key = { it.id }) { conv ->
                                    NavigationDrawerItem(
                                        label = {
                                            Text(
                                                text = conv.title,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        },
                                        selected = currentRoute == ROUTE_CHAT && activeConversationId == conv.id,
                                        onClick = {
                                            viewModel.selectConversation(conv.id)
                                            navigateTo(ROUTE_CHAT)
                                        },
                                        badge = {
                                            IconButton(
                                                onClick = { viewModel.deleteConversation(conv.id) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.DeleteOutline,
                                                    contentDescription = "Delete chat",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        },
                                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                        NaterFooter(modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        ) {
            Scaffold(
                contentWindowInsets = WindowInsets.safeDrawing,
                containerColor = MaterialTheme.colorScheme.background,
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            titleContentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        navigationIcon = {
                            IconButton(
                                onClick = { scope.launch { drawerState.open() } },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("open_drawer_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Open navigation menu"
                                )
                            }
                        },
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val titleText = when (currentRoute) {
                                    ROUTE_KNOWLEDGE -> stringResource(R.string.nav_knowledge)
                                    ROUTE_MEDIA -> stringResource(R.string.nav_media)
                                    ROUTE_SETTINGS -> stringResource(R.string.nav_settings)
                                    else -> stringResource(R.string.app_name)
                                }
                                Text(
                                    text = titleText,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                ConnectivityBadge(isOnline = effectiveOnline)
                            }
                        },
                        actions = {
                            val isDark = preferences.themeMode != ThemeMode.LIGHT
                            IconButton(
                                onClick = {
                                    viewModel.setThemeMode(
                                        if (isDark) ThemeMode.LIGHT else ThemeMode.DARK
                                    )
                                },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("toggle_theme_button")
                            ) {
                                Icon(
                                    imageVector = if (isDark) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                                    contentDescription = if (isDark) "Switch to light mode" else "Switch to dark mode"
                                )
                            }

                            IconButton(
                                onClick = {
                                    val next = !preferences.autoSpeakResponses
                                    viewModel.setAutoSpeak(next)
                                    if (!next) viewModel.stopSpeaking()
                                },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("toggle_auto_speak_button")
                            ) {
                                Icon(
                                    imageVector = if (preferences.autoSpeakResponses) {
                                        Icons.AutoMirrored.Filled.VolumeUp
                                    } else {
                                        Icons.AutoMirrored.Filled.VolumeOff
                                    },
                                    contentDescription = if (preferences.autoSpeakResponses) {
                                        "Disable automatic spoken answers"
                                    } else {
                                        "Enable automatic spoken answers"
                                    },
                                    tint = if (preferences.autoSpeakResponses) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }

                            if (currentRoute == ROUTE_CHAT) {
                                IconButton(
                                    onClick = { viewModel.startNewChat() },
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("top_new_chat_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddComment,
                                        contentDescription = stringResource(R.string.new_chat)
                                    )
                                }
                            }
                        }
                    )
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = ROUTE_CHAT,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    composable(ROUTE_CHAT) {
                        ChatScreen(
                            messages = messages,
                            inputText = inputText,
                            isGenerating = isGenerating,
                            isOnline = effectiveOnline,
                            showFirebaseBanner = showFirebaseBanner,
                            voiceState = voiceState,
                            onInputChange = viewModel::updateInputText,
                            onSendClick = { viewModel.sendMessage() },
                            onQuickPromptClick = { prompt -> viewModel.sendMessage(promptOverride = prompt) },
                            onVoiceMicClick = requestVoiceInput,
                            onStopVoiceClick = viewModel::stopVoiceListening,
                            onDismissVoiceError = viewModel::clearVoiceError,
                            onSpeakToggle = viewModel::toggleSpeakMessage,
                            onDismissFirebaseBanner = viewModel::dismissFirebaseBanner
                        )
                    }

                    composable(ROUTE_KNOWLEDGE) {
                        KnowledgeLibraryScreen(
                            entries = knowledgeEntries,
                            allSubjects = allSubjects,
                            selectedSubject = selectedSubject,
                            searchQuery = knowledgeSearch,
                            onSearchChange = viewModel::updateKnowledgeSearch,
                            onSubjectSelect = viewModel::selectKnowledgeSubject,
                            onOpenInChat = { entry ->
                                viewModel.openKnowledgeTopicInChat(entry)
                                navigateTo(ROUTE_CHAT)
                            },
                            onSpeakText = viewModel::toggleSpeakMessage
                        )
                    }

                    composable(ROUTE_MEDIA) {
                        MediaFinderScreen(
                            query = mediaQuery,
                            selectedCategory = mediaCategory,
                            searchState = mediaState,
                            cachedMedia = cachedMedia,
                            isOnline = effectiveOnline,
                            onQueryChange = viewModel::updateMediaSearchQuery,
                            onCategorySelect = viewModel::selectMediaCategory,
                            onSearchSubmit = { viewModel.searchMedia() },
                            onSuggestedSearch = { q, cat -> viewModel.searchMedia(q, cat) }
                        )
                    }

                    composable(ROUTE_SETTINGS) {
                        SettingsScreen(
                            preferences = preferences,
                            onThemeModeChange = viewModel::setThemeMode,
                            onFontScaleChange = viewModel::setFontScaleLevel,
                            onAutoSpeakChange = viewModel::setAutoSpeak,
                            onPreferOfflineChange = viewModel::setPreferOfflineMode
                        )
                    }
                }
            }
        }
    }
}
