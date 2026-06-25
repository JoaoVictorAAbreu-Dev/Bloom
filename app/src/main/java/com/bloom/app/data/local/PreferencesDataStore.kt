package com.bloom.app.data.local

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.bloomPreferencesDataStore by preferencesDataStore(name = "bloom_preferences")
