package com.lemondog.lemonplayer.appbar.model

data class AppBarState(
    val isShuffleModelOn: Boolean,
) {
    companion object {
        val DEFAULT_STATE = AppBarState(
            isShuffleModelOn = true,
        )
    }
}
