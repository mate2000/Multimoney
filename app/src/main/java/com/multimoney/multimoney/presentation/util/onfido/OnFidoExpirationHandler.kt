package com.multimoney.multimoney.presentation.util.onfido

import android.util.Log
import com.onfido.android.sdk.capture.token.TokenExpirationHandler

class OnFidoExpirationHandler : TokenExpirationHandler {
    override fun refreshToken(injectNewToken: (String?) -> Unit) {
        // TODO("<Your network request logic to retrieve SDK token goes here>")
        Log.d("TEST_REFRESH_TOKEN", "True")
        injectNewToken("eyJhbGciOiJFUzUxMiJ9.eyJleHAiOjE2NDY5MzUwODAsInBheWxvYWQiOnsiYXBwIjoiYjBiMmM2NDUtMGJkMi00Zjk0LWI1M2EtNGRmYWQ1Yzk2NzhhIiwiY2xpZW50X3V1aWQiOiIzNjIzYjNhZC1mNDc3LTQwYjUtYTc2Zi04MDEyZWI3NzIyZDciLCJpc19zYW5kYm94Ijp0cnVlLCJzYXJkaW5lX3Nlc3Npb24iOiJmZDdhYmViOS0yNGNkLTQ3M2YtYjI1ZC1mOGEwMTVjM2VmZTMifSwidXVpZCI6IkIzc21vT0M5eGlMIiwidXJscyI6eyJ0ZWxlcGhvbnlfdXJsIjoiaHR0cHM6Ly90ZWxlcGhvbnkub25maWRvLmNvbSIsImRldGVjdF9kb2N1bWVudF91cmwiOiJodHRwczovL3Nkay5vbmZpZG8uY29tIiwic3luY191cmwiOiJodHRwczovL3N5bmMub25maWRvLmNvbSIsImhvc3RlZF9zZGtfdXJsIjoiaHR0cHM6Ly9pZC5vbmZpZG8uY29tIiwiYXV0aF91cmwiOiJodHRwczovL2FwaS5vbmZpZG8uY29tIiwib25maWRvX2FwaV91cmwiOiJodHRwczovL2FwaS5vbmZpZG8uY29tIn19.MIGIAkIBfgfwl2zGoRurzCULa5wbPOaNEFo-1PH195gbtkMjqsnB3Z5_l1b1r5d9eNG2MwMYtlCmqTlsTEzGVdm8m3TVSFYCQgHUwE-uVtRLuzM2E6ss-A630zhoPPkw675e-wSeReLfkE8atDZ_ZhkOuOZq7KLhefRn2MU2dkggeg2WtA32oVauNw") // if you pass `null` the sdk will exit with token expired error
    }
}