package com.lemondog.lemonplayer.player

import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
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
) : ViewModel(), Player.Listener {
    internal var mediaController by mutableStateOf<MediaController?>(null)
        private set

    private lateinit var controllerFuture: ListenableFuture<MediaController>

    internal var appBarUIState by mutableStateOf(AppBarState.DEFAULT_STATE)
        private set

    internal var playerState by mutableStateOf(PlayerState())
        private set

    // Note: this is frequently being updated
    internal var playerItemState by mutableStateOf(PlayerItemState())
        private set

    private var positionUpdateJob: Job? = null

    init {
        initMediaController()
        viewModelScope.launch(Dispatchers.IO) {
            musicRepository.getLocalMusicsFlow().collectLatest {
                playerState = playerState.copy(
                    playlist = it,
                )
            }
        }
    }

    private fun initMediaController() {
        val sessionToken = SessionToken(
            context,
            ComponentName(
                context,
                PlayerService::class.java,
            )
        )

        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener({
            mediaController = controllerFuture.get()
            mediaController?.addListener(this@Media3PlayerViewModel)
        }, MoreExecutors.directExecutor())
    }

    internal fun playPause() {
        loadPlaylist()
        if (mediaController?.isPlaying == true) {
            mediaController?.pause()
            playerState = playerState.copy(isPlaying = false)
        } else if (playerState.isPlayerLoaded) {
            mediaController?.prepare()
            mediaController?.play()
            playerState = playerState.copy(
                isPlaying = true,
                currentPlayingItem = mediaController?.currentMediaItem,
            )
        }
    }

    internal fun playNext() {
        mediaController?.seekToNext()
        playerState = playerState.copy(
            currentPlayingItem = mediaController?.currentMediaItem,
        )
    }

    internal fun playPrevious() {
        mediaController?.seekToPrevious()
        playerState = playerState.copy(
            currentPlayingItem = mediaController?.currentMediaItem,
        )
    }

    internal fun shufflePlayList() {
        appBarUIState = appBarUIState.copy(
            isShuffleModelOn = when (appBarUIState.isShuffleModelOn) {
                true -> false
                false -> true
            },
        )
        mediaController?.shuffleModeEnabled = appBarUIState.isShuffleModelOn
    }

    private fun loadPlaylist(
        forceRefresh: Boolean = false,
    ) {
        if (!playerState.isPlayerLoaded && playerState.playlist.isNotEmpty()) {
            Log.d("Lemondog", "current playlist size: ${playerState.playlist.size}")
            mediaController?.setMediaItems(playerState.playlist)
            mediaController?.repeatMode = MediaController.REPEAT_MODE_ALL
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
                mediaController?.duration?.let { duration ->
                    playerItemState = playerItemState.copy(
                        title = mediaController?.mediaMetadata?.title.toString(),
                        duration = duration,
                    )
                }
                // Update PositionUpdate Job
                positionUpdateJob?.cancel()
                positionUpdateJob = viewModelScope.launch {
                    while (true) {
                        mediaController?.currentPosition?.let { currentPosition ->
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
        MediaController.releaseFuture(controllerFuture)
        positionUpdateJob?.cancel()
    }

    companion object {
        private const val positionUpdateInterval: Long = 1000 // 1 second
    }
}