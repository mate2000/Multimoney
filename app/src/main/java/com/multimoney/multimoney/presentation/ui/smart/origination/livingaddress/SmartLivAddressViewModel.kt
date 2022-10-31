package com.multimoney.multimoney.presentation.ui.smart.origination.livingaddress

import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelOneUseCase
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelOneUseCaseImpl
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelThreeUseCase
import com.multimoney.domain.interaction.accountsmart.QueryAddressLevelTwoUseCase
import com.multimoney.multimoney.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SmartLivAddressViewModel @Inject constructor(
    addressLevelOneUseCase: QueryAddressLevelOneUseCase,
    addressLevelTwoUseCase: QueryAddressLevelTwoUseCase,
    addressLevelThreeUseCase: QueryAddressLevelThreeUseCase
) : BaseViewModel(true) {

}