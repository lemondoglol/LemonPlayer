package com.lemondog.lemonplayer.player

import android.util.Log
import androidx.media3.common.Player
import javax.inject.Inject

class PlayerListener @Inject constructor(

) : Player.Listener {

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        when (playbackState) {
            Player.STATE_BUFFERING -> {
                Log.d("Lemondog", "STATE_BUFFERING")
            }
            Player.STATE_ENDED -> {
                Log.d("Lemondog", "STATE_ENDED")
            }
            Player.STATE_IDLE -> {
                Log.d("Lemondog", "STATE_IDLE")
            }
            Player.STATE_READY -> {
                Log.d("Lemondog", "STATE_READY")
            }
        }
    }
}