package com.lemondog.lemonplayer.player

import android.content.Intent
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PlayerService : MediaSessionService() {

    // To enable background playback, define Player and MediaSession inside a separate service
    // MediaSession should only be defined in here
    @Inject
    lateinit var mediaSession: MediaSession

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession.apply {
            release()
            // This ensures that if the player is started again, it won't automatically start playing.
            player.playWhenReady = false
            player.stop()
        }
        super.onDestroy()
    }
}