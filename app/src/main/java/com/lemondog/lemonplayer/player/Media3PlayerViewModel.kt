package com.lemondog.lemonplayer.player

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class Media3PlayerViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val media3PlayerListener: PlayerListener,
) : ViewModel() {

    // TODO, check how to expose this, use a StateFlow to determine if it's ready to be used
    lateinit var mediaController: MediaController

    private lateinit var controllerFuture: ListenableFuture<MediaController>

    internal var playerReadyToUse = mutableStateOf(false)
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

    internal fun playAudioItem() {
        val mediaItem = MediaItem.Builder().setMediaId("media-1")
            .setUri(Uri.parse("https://storage.googleapis.com/exoplayer-test-media-0/play.mp3"))
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setArtist("Ryan C")
                    .setTitle("Epic Sax")
                    .build()
            ).build()
        mediaController.setMediaItem(mediaItem)
        mediaController.prepare()
        mediaController.play()
    }

    override fun onCleared() {
        super.onCleared()
        MediaController.releaseFuture(controllerFuture)
    }
}