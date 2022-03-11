package com.multimoney.multimoney.util.firebase

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings

class FirebaseRemoteConfigHelper {
    private val remoteConfig = Firebase.remoteConfig

    init {
        val settings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = FETCH_INTERVAL
        }

        remoteConfig.setConfigSettingsAsync(settings)
    }

    companion object {
        private const val FETCH_INTERVAL = 60L
    }
}