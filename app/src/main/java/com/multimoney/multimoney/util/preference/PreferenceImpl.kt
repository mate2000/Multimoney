package com.multimoney.multimoney.util.preference

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val STORE_NAME = "multimoney_preferences"

class PreferenceImpl @Inject constructor(@ApplicationContext private val context: Context) :
    Preference {
    private val Context.dataStore by preferencesDataStore(STORE_NAME)
}
