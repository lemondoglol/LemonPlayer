package com.lemondog.lemonplayer.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        AudioItemEntity::class
    ],
    version = 1
)
abstract class AppRoomDatabase : RoomDatabase() {
    abstract fun audioItemDao(): AudioItemDao
}
