package com.lemondog.lemonplayer.player

import android.content.Context
import androidx.media3.common.Player
import androidx.media3.session.MediaSession
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LemonPlayerMediaSessionProvider @Inject constructor(
    @ApplicationContext private val context: Context,
    player: Player,
    customMediaSessionCallback: CustomMediaSessionCallback,
) {
    val mediaSession = MediaSession.Builder(context, player).setCallback(customMediaSessionCallback).build()
}
