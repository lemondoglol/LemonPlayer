package com.lemondog.lemonplayer.data.datastore

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface LemonPlayerPreferencesRepository {

    /**
     * Saves a value to the DataStore with the specified key.
     *
     * @param key The Preferences.Key associated with the value.
     * @param value The value to be stored.
     */
    suspend fun <T> setValue(key: Preferences.Key<T>, value: T)

    /**
     * Retrieves a value from the DataStore associated with the specified key.
     *
     * @param key The Preferences.Key associated with the desired value.
     * @param defaultValue The default value to return if the key is not present.
     * @return The retrieved value or the default value if the key is absent.
     */
    suspend fun <T> getValue(key: Preferences.Key<T>, defaultValue: T): T

    /**
     * Observes changes to the value associated with the specified key in the DataStore.
     *
     * @param key The Preferences.Key to observe.
     * @param defaultValue The default value to emit if the key is not present.
     * @return A Flow emitting the current and future values associated with the key.
     */
    fun <T> getValueAsFlow(key: Preferences.Key<T>, defaultValue: T): Flow<T>
}
