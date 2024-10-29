package com.lemondog.lemonplayer.player

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.lemondog.lemonplayer.data.repository.MusicRepository
import com.lemondog.lemonplayer.player.model.PlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class Media3PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val media3PlayerListener: PlayerListener,
    private val musicRepository: MusicRepository,
) : ViewModel() {
    internal var mediaController by mutableStateOf<MediaController?>(null)
        private set

    private lateinit var controllerFuture: ListenableFuture<MediaController>

    internal var shuffleModeEnabled = mutableStateOf(false)
        private set

    internal var playerState by mutableStateOf(PlayerState())
        private set

    init {
        initMediaController()
        viewModelScope.launch(Dispatchers.IO) {
            musicRepository.getLocalMusics()?.let {
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
            mediaController?.addListener(media3PlayerListener)
        }, MoreExecutors.directExecutor())
    }

    internal fun playPause() {
        loadPlaylist()
        if (mediaController?.isPlaying == true) {
            mediaController?.pause()
            playerState = playerState.copy(isPlaying = false)
        } else {
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
        shuffleModeEnabled.value = !shuffleModeEnabled.value
        mediaController?.shuffleModeEnabled = shuffleModeEnabled.value
    }

    private fun loadPlaylist(
        forceRefresh: Boolean = false,
    ) {
        if (!playerState.isPlayerLoaded) {
            mediaController?.setMediaItems(playerState.playlist)
            mediaController?.repeatMode = MediaController.REPEAT_MODE_ALL
            playerState = playerState.copy(
                isPlayerLoaded = true,
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        MediaController.releaseFuture(controllerFuture)
    }
}