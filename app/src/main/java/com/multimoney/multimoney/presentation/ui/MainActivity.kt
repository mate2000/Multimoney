package com.multimoney.multimoney.presentation.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import com.multimoney.multimoney.presentation.navigation.navgraph.Navigation
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.util.LifecycleCountDownTimer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    private var lifecycleCountDownTimer: LifecycleCountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleCountDownTimer = LifecycleCountDownTimer()
        setContent {
            MultimoneyTheme {
                Navigation(lifecycleCountDownTimer)
            }
        }
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        lifecycleCountDownTimer?.restartTimer()
    }
}
