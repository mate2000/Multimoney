package com.multimoney.multimoney.presentation.ui

import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import com.facebook.hermes.reactexecutor.HermesExecutorFactory
import com.facebook.react.BuildConfig
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactPackage
import com.facebook.react.ReactRootView
import com.facebook.react.common.LifecycleState
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.react.shell.MainReactPackage

class ReactActivity : AppCompatActivity(), DefaultHardwareBackBtnHandler {

    private var reactRootView: ReactRootView? = null
    private var reactInstanceManager: ReactInstanceManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val packages: List<ReactPackage> = arrayListOf(
                MainReactPackage(), PackageTrackerReact()
        )

        reactRootView = ReactRootView(this)
        reactRootView?.setBackgroundColor(Color.BLACK)
        reactInstanceManager = ReactInstanceManager.builder()
                .setApplication(application)
                .setCurrentActivity(this)
                .setBundleAssetName(BUNDLE_ASSET_NAME)
                .setJSMainModulePath(JS_MAIN_MODULE_PATH)
                .addPackages(packages)
                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .setJavaScriptExecutorFactory(HermesExecutorFactory())
                .build()

        val props = Bundle()
        //FTT APPLICATION NAME
        props.putString(APPLICATION_NAME, intent.extras?.getString(APPLICATION_NAME) ?: "")
        //FTT USERNAME
        props.putString(USER_NAME, intent.extras?.getString(USER_NAME) ?: "")
        //FTT USER PASSWORD
        props.putString(USER_PASS, intent.extras?.getString(USER_PASS) ?: "")
        //FTT ENDPOINT
        props.putString(ENDPOINT, intent.extras?.getString(ENDPOINT))
        reactRootView?.startReactApplication(reactInstanceManager, CARD, props)
        setContentView(reactRootView)

        onHandleBackPressed()
    }

    override fun invokeDefaultOnBackPressed() {
        onHandleBackPressed()
    }

    fun onHandleBackPressed() {
        OnBackPressedDispatcher().addCallback(this, object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (reactInstanceManager != null) {
                    reactInstanceManager?.onBackPressed()
                } else {
                    onBackPressed()
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        if (reactInstanceManager != null) {
            reactInstanceManager?.onHostPause(this)
        }
    }

    override fun onResume() {
        super.onResume()
        if (reactInstanceManager != null) {
            reactInstanceManager?.onHostResume(this, this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (reactInstanceManager != null) {
            reactInstanceManager?.onHostDestroy(this)
        }
        if (reactRootView != null) {
            reactRootView?.unmountReactApplication()
        }
    }

    override fun onBackPressed() {
        if (reactInstanceManager != null) {
            reactInstanceManager?.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU && reactInstanceManager != null) {
            reactInstanceManager?.showDevOptionsDialog()
            return true
        }
        return super.onKeyUp(keyCode, event)
    }

    companion object {
        const val BUNDLE_ASSET_NAME = "index.android.bundle"
        const val JS_MAIN_MODULE_PATH = "index"
        const val APPLICATION_NAME = "applicationName"
        const val USER_NAME = "userName"
        const val USER_PASS = "userPassword"
        const val ENDPOINT = "endpoint"
        const val CARD = "card"
    }
}