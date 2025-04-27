package com.lemondog.lemonplayer.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.lemondog.lemonplayer.data.datastore.LemonPlayerPreferencesRepository
import com.lemondog.lemonplayer.data.repository.LemonPlayerPreferencesRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    fun providePreferencesDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create {
            context.dataStoreFile("com.lemondog.lemonplayer.preferences_pb")
        }

    @Provides
    @Singleton
    fun provideLemonPlayerPreferencesRepository(
        lemonPlayerPreferencesRepository: LemonPlayerPreferencesRepositoryImpl
    ): LemonPlayerPreferencesRepository = lemonPlayerPreferencesRepository
}
