package com.lemondog.lemonplayer.player

import android.content.ComponentName
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.lemondog.lemonplayer.data.repository.MusicRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class Media3PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val media3PlayerListener: PlayerListener,
    private val musicRepository: MusicRepository,
) : ViewModel() {

    // TODO, check how to expose this, use a StateFlow to determine if it's ready to be used
    lateinit var mediaController: MediaController

    private lateinit var controllerFuture: ListenableFuture<MediaController>

    internal var playerReadyToUse = mutableStateOf(false)
        private set

    private var isPlayListLoaded = false

    internal var shuffleModeEnabled = mutableStateOf(false)
        private set

    // Note: this method needs to be manually called in Fragment
    internal fun initMediaController() {
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
            playerReadyToUse.value = true
            mediaController.addListener(media3PlayerListener)
        }, MoreExecutors.directExecutor())
    }

    internal fun play() {
        loadPlaylist()
        mediaController.shuffleModeEnabled = false
        if (mediaController.isPlaying) {
            mediaController.pause()
        } else {
            mediaController.prepare()
            mediaController.play()
        }
    }

    internal fun shufflePlayList() {
        shuffleModeEnabled.value = !shuffleModeEnabled.value
        mediaController.shuffleModeEnabled = shuffleModeEnabled.value
    }

    private fun loadPlaylist(
        forceRefresh: Boolean = false,
    ) {
        if (!isPlayListLoaded) {
            val playlist = musicRepository.getLocalMusics()
            mediaController.setMediaItems(playlist)
            isPlayListLoaded = true
            mediaController.repeatMode = MediaController.REPEAT_MODE_ALL
        }
    }

    override fun onCleared() {
        super.onCleared()
        MediaController.releaseFuture(controllerFuture)
    }
}