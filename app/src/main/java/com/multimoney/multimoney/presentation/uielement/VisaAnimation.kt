package com.multimoney.multimoney.presentation.uielement

import android.app.Dialog
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.extension.findActivity
import com.visa.SensoryBrandingView

@Composable
fun VisaAnimation(
    onFinishAnimationAction: () -> Unit
) {
    val backDropColor: String = if (isSystemInDarkTheme()) {
        "#FF080808"
    } else {
        "#FF080808"
    }
    AndroidView(
        factory = { context ->
            val sensoryBrandingViewContainer = LayoutInflater.from(context).inflate(R.layout.sensory_branding_view, null, false)
            val activity = context.findActivity()
            activity?.let {
                val animation = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
                val sensoryBrandingComponent: SensoryBrandingView =
                    sensoryBrandingViewContainer.findViewById(R.id.sensory_branding_view_component) as SensoryBrandingView
                sensoryBrandingComponent.isCheckMarkShown = true
                sensoryBrandingComponent.backdropColor = Color.parseColor(backDropColor)
                sensoryBrandingComponent.isSoundEnabled = true
                sensoryBrandingComponent.isHapticFeedbackEnabled = true
                animation.show()

                Handler(Looper.getMainLooper()).postDelayed({
                    try {
                        sensoryBrandingComponent.animate {
                            animation.cancel()
                            onFinishAnimationAction()
                        }
                    } catch (ew: Exception) {
                        animation.cancel()
                        onFinishAnimationAction()
                    }
                }, 100)
            }
            sensoryBrandingViewContainer // return the view
        },
        update = { _ ->
        }
    )
}
