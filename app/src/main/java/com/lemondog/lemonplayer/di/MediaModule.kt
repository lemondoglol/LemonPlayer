package com.lemondog.lemonplayer.di

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.session.MediaSession
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MediaModule {

    @OptIn(UnstableApi::class)
    @Singleton
    @Provides
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer {
        // auto adjust video quality based on network bandwidth
        val trackSelector = DefaultTrackSelector(context).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }
        return ExoPlayer.Builder(context)
            .setTrackSelector(trackSelector)
            .build()
    }

    @Provides
    @Singleton
    fun provideMediaSession(
        @ApplicationContext context: Context,
        player: ExoPlayer,
    ): MediaSession = MediaSession.Builder(context, player).build()
}