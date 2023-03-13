package com.multimoney.multimoney.presentation.ui.qrcodescanner

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.common.util.concurrent.ListenableFuture
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.ui.qrcodescanner.QrCodeScannerViewModel.Companion.SCHEME_PACKAGE
import com.multimoney.multimoney.presentation.uielement.CustomDialog
import com.multimoney.multimoney.presentation.uielement.TopNavBar
import timber.log.Timber
import java.util.concurrent.ExecutorService
import com.multimoney.multimoney.presentation.util.checkPermission
import java.util.concurrent.Executors

@Composable
fun QrCodeScannerScreen(
    viewModel: QrCodeScannerViewModel = hiltViewModel(),
    onPopBackStack: (String) -> Unit = {},
) {
    val context = LocalContext.current

    val launcherContactPermissionDialog = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        viewModel.onUIEvent(QrCodeScannerViewModel.UIEvent.OnPermissionResult)
    }
    val permissionFlow: (fromRationale: Boolean) -> Unit = {
        context.checkPermission(
            permission = Manifest.permission.CAMERA,
            permissionGrantedAction = {},
            showRationaleAction = { isPermanentlyDenied ->
                if (isPermanentlyDenied) {
                    viewModel.onUIEvent(QrCodeScannerViewModel.UIEvent.OnShowEnablePermissionsInSettingsDialog)
                } else {
                    viewModel.onUIEvent(QrCodeScannerViewModel.UIEvent.OnCameraPermissionMissed)
                }
            },
            launchFromRationale = { isLastRetry ->
                viewModel.setIfIsLastPermissionRetry(isLastRetry)
            },
            comesFromRationale = it,
            isFirstRequest = viewModel.isCameraPermissionAlreadyRequested.not(),
            launcher = launcherContactPermissionDialog
        )
    }

    LaunchedEffect(true) {
        permissionFlow(false)
    }

    QrCodeScannerContent(context, onPopBackStack)

    if (viewModel.uiState.permissionDialog.isActive.value) {
        CustomDialog(
            title = stringResource(id = viewModel.uiState.permissionDialog.titleResource),
            message = viewModel.uiState.permissionDialog.description.ifBlank {
                stringResource(viewModel.uiState.permissionDialog.descriptionResource)
            },
            positiveButtonText = stringResource(id = viewModel.uiState.permissionDialog.positiveResource),
            negativeButtonText = stringResource(id = viewModel.uiState.permissionDialog.negativeResource),
            openDialogCustom = viewModel.uiState.permissionDialog.isActive,
            onPositiveAction = viewModel.uiState.permissionDialog.positiveAction,
            onNegativeAction = { onPopBackStack("") },
            isCancelable = viewModel.uiState.permissionDialog.isCancelable,
        )
    }

    if (viewModel.uiState.requestCameraPermission.value) {
        permissionFlow(viewModel.showRationale)
    }

    if (viewModel.uiState.openPermissionInSettings.value) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts(SCHEME_PACKAGE, context.applicationContext.packageName, null)
        }
        context.startActivity(intent)
        viewModel.onUIEvent(QrCodeScannerViewModel.UIEvent.OnPermissionInSettingsOpened)
        onPopBackStack("")
    }
}

@Composable
fun QrCodeScannerContent(context: Context, onPopBackStack: (String) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var preview by remember { mutableStateOf<Preview?>(null) }
    val barCodeVal = remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopNavBar(
                isLeftButtonVisible = true,
                isRightButtonVisible = false,
                onLeftButtonClick = { onPopBackStack("") },
            )
            AndroidView(
                factory = { AndroidViewContext ->
                    PreviewView(AndroidViewContext).apply {
                        this.scaleType = PreviewView.ScaleType.FILL_CENTER
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                        )
                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                    }
                },
                modifier = Modifier
                    .fillMaxSize(),
                update = { previewView ->
                    val cameraSelector: CameraSelector = CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build()
                    val cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
                    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> =
                        ProcessCameraProvider.getInstance(context)

                    cameraProviderFuture.addListener({
                        preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
                        val barcodeAnalyser = QrCodeAnalyser { barcodes ->
                            barcodes.first().rawValue?.let { barcodeValue ->
                                barCodeVal.value = barcodeValue
                                onPopBackStack(barcodeValue)
                            }
                        }
                        val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()
                            .also {
                                it.setAnalyzer(cameraExecutor, barcodeAnalyser)
                            }

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        } catch (e: Exception) {
                            Timber.d("TAG", "CameraPreview: ${e.localizedMessage}")
                        }
                    }, ContextCompat.getMainExecutor(context))
                }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.3f)
                .background(Color.Black.copy(alpha = 0.3f))
                .align(Alignment.BottomEnd)
        ) {
            Text(
                text = stringResource(id = R.string.common_scan_qr_code),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(24.dp),
                style = Typography.body1.copy(fontWeight = FontWeight.SemiBold),
                color = MultimoneyTheme.colors.text
            )
        }
    }
}
