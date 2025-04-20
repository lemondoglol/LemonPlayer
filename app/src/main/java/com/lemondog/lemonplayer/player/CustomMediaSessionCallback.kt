package com.lemondog.lemonplayer.player

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSession.ConnectionResult
import androidx.media3.session.MediaSession.ConnectionResult.AcceptedResultBuilder
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionResult
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class CustomMediaSessionCallback @Inject constructor(
    @ApplicationContext private val context: Context,
) : MediaSession.Callback {
    /**
     * To define custom commands, override onConnect to set the available custom commands
     * */
    @OptIn(UnstableApi::class)
    override fun onConnect(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
    ): MediaSession.ConnectionResult {
        val sessionCommands = ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
            .add(SessionCommand(SAVE_TO_FAVORITES, Bundle.EMPTY))
            .build()

        return AcceptedResultBuilder(session)
            .setAvailableSessionCommands(sessionCommands)
            .build()
    }

    /**
     * To receive custom command requests from a MediaController
     * */
    override fun onCustomCommand(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        customCommand: SessionCommand,
        args: Bundle,
    ): ListenableFuture<SessionResult> {
        return if (customCommand.customAction == SAVE_TO_FAVORITES) {
            Toast.makeText(context, "Saved to favorites", Toast.LENGTH_SHORT).show()
            Futures.immediateFuture(
                SessionResult(SessionResult.RESULT_SUCCESS)
            )
        } else {
            super.onCustomCommand(session, controller, customCommand, args)
        }
    }

    @OptIn(UnstableApi::class)
    override fun onPlaybackResumption(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo
    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
        return super.onPlaybackResumption(mediaSession, controller)
    }

    companion object {
        const val SAVE_TO_FAVORITES = "SAVE_TO_FAVORITES"
    }
}