package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.data.network.MediaCategory
import com.example.data.network.MediaDownloadItem
import com.example.ui.MediaFinderUiState
import com.example.ui.components.MediaDownloadCard
import com.example.ui.components.NaterFooter

private val suggestedMediaQueries = listOf(
    "Beethoven" to MediaCategory.MUSIC,
    "Jazz Piano" to MediaCategory.MUSIC,
    "Charlie Chaplin" to MediaCategory.MOVIE,
    "Space Documentary" to MediaCategory.MOVIE
)

@Composable
fun MediaFinderScreen(
    query: String,
    selectedCategory: MediaCategory,
    searchState: MediaFinderUiState,
    cachedMedia: List<MediaDownloadItem>,
    isOnline: Boolean,
    onQueryChange: (String) -> Unit,
    onCategorySelect: (MediaCategory) -> Unit,
    onSearchSubmit: () -> Unit,
    onSuggestedSearch: (String, MediaCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 760.dp)
        ) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.small,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Free public-domain & Creative Commons songs and movies via Internet Archive.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    placeholder = { Text("Search song, artist, or classic film…") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = CircleShape,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { onSearchSubmit() }),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("media_search_input")
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onSearchSubmit,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("media_search_button")
                ) {
                    Text("Find")
                }
            }

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(MediaCategory.entries) { category ->
                    FilterChip(
                        selected = category == selectedCategory,
                        onClick = { onCategorySelect(category) },
                        label = { Text(category.label) },
                        leadingIcon = {
                            when (category) {
                                MediaCategory.MUSIC -> Icon(Icons.Default.MusicNote, null, modifier = Modifier.size(16.dp))
                                MediaCategory.MOVIE -> Icon(Icons.Default.Movie, null, modifier = Modifier.size(16.dp))
                                MediaCategory.ALL -> null
                            }
                        },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                when (searchState) {
                    is MediaFinderUiState.Idle -> {
                        item {
                            Text(
                                text = "Popular Free Searches",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(suggestedMediaQueries) { (label, cat) ->
                                    AssistChip(
                                        onClick = { onSuggestedSearch(label, cat) },
                                        label = { Text(label) },
                                        modifier = Modifier.minimumInteractiveComponentSize()
                                    )
                                }
                            }
                        }

                        if (cachedMedia.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Saved Offline Links (${cachedMedia.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            items(cachedMedia, key = { "cached_${it.identifier}" }) { item ->
                                MediaDownloadCard(item = item)
                            }
                        }
                    }

                    is MediaFinderUiState.Loading -> {
                        item {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = if (isOnline) "Fetching direct download links…" else "Searching offline media cache…",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    is MediaFinderUiState.Error -> {
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                                shape = MaterialTheme.shapes.medium,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = searchState.message,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }

                    is MediaFinderUiState.Success -> {
                        if (searchState.items.isEmpty()) {
                            item {
                                Text(
                                    text = "No matching public-domain songs or movies found. Try a broader term like \"Jazz\", \"Mozart\", or \"Silent Film\".",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 24.dp)
                                )
                            }
                        } else {
                            if (searchState.fromCache) {
                                item {
                                    Text(
                                        text = "Loaded from Offline Cache",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                            items(searchState.items, key = { it.identifier }) { item ->
                                MediaDownloadCard(item = item)
                            }
                        }
                    }
                }
            }

            NaterFooter(modifier = Modifier.navigationBarsPadding())
        }
    }
}
