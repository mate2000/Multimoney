package com.multimoney.multimoney.presentation.ui.test.subscription

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.multimoney.domain.model.credit.CreditContractEvent

@Composable
fun SubscriptionScreen(subscriptionViewModel: SubscriptionViewModel = hiltViewModel()) {
    Column() {
        Button(onClick = {
            subscriptionViewModel.sendCreditContractEvent(
                CreditContractEvent(
                    1120654,
                    5,
                    "https://ecertia.com/Delivery/ad45b95a-f558-48fe-bf68-1c5cc2315477",
                    "Pendiente",
                    "Pendiente",
                    false,
                    "LINK GENERADO"
                )
            )
        }) {
            Text(text = "Actualizar Evicertia url")
        }

        Button(onClick = {
            subscriptionViewModel.sendCreditContractEvent(
                CreditContractEvent(
                    1120654,
                    5,
                    "https://ecertia.com/Delivery/ad45b95a-f558-48fe-bf68-1c5cc2315477",
                    "Firmado",
                    "Pendiente",
                    false,
                    "LINK GENERADO"
                )
            )
        }) {
            Text(text = "Acceptar Contrato")
        }

        Button(onClick = {
            subscriptionViewModel.sendCreditContractEvent(
                CreditContractEvent(
                    1120654,
                    5,
                    "https://ecertia.com/Delivery/ad45b95a-f558-48fe-bf68-1c5cc2315477",
                    "Rechazado",
                    "Pendiente",
                    false,
                    "LINK GENERADO"
                )
            )
        }) {
            Text(text = "Rechaza Contrato")
        }
        Button(onClick = {
            subscriptionViewModel.sendCreditContractEvent(
                CreditContractEvent(
                    1120654,
                    5,
                    "https://ecertia.com/Delivery/ad45b95a-f558-48fe-bf68-1c5cc2315477",
                    "Excedio_contador",
                    "Pendiente",
                    false,
                    "LINK GENERADO"
                )
            )
        }) {
            Text(text = "Exedio Contador")
        }
    }
}
