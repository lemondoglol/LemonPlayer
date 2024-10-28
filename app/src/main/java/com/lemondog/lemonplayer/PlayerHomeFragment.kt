package com.lemondog.lemonplayer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.ui.PlayerView
import com.lemondog.lemonplayer.player.Media3PlayerViewModel
import com.lemondog.lemonplayer.player.view.PlayerControllerButton
import com.lemondog.lemonplayer.ui.dimens.Padding
import dagger.hilt.android.AndroidEntryPoint

/**
 * TODO Google how to create custom media3 player
 * */
@AndroidEntryPoint
class PlayerHomeFragment : Fragment() {

    private val viewModel: Media3PlayerViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                Scaffold(
                    content = {
                        PlaybackSection(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it),
                        )
                    }
                )
            }
        }
    }

    @Composable
    private fun PlaybackSection(
        modifier: Modifier = Modifier,
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            PlayerView(requireContext()).also {
                it.player = viewModel.mediaController
            }
            PlayerControllerSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Padding.Medium)
            )

            PlayListView(
                modifier = Modifier.fillMaxSize(),
                playlistItems = viewModel.playerState.playlist,
                currentPlayingItem = viewModel.playerState.currentPlayingItem,
            )
        }
    }

    @Composable
    private fun PlayerControllerSection(
        modifier: Modifier = Modifier,
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // TODO add progress bar, don't add to the state, create a new one CurrentPlayingItemState
            // Previous, Play/Pause, Next
            PlayerControllerButton(
                onClick = viewModel::playPrevious,
                icon = Icons.Filled.SkipPrevious,
            )

            val playPauseIcon = if (viewModel.playerState.isPlaying) {
                Icons.Filled.PauseCircle
            } else {
                Icons.Filled.PlayCircle
            }
            PlayerControllerButton(
                onClick = viewModel::playPause,
                icon = playPauseIcon,
            )

            PlayerControllerButton(
                onClick = viewModel::playNext,
                icon = Icons.Filled.SkipNext,
            )
        }
    }

    @Composable
    fun PlayListView(
        modifier: Modifier = Modifier,
        playlistItems: List<MediaItem> = emptyList(),
        currentPlayingItem: MediaItem? = null,
    ) {
        LazyColumn(
            modifier = modifier,
        ) {
            items(playlistItems) {
                // TODO Update View Here
                if (currentPlayingItem?.mediaMetadata?.title == it.mediaMetadata.title) {
                    Text(text = "Current Playing Item: ${it.mediaMetadata.title}")
                } else {
                    Text(text = "${it.mediaMetadata.title}")
                }
            }
        }
    }

    // Default Player View
    @Composable
    private fun PlayerScreen(
        modifier: Modifier = Modifier,
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            viewModel.mediaController?.let {
                AndroidView(
                    factory = { context ->
                        PlayerView(context).also {
                            it.player = viewModel.mediaController
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f),
                )
            }

            Button(onClick = { viewModel.playPause() }) {
                Text(text = "Play")
            }

            Button(onClick = { viewModel.shufflePlayList() }) {
                if (viewModel.shuffleModeEnabled.value) {
                    Text(text = "Shuffle Mode On")
                } else {
                    Text(text = "Shuffle Mode Off")
                }
            }
        }
    }
}
