package com.multimoney.multimoney.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.multimoney.presentation.navigation.navgraph.Navigation
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.SignOutCommunicator
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.util.CognitoHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SignOutCommunicator {

    @Inject
    lateinit var mmCountDownTimer: MMCountDownTimer

    @Inject
    lateinit var dataStorePreferences: DataStorePreferences

    @Inject
    lateinit var cognitoHelper: CognitoHelper

    var dialogParameters = mutableStateOf(DialogParameters())

    private var isAppInForeground = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MultimoneyTheme {
                Navigation()
                if (dialogParameters.value.isActive.value) {
                    CustomDialog(
                        title = stringResource(id = dialogParameters.value.titleResource),
                        message = stringResource(
                            id = dialogParameters.value.descriptionResource,
                            dialogParameters.value.additionalText,
                        ).ifEmpty { dialogParameters.value.description },
                        positiveButtonText = stringResource(id = dialogParameters.value.positiveResource),
                        openDialogCustom = dialogParameters.value.isActive,
                        onPositiveAction = dialogParameters.value.positiveAction,
                        isCancelable = dialogParameters.value.isCancelable,
                    )
                }
            }
        }
        // TODO this is an example, remove and replace into the required location
        //startActivity(Intent(this, ReactActivity::class.java))
    }

    override fun onUserInteraction() {
        super.onUserInteraction()
        mmCountDownTimer.restartTimer()
    }

    override fun onDestroy() {
        super.onDestroy()
        signOut()
    }

    override fun onResume() {
        super.onResume()
        isAppInForeground = true
    }

    override fun onStop() {
        super.onStop()
        isAppInForeground = false
    }

    private fun signOut() {
        cognitoHelper.signOut(signOutError = {
            Timber.d("SignOut Error")
        })
        lifecycleScope.launch {
            dataStorePreferences.setAuthToken("")
            dataStorePreferences.setVolatileDialogVisible(true)
        }
        mmCountDownTimer.discardTimer()
    }

    override fun onMaxTimeUsedDialogChangeState(dialogParameters: DialogParameters) {
        this.dialogParameters.value = dialogParameters
    }

    override fun isAppInForeground(): Boolean {
        return isAppInForeground
    }
}
