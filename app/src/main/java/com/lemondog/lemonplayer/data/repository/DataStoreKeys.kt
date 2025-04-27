package com.lemondog.lemonplayer.data.repository

import androidx.datastore.preferences.core.booleanPreferencesKey

object DataStoreKeys {
    val SHUFFLE_MODE_KEY = booleanPreferencesKey("shuffle_mode_on")
}
