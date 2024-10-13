package com.lemondog.lemonplayer.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = AUDIO_ITEM_TABLE_NAME)
data class AudioItemEntity(
    @PrimaryKey val fileUri: String,
    val title: String = Unknown,
    val artist: String = Unknown,
    val coverArtUri: String = Unknown,
)

const val AUDIO_ITEM_TABLE_NAME = "audio_item_table"
const val Unknown = "Unknown"