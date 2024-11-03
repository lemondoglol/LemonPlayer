package com.lemondog.lemonplayer.player.model

data class PlayerItemState(
    val title: String = "UnKnown",
    val duration: Long = DURATION_UNSET,
    val progress: Float = 0f,
) {
    companion object {
        const val DURATION_UNSET = -1L
    }
}
