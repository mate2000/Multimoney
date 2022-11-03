package com.multimoney.multimoney.presentation.ui.smart.origination.fecta

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.data.util.catalog.SmartSteps
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.theme.Typography
import com.multimoney.multimoney.presentation.theme.WhiteTransparency90
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnCallMutationUpdateGlobalRequestUseCase
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueEnable
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnContinueVisible
import com.multimoney.multimoney.presentation.ui.smart.SmartViewModel.UIEvent.OnSetNavigation
import com.multimoney.multimoney.presentation.ui.smart.origination.fecta.FactaViewModel.BaseEvent.OnFormValidateCompleted
import com.multimoney.multimoney.presentation.ui.smart.origination.fecta.FactaViewModel.UIEvent.OnCrGoPageOne
import com.multimoney.multimoney.presentation.ui.smart.origination.fecta.FactaViewModel.UIEvent.OnCrGoPageTwo
import com.multimoney.multimoney.presentation.ui.smart.origination.fecta.FactaViewModel.UIEvent.OnIsActivityOfArt15Change
import com.multimoney.multimoney.presentation.ui.smart.origination.fecta.FactaViewModel.UIEvent.OnIsPEPChange
import com.multimoney.multimoney.presentation.ui.smart.origination.fecta.FactaViewModel.UIEvent.OnIsTaxPayerChange
import com.multimoney.multimoney.presentation.ui.smart.origination.fecta.FactaViewModel.UIEvent.OnIsUSCitizenChange
import com.multimoney.multimoney.presentation.ui.smart.origination.fecta.FactaViewModel.UIEvent.OnIsUSTaxPayerChange
import com.multimoney.multimoney.presentation.uielement.CustomButton
import com.multimoney.multimoney.presentation.uielement.CustomButtonType.PrimaryPrimary
import com.multimoney.multimoney.presentation.uielement.CustomRadioButtonVertical

@Composable
fun FactaScreen(
    sharedViewModel: SmartViewModel = hiltViewModel(),
    viewModel: FactaViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        sharedViewModel.onUIEvent(OnContinueEnable(viewModel.isFormValid(sharedViewModel.idBrandAsInt)))

        sharedViewModel.onUIEvent(
            OnSetNavigation(
                nextAction = {
                    sharedViewModel.onUIEvent(
                        OnCallMutationUpdateGlobalRequestUseCase(
                            accountSmartData = sharedViewModel.accountSmartData?.copy()
                        )
                    )
                },
                nextStep = SmartSteps.Four.id,
                previousStep = SmartSteps.Two.id
            )
        )
        viewModel.baseEvent.collect { event ->
            when (event) {
                is OnFormValidateCompleted -> sharedViewModel.onUIEvent(
                    OnContinueEnable(event.isFormValid)
                )
            }
        }
    }

    when (sharedViewModel.idBrandAsInt) {
        5 -> {
            when (viewModel.uiState.crPage) {
                1 -> {
                    ContentOneCR(viewModel, Modifier.padding(16.dp))
                    sharedViewModel.onUIEvent(OnContinueVisible(false))
                }
                2 -> {
                    ContentTwoCR(viewModel, Modifier.padding(16.dp))
                    sharedViewModel.onUIEvent(OnContinueVisible(true))
                    BackHandler {
                        viewModel.onUiEvent(OnCrGoPageTwo)
                    }
                }
            }
        }
        7 -> {
            ContentSV(viewModel, Modifier.padding(16.dp))
            sharedViewModel.onUIEvent(OnContinueVisible(true))
        }
    }
}

