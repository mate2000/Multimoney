package com.multimoney.multimoney.presentation.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.multimoney.presentation.navigation.navgraph.Navigation
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.util.CognitoHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var mmCountDownTimer: MMCountDownTimer

    @Inject
    lateinit var dataStorePreferences: DataStorePreferences

    @Inject
    lateinit var cognitoHelper: CognitoHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MultimoneyTheme {
                Navigation()
            }
        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        mmCountDownTimer.restartTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        signOut()
    }

    private fun signOut() {
        cognitoHelper.signOut(signOutError = {
            Timber.d("SignOut Error")
        })
        lifecycleScope.launch {
            dataStorePreferences.setAuthToken("")
        }
        mmCountDownTimer.discardTimer()
    }
}
