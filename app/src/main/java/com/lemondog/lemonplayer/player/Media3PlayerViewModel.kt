package com.lemondog.lemonplayer.player

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.lemondog.lemonplayer.appbar.model.AppBarState
import com.lemondog.lemonplayer.data.repository.MusicRepository
import com.lemondog.lemonplayer.player.model.PlayerItemState
import com.lemondog.lemonplayer.player.model.PlayerItemState.Companion.DURATION_UNSET
import com.lemondog.lemonplayer.player.model.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class Media3PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val musicRepository: MusicRepository,
    private val lemonMediaController: LemonMediaController,
) : ViewModel(), Player.Listener {

    var mediaController: MediaController? = null

    internal var appBarUIState by mutableStateOf(AppBarState.DEFAULT_STATE)
        private set

    internal var playerState by mutableStateOf(PlayerState())
        private set

    // Note: this is frequently being updated
    internal var playerItemState by mutableStateOf(PlayerItemState())
        private set

    private var positionUpdateJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            lemonMediaController.initMediaController({
                mediaController = it
                mediaController?.addListener(this@Media3PlayerViewModel)
            })

            musicRepository.getLocalMusicsStateFlow().collectLatest {
                playerState = playerState.copy(
                    playlist = it,
                )
            }
        }
    }

    internal fun playPause() {
        loadPlaylist()
        if (lemonMediaController.isPlaying()) {
            lemonMediaController.pause()
            playerState = playerState.copy(isPlaying = false)
        } else if (playerState.isPlayerLoaded) {
            lemonMediaController.play()

            playerState = playerState.copy(
                isPlaying = true,
                currentPlayingItem = lemonMediaController.getCurrentlyPlayingItem(),
            )
        }
    }

    /**
     * For handling custom command
     * */
    private fun testingCustomCommand() {
        lemonMediaController.sveToFavorites()
    }

    internal fun playNext() {
        lemonMediaController.playNext()
        playerState = playerState.copy(
            currentPlayingItem = lemonMediaController.getCurrentlyPlayingItem(),
        )
    }

    internal fun playPrevious() {
        lemonMediaController.playPrevious()
        playerState = playerState.copy(
            currentPlayingItem = lemonMediaController.getCurrentlyPlayingItem(),
        )
    }

//    internal fun seekBack(timerInterval: Long = defaultSeekInterval) {
//        mediaController?.currentPosition?.let {
//            val newPosition = it - timerInterval
//            mediaController?.seekTo(newPosition.coerceAtLeast(0))
//        }
//    }

    internal fun seekForward(timerInterval: Long = defaultSeekInterval) {
        lemonMediaController.seekForward(timerInterval)
    }

    internal fun shufflePlayList() {
        viewModelScope.launch {
            val previousShuffleMode = mediaController?.shuffleModeEnabled
            val newShuffleMode = when (previousShuffleMode) {
                true -> false
                // if never set or false
                else -> true
            }
            appBarUIState = appBarUIState.copy(
                isShuffleModelOn = newShuffleMode,
            )
            lemonMediaController.setShuffleMode(newShuffleMode)
        }
    }

    private fun loadPlaylist(
        forceRefresh: Boolean = false,
    ) {
        if (!playerState.isPlayerLoaded && playerState.playlist.isNotEmpty()) {
            lemonMediaController.loadPlaylist(playerState.playlist)
            playerState = playerState.copy(
                isPlayerLoaded = true,
            )
        }
    }

    /**
     * Player.Listener Interface Begin
     */
    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        when (playbackState) {
            Player.STATE_READY -> {
                val currentItemMetadata = lemonMediaController.getCurrentlyPlayingItemMetadata()
                lemonMediaController.getDuration()?.let { duration ->
                    playerItemState = playerItemState.copy(
                        title = currentItemMetadata?.title.toString(),
                        duration = duration,
                    )
                }
                // Update PositionUpdate Job
                positionUpdateJob?.cancel()
                positionUpdateJob = viewModelScope.launch {
                    while (true) {
                        lemonMediaController.getCurrentPosition()?.let { currentPosition ->
                            if (playerItemState.duration != DURATION_UNSET) {
                                playerItemState = playerItemState.copy(
                                    progress = (currentPosition.toFloat() / playerItemState.duration.toFloat())
                                )
                            }
                            delay(positionUpdateInterval)
                        }
                    }
                }
            }
            Player.STATE_ENDED -> {
                positionUpdateJob?.cancel()
                positionUpdateJob = null
            }
            else -> Unit
        }
    }
    /**
     * Player.Listener Interface End
     */

    override fun onCleared() {
        super.onCleared()
        lemonMediaController.releaseFuture()
        positionUpdateJob?.cancel()
    }

    companion object {
        private const val positionUpdateInterval: Long = 1000 // 1 second
        private const val defaultSeekInterval: Long = 5000
    }
}