@Composable
fun ContentSV(
    viewModel: FactaViewModel,
    modifier: Modifier
) {
    Column(modifier) {
        Text(
            text = "¿Sos ciudadano o residente de los Estados Unidos?",
            style = Typography.body1.copy(
                fontSize = 17.sp,
                letterSpacing = (-0.41).sp
            ),
            color = WhiteTransparency90
        )
        CustomRadioButtonVertical(
            condition = viewModel.uiState.isUSCitizen,
            optionsOne = Triple("Si, lo soy", true, { viewModel.onUiEvent(OnIsUSCitizenChange(true, 7)) }),
            optionsTwo = Triple("No, no lo soy", false, { viewModel.onUiEvent(OnIsUSCitizenChange(false, 7)) })
        )

        Spacer(modifier = Modifier.fillMaxWidth().height(40.dp))

        Text(
            text = "¿Vos o alguno de tus familiares califican como persona politicamente expuesta (PEP)?",
            style = Typography.body1.copy(
                fontSize = 17.sp,
                letterSpacing = (-0.41).sp
            ),
            color = WhiteTransparency90
        )
        CustomRadioButtonVertical(
            condition = viewModel.uiState.isPEP,
            optionsOne = Triple("Si, si califican", true, { viewModel.onUiEvent(OnIsPEPChange(true, 7)) }),
            optionsTwo = Triple("No, no califican", false, { viewModel.onUiEvent(OnIsPEPChange(false, 7)) })
        )
    }
}

@Composable
fun ContentOneCR(
    viewModel: FactaViewModel,
    modifier: Modifier
) {
    viewModel.onUiEvent(OnCrGoPageTwo)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "¿Desempeñas entre tus actividades las citadas en los artículos 15 y 15 Bis de la ley 8204?",
                style = Typography.body1.copy(
                    fontSize = 17.sp,
                    letterSpacing = (-0.41).sp
                ),
                color = WhiteTransparency90
            )
        }
        CustomRadioButtonVertical(
            condition = viewModel.uiState.isActivityOfArt15,
            optionsOne = Triple("Si", true, { viewModel.onUiEvent(OnIsActivityOfArt15Change(true, 5)) }),
            optionsTwo = Triple("No", false, { viewModel.onUiEvent(OnIsActivityOfArt15Change(false, 5)) })
        )

        Spacer(modifier = Modifier.fillMaxWidth().height(40.dp))

        Text(
            text = "¿Vos o alguno de tus familiares califican como persona politicamente expuesta (PEP)?",
            style = Typography.body1.copy(
                fontSize = 17.sp,
                letterSpacing = (-0.41).sp
            ),
            color = WhiteTransparency90
        )
        CustomRadioButtonVertical(
            condition = viewModel.uiState.isPEP,
            optionsOne = Triple("Si, si califican", true, { viewModel.onUiEvent(OnIsPEPChange(true, 5)) }),
            optionsTwo = Triple("No, no califican", false, { viewModel.onUiEvent(OnIsPEPChange(false, 5)) })
        )
    }
    CustomButton(
        onClick = { viewModel.onUiEvent(OnCrGoPageOne) },
        text = stringResource(id = R.string.button_continue),
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, bottom = 32.dp, top = 16.dp)
            .fillMaxWidth()
            .height(48.dp),
        buttonType = PrimaryPrimary,
        enable = viewModel.uiState.isActivityOfArt15 != null && viewModel.uiState.isPEP != null
    )
}

@Composable
fun ContentTwoCR(
    viewModel: FactaViewModel,
    modifier: Modifier
) {
    Column(
        modifier = modifier
    ) {
        viewModel.onUiEvent(OnCrGoPageTwo)
        Text(
            text = "¿Sos contribuyente al pago de impuestos en Estados Unidos?",
            style = Typography.body1.copy(
                fontSize = 17.sp,
                letterSpacing = (-0.41).sp
            ),
            color = WhiteTransparency90
        )
        CustomRadioButtonVertical(
            condition = viewModel.uiState.isUSTaxPayer,
            optionsOne = Triple("Si", true, { viewModel.onUiEvent(OnIsUSTaxPayerChange(true, 5)) }),
            optionsTwo = Triple("No", false, { viewModel.onUiEvent(OnIsUSTaxPayerChange(false, 5)) })
        )

        Spacer(modifier = Modifier.fillMaxWidth().height(40.dp))

        Text(
            text = "¿Sos contribuyente al pago de impuestos en otro país diferente de Costa Rica?",
            style = Typography.body1.copy(
                fontSize = 17.sp,
                letterSpacing = (-0.41).sp
            ),
            color = WhiteTransparency90
        )
        CustomRadioButtonVertical(
            condition = viewModel.uiState.isTaxPayer,
            optionsOne = Triple("Si", true, { viewModel.onUiEvent(OnIsTaxPayerChange(true, 5)) }),
            optionsTwo = Triple("No", false, { viewModel.onUiEvent(OnIsTaxPayerChange(false, 5)) })
        )
    }
}
