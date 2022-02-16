package com.multimoney.domain.model.util

/**
 * Created by rodrigomiranda on 3/1/20.
 * Applaudo Studios
 *
 * Class used to expose http errors.
 * Source:
 * @see https://github.com/ronyvas/gdg-clean/tree/master/domain
 */
class HttpError(val throwable: Throwable, val errorCode: Int = 0)