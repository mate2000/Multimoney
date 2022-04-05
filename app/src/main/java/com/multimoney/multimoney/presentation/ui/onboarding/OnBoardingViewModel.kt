package com.multimoney.multimoney.presentation.ui.onboarding

import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.base.BaseViewModel
import javax.inject.Inject

class OnBoardingViewModel @Inject constructor() : BaseViewModel() {

    fun getStepContent(step: Int): List<Int> = when (step) {
        STEP_ONE -> {
            listOf(
                R.string.onboarding_step_one_title,
                R.string.onboarding_step_one_sub_title,
                R.drawable.ic_onboarding_step_one
            )
        }
        STEP_TWO -> {
            listOf(
                R.string.onboarding_step_two_title,
                R.string.onboarding_step_two_sub_title,
                R.drawable.ic_onboarding_step_two
            )
        }
        else -> {
            listOf(
                R.string.onboarding_step_three_title,
                R.string.onboarding_step_three_sub_title,
                R.drawable.ic_onboarding_step_three
            )
        }
    }

    companion object {
        const val STEP_ONE = 1
        const val STEP_TWO = 2
        const val STEP_TITLE = 0
        const val STEP_SUBTITLE = 1
        const val STEP_ICON = 2
    }
}