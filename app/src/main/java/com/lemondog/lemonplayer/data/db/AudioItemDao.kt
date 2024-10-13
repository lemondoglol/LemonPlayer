package com.lemondog.lemonplayer.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AudioItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun put(audioItem: AudioItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun putAll(audioItems: List<AudioItemEntity>)

    @Query("Select * From $AUDIO_ITEM_TABLE_NAME")
    suspend fun getAllAudioItems(): List<AudioItemEntity>?
}