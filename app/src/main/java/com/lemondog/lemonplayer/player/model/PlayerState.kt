package com.lemondog.lemonplayer.player.model

import androidx.media3.common.MediaItem

data class PlayerState(
    val isPlaying: Boolean = false,
    val currentPlayingItem: MediaItem? = null,
    val playlist: List<MediaItem> = emptyList(),
    val isPlayerLoaded: Boolean = false,
) {
    fun isPlaylistLoaded(): Boolean = this.playlist.isNotEmpty()
}
