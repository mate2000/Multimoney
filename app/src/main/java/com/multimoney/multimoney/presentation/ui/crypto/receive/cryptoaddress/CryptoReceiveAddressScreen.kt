package com.multimoney.multimoney.presentation.ui.crypto.receive.cryptoaddress

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.multimoney.data.util.ClipboardUtil
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.LocalMultimoneyColors
import com.multimoney.multimoney.presentation.theme.MultimoneyTheme
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency70
import com.multimoney.multimoney.presentation.ui.crypto.receive.CryptoReceiveSharedViewModel
import com.multimoney.multimoney.presentation.uielement.CustomImageCard
import com.multimoney.multimoney.presentation.uielement.CustomInformativeText
import com.multimoney.multimoney.presentation.uielement.ShimmerBoxView
import com.multimoney.multimoney.presentation.uielement.ShimmerItemView
import com.multimoney.multimoney.util.QRCodeGenerator

@Composable
fun CryptoReceiveAddressScreen(
    viewModel: CryptoReceiveAddressViewModel = hiltViewModel(),
    sharedViewModel: CryptoReceiveSharedViewModel
) {
    val context = LocalContext.current
    LaunchedEffect(true) {
        viewModel.onUIEvent(
            CryptoReceiveAddressViewModel.UIEvent.OnGetInfo(
                user = sharedViewModel.email,
                idBrand = sharedViewModel.idBrand,
                identification = sharedViewModel.identification,
                market = sharedViewModel.uiState.asset ?: "",
                cryptoNetwork = sharedViewModel.uiState.cryptoNetWork ?: ""
            )
        )
        viewModel.onUIEvent(
            CryptoReceiveAddressViewModel.UIEvent.OnSetOpenMaintenanceAction(
            action = {
                sharedViewModel.onUIEvent(
                    CryptoReceiveSharedViewModel.BaseEvent.OnShowMaintenance
                )
            }
        ))
        viewModel.onUIEvent(CryptoReceiveAddressViewModel.UIEvent.OnGetCryptoReceiveAddress)
    }
    LaunchedEffect(key1 = true) {
        viewModel.onUIEvent(CryptoReceiveAddressViewModel.UIEvent.OnRegisterAdjustEnterReceiveQRScreen)
    }

    CryptoReceiveAddressContent(viewModel = viewModel, sharedViewModel = sharedViewModel, context)
}

@Composable
fun CryptoReceiveAddressContent(
    viewModel: CryptoReceiveAddressViewModel,
    sharedViewModel: CryptoReceiveSharedViewModel,
    context: Context
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MultimoneyTheme.colors.background)
            .padding(16.dp),
    ) {
        // Title
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = stringResource(
                id = R.string.crypto_receive_address_title
            ),
            style = Typography.h6.copy(
                fontWeight = FontWeight.SemiBold,
                color = LocalMultimoneyColors.current.titleText
            ),
            textAlign = TextAlign.Left
        )
        // Information
        CustomInformativeText(
            modifier = Modifier.padding(top = 8.dp),
            leadingIcon = R.drawable.ic_information,
            text = stringResource(
                id = R.string.crypto_receive_address_information,
                sharedViewModel.uiState.asset ?: ""
            ),
            textStyle = Typography.body2.copy(color = MultimoneyTheme.colors.titleText),
            alignmentVertical = Alignment.Top
        )
        Spacer(modifier = Modifier.padding(24.dp))
        // QR code of crypto receive address
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (viewModel.uiState.isLoading) {
                QrCodeSkeleton()
            } else {
                CustomImageCard(
                    Modifier
                        .width(230.dp)
                        .height(230.dp),
                    QRCodeGenerator.generateQrCode(
                        text = viewModel.uiState.address,
                        size = 720
                    )
                )
            }
        }
        Spacer(modifier = Modifier.padding(32.dp))
        // Text copy of crypto receive address
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .paint(
                        painterResource(id = R.drawable.bg_cryptocurrency_enabled),
                        contentScale = ContentScale.FillBounds)
                    ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.CenterVertically),
                        painter = rememberAsyncImagePainter(model = sharedViewModel.uiState.imageUrl),
                        contentDescription = null
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Text(
                            text = stringResource(
                                id = R.string.currency_receive_address_description,
                                sharedViewModel.uiState.asset ?: "",
                                sharedViewModel.uiState.assetDescription ?: ""
                            ),
                            style = Typography.body2,
                            color = MultimoneyTheme.colors.labelText,
                            maxLines = 1
                        )
                        Text(
                            text = viewModel.uiState.address,
                            style = Typography.caption.copy(fontWeight = FontWeight.SemiBold),
                            color = WhiteTransparency70,
                            maxLines = 1
                        )
                    }
                    Spacer(modifier = Modifier.padding(12.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_share),
                        contentDescription = "",
                        modifier = Modifier.clickable(onClick = {
                            if (!viewModel.uiState.isLoading) {
                                val qrcodeBitmap = QRCodeGenerator.generateQrCode(
                                    text = viewModel.uiState.address,
                                    size = 720
                                )
                                if (qrcodeBitmap != null) {
                                    viewModel.onUIEvent(
                                        CryptoReceiveAddressViewModel.UIEvent.OnShareCryptoReceiveAddress(
                                            viewModel.uiState.address,
                                            qrcodeBitmap
                                        )
                                    )
                                }
                            }
                        })
                    )
                    Spacer(modifier = Modifier.padding(12.dp))
                    Image(
                        painter = painterResource(id = R.drawable.ic_copy),
                        contentDescription = "",
                        modifier = Modifier.clickable(onClick = {
                            ClipboardUtil.copy(context = context, text = viewModel.uiState.address)
                            Toast.makeText(context,  R.string.Crypto_receive_address_toast, Toast.LENGTH_SHORT).show()
                        })
                    )
                }
            }
        }
    }
}

@Composable
fun QrCodeSkeleton() {
    ShimmerBoxView {
        ShimmerItemView(
            modifier = Modifier
                .size(width = 230.dp, height = 230.dp),
            radius = 8.dp
        )
    }
}