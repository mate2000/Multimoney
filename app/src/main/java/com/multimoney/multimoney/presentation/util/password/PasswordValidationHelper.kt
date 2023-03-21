package com.multimoney.multimoney.presentation.util.password

import com.multimoney.domain.interaction.security.QueryValidatePasswordStructure
import com.multimoney.domain.model.util.onSuccess
import com.multimoney.multimoney.R
import com.multimoney.multimoney.presentation.util.containForbiddenWords
import com.multimoney.multimoney.presentation.util.haveMoreThanThreeConsecutiveLetterOrNumber
import com.multimoney.multimoney.presentation.util.haveMoreThanThreeSequentialLetterOrNumber
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

class PasswordValidationHelper @Inject constructor(private val queryValidatePasswordStructure: QueryValidatePasswordStructure) {

    private lateinit var forbiddenWords: List<String>

    suspend fun getValidatePasswordStructure(idBrand: Int, pkUser: Int, user: String) {
        queryValidatePasswordStructure.invoke(pkUser = pkUser, user = user, idBrand = idBrand)
            .collectLatest { result ->
                result.onSuccess {
                    forbiddenWords = it.data
                }
            }
    }

    /**
     * validate that the password has no more than 3 repeated characters or more
     * than three sequential characters (applies to numbers and letters) and has no forbidden words.
     */
    fun validateConsecutiveCharacter(
        value: String,
        password: String,
        confirmPassword: String
    ): Pair<Boolean, Int> {
        return when {
            haveMoreThanThreeConsecutiveLetterOrNumber(value) ||
                    haveMoreThanThreeSequentialLetterOrNumber(value) -> {
                Pair(
                    true,
                    R.string.sign_up_password_requirement_max_three_characters_or_number_consecutive
                )
            }

            containForbiddenWords(value, forbiddenWords) -> {
                Pair(true, R.string.sign_up_password_requirement_forbidden_words)
            }

            (password.isNotEmpty() && confirmPassword.isNotEmpty() && confirmPassword != password) -> {
                Pair(true, R.string.sign_up_password_confirm_password_error)
            }

            else -> {
                Pair(false, R.string.error_empty)
            }
        }
    }


    /**
     * Validate if the password contains forbidden words and return a string with the forbidden words
     */
    fun getForbiddenWords(value: String): String {
        val coincidence = forbiddenWords.filter { word ->
            value.contains(word, true)
        }
        return coincidence.joinToString(", ")
    }
}