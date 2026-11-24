package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.ChatMessageUi
import com.example.ui.components.ChatMessageBubble
import com.example.ui.components.FirebaseRequirementBanner
import com.example.ui.components.NaterFooter
import com.example.ui.components.VoiceListeningIndicator
import com.example.voice.VoiceUiState

private data class QuickPromptItem(
    val label: String,
    val prompt: String,
    val category: String
)

private val quickPrompts = listOf(
    QuickPromptItem("Newton's Laws", "Explain Newton's three laws of motion", "gk"),
    QuickPromptItem("World Capitals & UN", "What are major world capitals and UN headquarters?", "gk"),
    QuickPromptItem("Photosynthesis", "How does photosynthesis work in plants?", "gk"),
    QuickPromptItem("Quick Math", "Calculate (45 * 12) + 180", "gk"),
    QuickPromptItem("Free Classical Song", "Send download link for classical piano song", "music"),
    QuickPromptItem("Free Classic Film", "Find free movie download link Charlie Chaplin", "movie")
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatScreen(
    messages: List<ChatMessageUi>,
    inputText: String,
    isGenerating: Boolean,
    isOnline: Boolean,
    showFirebaseBanner: Boolean,
    voiceState: VoiceUiState,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onQuickPromptClick: (String) -> Unit,
    onVoiceMicClick: () -> Unit,
    onStopVoiceClick: () -> Unit,
    onDismissVoiceError: () -> Unit,
    onSpeakToggle: (String) -> Unit,
    onDismissFirebaseBanner: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 760.dp)
                .imePadding()
        ) {
            FirebaseRequirementBanner(
                visible = showFirebaseBanner,
                onDismiss = onDismissFirebaseBanner
            )

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (messages.isEmpty()) {
                    EmptyChatWelcome(
                        isOnline = isOnline,
                        onQuickPromptClick = onQuickPromptClick,
                        onVoiceMicClick = onVoiceMicClick
                    )
                } else {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(messages, key = { it.id }) { msg ->
                            ChatMessageBubble(
                                message = msg,
                                isCurrentlySpeaking = voiceState.isSpeaking,
                                onSpeakToggle = onSpeakToggle
                            )
                        }
                        if (isGenerating) {
                            item(key = "generating_indicator") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                                ) {
                                    CircularProgressIndicator(
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(16.dp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "Nater is thinking…",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (voiceState.isListening) {
                VoiceListeningIndicator(
                    partialTranscript = voiceState.partialTranscript,
                    rmsDb = voiceState.rmsDb,
                    onStopClick = onStopVoiceClick
                )
            }

            voiceState.errorMessage?.let { err ->
                Surface(
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.14f),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = err,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = onDismissVoiceError,
                            modifier = Modifier.minimumInteractiveComponentSize()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Dismiss voice error",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            ChatComposerBar(
                inputText = inputText,
                isGenerating = isGenerating,
                isListening = voiceState.isListening,
                onInputChange = onInputChange,
                onSendClick = onSendClick,
                onMicClick = if (voiceState.isListening) onStopVoiceClick else onVoiceMicClick
            )

            NaterFooter(
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EmptyChatWelcome(
    isOnline: Boolean,
    onQuickPromptClick: (String) -> Unit,
    onVoiceMicClick: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Image(
                painter = painterResource(id = R.drawable.img_nater_icon),
                contentDescription = "Nater emblem",
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(18.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ask Nater anything",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isOnline) {
                    "GK & academic answers, hands-free voice chat, offline knowledge vault, and free public-domain music & films."
                } else {
                    "Offline Ready — Ask GK & subject questions from the local vault or view cached media links."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(max = 460.dp)
            )
            Spacer(modifier = Modifier.height(18.dp))

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                shape = CircleShape,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onVoiceMicClick)
                    .minimumInteractiveComponentSize()
                    .testTag("welcome_voice_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Tap to ask by voice",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Try asking",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                quickPrompts.forEach { item ->
                    AssistChip(
                        onClick = { onQuickPromptClick(item.prompt) },
                        label = { Text(item.label, style = MaterialTheme.typography.bodySmall) },
                        leadingIcon = {
                            val icon = when (item.category) {
                                "music" -> Icons.Default.MusicNote
                                "movie" -> Icons.Default.Movie
                                else -> Icons.Default.School
                            }
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("quick_prompt_${item.label.lowercase().replace(" ", "_")}")
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatComposerBar(
    inputText: String,
    isGenerating: Boolean,
    isListening: Boolean,
    onInputChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onMicClick: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        tonalElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.padding(start = 8.dp, end = 6.dp, top = 4.dp, bottom = 4.dp)
        ) {
            IconButton(
                onClick = onMicClick,
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = if (isListening) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    } else {
                        Color.Transparent
                    }
                ),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("voice_mic_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = if (isListening) "Stop voice input" else "Start voice input",
                    tint = if (isListening) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextField(
                value = inputText,
                onValueChange = onInputChange,
                placeholder = {
                    Text(
                        text = stringResource(R.string.chat_placeholder),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSendClick() }),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field")
            )

            FilledIconButton(
                onClick = onSendClick,
                enabled = inputText.isNotBlank() && !isGenerating,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("send_message_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send message",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
