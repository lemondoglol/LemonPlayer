package com.lemondog.lemonplayer.player

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LemonMediaController @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private var mediaController: MediaController? = null

    private var controllerFuture: ListenableFuture<MediaController>? = null

    private val listeners = mutableListOf<Player.Listener>()

    /**
     * init the media controller if not init yet, meanwhile return the mediaController through
     * the callback
     * */
    suspend fun initMediaController(
        onControllerReady: (MediaController) -> Unit,
    ) = withContext(Dispatchers.IO) {
        if (controllerFuture == null) {
            val sessionToken = SessionToken(
                context,
                ComponentName(context, PlayerService::class.java)
            )
            controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        }
        controllerFuture?.addListener({
            mediaController = controllerFuture?.get()
            listeners.forEach {
                mediaController?.addListener(it)
            }
            mediaController?.let {
                onControllerReady(it)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun releaseFuture() {
        controllerFuture?.let {
            MediaController.releaseFuture(it)
        }
    }

    fun addListener(listener: Player.Listener) {
        listeners.add(listener)
        mediaController?.addListener(listener)
    }

    fun sveToFavorites() {
        val command = SessionCommand(
            SAVE_TO_FAVORITES, Bundle.EMPTY
        )
        mediaController?.sendCustomCommand(
            command, Bundle.EMPTY
        )
    }

    fun getCurrentlyPlayingItem(): MediaItem? {
        return mediaController?.currentMediaItem
    }

    fun getCurrentlyPlayingItemMetadata(): MediaMetadata? = mediaController?.mediaMetadata

    fun getDuration(): Long? = mediaController?.duration

    fun getCurrentPosition(): Long? = mediaController?.currentPosition

    fun isPlaying(): Boolean = mediaController?.isPlaying == true

    fun play() {
        mediaController?.prepare()
        mediaController?.play()
    }

    fun pause() {
        mediaController?.pause()
    }

    fun playNext() {
        mediaController?.seekToNext()
    }

    fun playPrevious() {
        mediaController?.seekToPrevious()
    }

    fun seekForward(timerInterval: Long) {
        mediaController?.currentPosition?.let {
            mediaController?.seekTo(it + timerInterval)
        }
    }

    fun shuffleMode(shuffleMode: Boolean) {
        mediaController?.shuffleModeEnabled = shuffleMode
    }

    fun loadPlaylist(playlist: List<MediaItem>) {
        mediaController?.setMediaItems(playlist)
        mediaController?.repeatMode = MediaController.REPEAT_MODE_ALL
    }

    companion object {
        private val SAVE_TO_FAVORITES = "SAVE_TO_FAVORITES"
    }
}
