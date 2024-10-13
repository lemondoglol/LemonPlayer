package com.lemondog.lemonplayer.di

import android.content.Context
import androidx.room.Room
import com.lemondog.lemonplayer.data.db.AppRoomDatabase
import com.lemondog.lemonplayer.data.db.AudioItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {
    @Provides
    @Singleton
    fun provideMemoryAppDatabase(context: Context): AppRoomDatabase =
        Room.databaseBuilder(
            context,
            AppRoomDatabase::class.java,
            "Lemon Player"
        ).build()

    @Provides
    @Singleton
    internal fun provideAudioItemDao(
        appRoomDatabase: AppRoomDatabase,
    ): AudioItemDao = appRoomDatabase.audioItemDao()
}
