package com.lemondog.lemonplayer.data.repository

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.lemondog.lemonplayer.data.model.AudioItem
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MusicRepository @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    // TODO Move these into DBs
    private val LOCAL_MUSIC_PATH_PREFIX = "android.resource://${context.packageName}/raw/"
    private val LOCAL_COVER_ART_PATH_PREFIX = "android.resource://${context.packageName}/drawable/"
    private val musicList = listOf(
        AudioItem(
            "despacito",
            "Despacito"
        ),
        AudioItem(
            "jaroflove",
            "Jar Of Love"
        ),
        AudioItem(
            "whereisthelove",
            "Where is the love"
        ),
        AudioItem(
            "mowenguiqi",
            "莫问归期"
        ),
        AudioItem(
            "pianai",
            "偏爱"
        ),
        AudioItem(
            "hongsegaogenxie",
            "红色高跟鞋"
        ),
        AudioItem(
            "laorenyuhai",
            "老人与海"
        )
    )


    fun getLocalMusics(): List<MediaItem> {
        return musicList.map {
            MediaItem.Builder().setUri(
                "$LOCAL_MUSIC_PATH_PREFIX${it.fileName}"
            ).setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(it.title)
                    .setArtworkUri(
                        Uri.parse(
                            "$LOCAL_COVER_ART_PATH_PREFIX" + "music"
                        )
                    )
                    .build()
            ).build()
        }
    }
}
