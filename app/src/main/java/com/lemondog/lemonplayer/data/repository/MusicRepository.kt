package com.lemondog.lemonplayer.data.repository

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.lemondog.lemonplayer.data.db.AudioItemDao
import com.lemondog.lemonplayer.data.db.AudioItemEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MusicRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val audioItemDao: AudioItemDao,
) {
    private val LOCAL_MUSIC_PATH_PREFIX = "android.resource://${context.packageName}/raw/"
    private val DEFAULT_LOCAL_COVER_ART_PATH_PREFIX = "android.resource://${context.packageName}/drawable/music"
    init {
        val musicItems = listOf(
            AudioItemEntity(
                fileUri = "${LOCAL_MUSIC_PATH_PREFIX}despacito",
                title = "Despacito",
                coverArtUri = DEFAULT_LOCAL_COVER_ART_PATH_PREFIX,
            ),
            AudioItemEntity(
                fileUri = "${LOCAL_MUSIC_PATH_PREFIX}jaroflove",
                title = "Jar of Love",
                coverArtUri = DEFAULT_LOCAL_COVER_ART_PATH_PREFIX,
            ),
            AudioItemEntity(
                fileUri = "${LOCAL_MUSIC_PATH_PREFIX}whereisthelove",
                title = "Where is the love",
                coverArtUri = DEFAULT_LOCAL_COVER_ART_PATH_PREFIX,
            ),
            AudioItemEntity(
                fileUri = "${LOCAL_MUSIC_PATH_PREFIX}mowenguiqi",
                title = "莫问归期",
                coverArtUri = DEFAULT_LOCAL_COVER_ART_PATH_PREFIX,
            ),
            AudioItemEntity(
                fileUri = "${LOCAL_MUSIC_PATH_PREFIX}pianai",
                title = "偏爱",
                coverArtUri = DEFAULT_LOCAL_COVER_ART_PATH_PREFIX,
            ),
            AudioItemEntity(
                fileUri = "${LOCAL_MUSIC_PATH_PREFIX}hongsegaogenxie",
                title = "红色高跟鞋",
                coverArtUri = DEFAULT_LOCAL_COVER_ART_PATH_PREFIX,
            ),
            AudioItemEntity(
                fileUri = "${LOCAL_MUSIC_PATH_PREFIX}laorenyuhai",
                title = "老人与海",
                coverArtUri = DEFAULT_LOCAL_COVER_ART_PATH_PREFIX,
            ),
        )
        CoroutineScope(Dispatchers.IO).launch {
            audioItemDao.putAll(musicItems)
        }
    }

    suspend fun getLocalMusics(): List<MediaItem>? {
        return audioItemDao.getAllAudioItems()?.map { audioItem ->
            MediaItem.Builder().setUri(
                audioItem.fileUri
            ).setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(audioItem.title)
                    .setArtist(audioItem.artist)
                    .setArtworkUri(
                        Uri.parse(audioItem.coverArtUri)
                    )
                    .build()
            ).build()
        }
    }
}