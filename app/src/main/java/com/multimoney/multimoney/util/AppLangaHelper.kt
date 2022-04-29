package com.multimoney.multimoney.util

import android.content.Context
import android.telephony.TelephonyManager
import com.applanga.android.Applanga
import java.util.Locale
import javax.inject.Inject

class AppLangaHelper @Inject constructor(val context: Context) {

    /**
     * This function starts the sdk
     */
    fun initAppLanga() {
        Applanga.init(context)
        Applanga.update {
            // nothing to do here
        }
    }

    /**
     * This function defines the language using a combine between Local and SIM configuration.
     * call this function in the Activity after super
     *
     * @return string with the format [language]-[region] e.g. es-sv, es-cr, es-gt
     */
    fun setLanguageBySim() {
        Applanga.setLanguage(getCountryCode())
    }

    private fun getCountryCode(): String {
        val locate = Locale.getDefault()
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return locate.toLanguageTag() + "-" + tm.simCountryIso
    }
}