package com.lemondog.lemonplayer.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.lemondog.lemonplayer.data.datastore.LemonPlayerPreferencesRepository
import androidx.datastore.preferences.core.edit
import com.lemondog.lemonplayer.di.DispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LemonPlayerPreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val dispatcher: DispatcherProvider,
) : LemonPlayerPreferencesRepository {

    override suspend fun <T> setValue(key: Preferences.Key<T>, value: T) {
        withContext(dispatcher.io()) {
            dataStore.edit { preferences ->
                preferences[key] = value
            }
        }
    }


    override suspend fun <T> getValue(key: Preferences.Key<T>, defaultValue: T): T {
        return withContext(dispatcher.io()) {
            val preferences = dataStore.data.map { it[key] ?: defaultValue }
            preferences.first()
        }
    }

    override fun <T> getValueAsFlow(key: Preferences.Key<T>, defaultValue: T): Flow<T> {
        return dataStore.data
            .map { preferences ->
                preferences[key] ?: defaultValue
            }
    }
}