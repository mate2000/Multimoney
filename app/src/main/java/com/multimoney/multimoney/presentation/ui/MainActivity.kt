package com.multimoney.multimoney.presentation.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.fragment.app.FragmentActivity
import com.multimoney.multimoney.presentation.navigation.navgraph.Navigation
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.util.LifecycleCountDownTimer
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var lifecycleCountDownTimer: LifecycleCountDownTimer

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
        lifecycleCountDownTimer.restartTimer()
    }
}
