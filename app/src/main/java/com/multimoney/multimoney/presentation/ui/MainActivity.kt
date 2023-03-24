package com.multimoney.multimoney.presentation.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import com.google.firebase.messaging.FirebaseMessaging
import com.multimoney.data.util.DataStorePreferences
import com.multimoney.multimoney.presentation.navigation.navgraph.Navigation
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.ui.home.HomeViewModel
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.util.MMCountDownTimer
import com.multimoney.multimoney.presentation.util.SignOutCommunicator
import com.multimoney.multimoney.presentation.util.catalog.DialogParameters
import com.multimoney.multimoney.presentation.util.getDeviceId
import com.multimoney.multimoney.util.CognitoHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), SignOutCommunicator {

    @Inject
    lateinit var mmCountDownTimer: MMCountDownTimer

    @Inject
    lateinit var dataStorePreferences: DataStorePreferences

    @Inject
    lateinit var cognitoHelper: CognitoHelper

    var dialogParameters = mutableStateOf(DialogParameters())

    var isSessionAlreadyOpened: Flow<Boolean> = flowOf(false)

    private var isAppInForeground = true

    private var activity: AppCompatActivity? = null

    private var homeViewModel: HomeViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        isSessionAlreadyOpened = dataStorePreferences.isSessionDuplicated()
        setContent {
            MultimoneyTheme {
                Navigation {
                    homeViewModel = it
                }

                LaunchedEffect(key1 = true) {
                    obtainNotificationRoute(intent?.getStringExtra(ROUTE_KEY) ?: "")
                    if (dataStorePreferences.getDeviceId().first().isEmpty()) {
                        val deviceId: String = getDeviceId(activity as MainActivity)
                        if (deviceId.isEmpty()) {
                            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                                saveToken(it.result)
                            }.addOnCanceledListener {
                                saveToken(getDeviceId(activity = activity as MainActivity))
                            }.addOnFailureListener {
                                saveToken(getDeviceId(activity = activity as MainActivity))
                            }
                        } else {
                            saveToken(deviceId)
                        }
                    }
                    isSessionAlreadyOpened.collectLatest {
                        if (it) {
                            signOut()
                        }
                    }
                }

                if (dialogParameters.value.isActive.value) {
                    CustomDialog(
                        title = stringResource(id = dialogParameters.value.titleResource),
                        message = stringResource(
                            id = dialogParameters.value.descriptionResource,
                            dialogParameters.value.additionalText
                        ).ifEmpty { dialogParameters.value.description },
                        positiveButtonText = stringResource(id = dialogParameters.value.positiveResource),
                        openDialogCustom = dialogParameters.value.isActive,
                        onPositiveAction = dialogParameters.value.positiveAction,
                        isCancelable = dialogParameters.value.isCancelable
                    )
                }
            }
        }
    }

    private fun saveToken(token: String) {
        lifecycleScope.launch {
            dataStorePreferences.setDeviceID(token)
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

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private suspend fun obtainNotificationRoute(route: String) =
        dataStorePreferences.setNavigationRouteByNotification(route)

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            homeViewModel?.setNotificationRoute(intent?.getStringExtra(ROUTE_KEY) ?: "")
        }
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
            dataStorePreferences.isSessionDuplicated(false)
            mmCountDownTimer.discardTimer()
        }
    }

    override fun onMaxTimeUsedDialogChangeState(dialogParameters: DialogParameters) {
        this.dialogParameters.value = dialogParameters
    }

    override fun isAppInForeground(): Boolean {
        return isAppInForeground
    }

    override fun isSessionDuplicated() = isSessionAlreadyOpened

    companion object {
        private const val ROUTE_KEY = "routeName"
    }
}
