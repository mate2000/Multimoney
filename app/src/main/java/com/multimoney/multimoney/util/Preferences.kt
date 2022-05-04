package com.multimoney.multimoney.util

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import com.multimoney.data.base.BasePreference
import com.multimoney.data.util.GsonHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class Preferences @Inject constructor(
    @ApplicationContext context: Context,
    gsonHelper: GsonHelper
) : BasePreference(context = context, preferenceKey = STORE_NAME, gsonHelper = gsonHelper) {

    suspend fun setCiphertextWrapper(ciphertextWrapper: String) =
        putValue(CIPHERTEXT_WRAPPER_KEY, ciphertextWrapper)

    fun getCiphertextWrapper(): Flow<String> = getValue(CIPHERTEXT_WRAPPER_KEY, "")

    suspend fun removeCiphertextWrapper() = removeValue(CIPHERTEXT_WRAPPER_KEY)

    companion object {
        private const val STORE_NAME = "multimoney_preferences"
        private val CIPHERTEXT_WRAPPER_KEY = stringPreferencesKey("ciphertext_wrapper_key")
    }
}
