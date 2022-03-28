package com.multimoney.multimoney.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.multimoney.multimoney.presentation.navigation.navgraph.Navigation
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MultimoneyTheme {
                Navigation()
            }
        }
    }
}