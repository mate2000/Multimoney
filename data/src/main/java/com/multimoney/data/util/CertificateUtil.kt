package com.multimoney.data.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayInputStream
import java.security.KeyFactory
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import java.security.cert.Certificate
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.security.interfaces.RSAPrivateKey
import java.security.spec.InvalidKeySpecException
import java.security.spec.PKCS8EncodedKeySpec
import java.util.Base64
import javax.inject.Inject
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

class CertificateUtil @Inject constructor(
    @ApplicationContext val context: Context
) {

    private fun getCertificateResource(resource: Int) = context.resources.openRawResource(resource).readBytes()

    private fun getKeyStore(certAndKey: ByteArray): KeyStore? {
        val caPublicKey =
            generateCertificateFromDER(parseDERFromPEM(certAndKey, CA_PUBLIC_KEY_BEGIN, CA_PUBLIC_KEY_END))
        val clientPrivateKey =
            generatePrivateKeyFromDER(parseDERFromPEM(certAndKey, CLIENT_PRIVATE_KEY_BEGIN, CLIENT_PRIVATE_KEY_END))
        val clientPublicKey =
            generateCertificateFromDER(parseDERFromPEM(certAndKey, CLIENT_PUBLIC_KEY_BEGIN, CLIENT_PUBLIC_KEY_END))

        return KeyStore.getInstance(KeyStore.getDefaultType()).apply {
            load(null, null)
            setCertificateEntry(KEY_STORE_ALIAS, caPublicKey)
            setKeyEntry(
                KEY_STORE_ALIAS,
                clientPrivateKey,
                KEY_STORE_PASSWORD.toCharArray(),
                arrayOf<Certificate>(clientPublicKey)
            )
        }
    }

    private fun getKeyManager(keyStore: KeyStore?): KeyManagerFactory? {
        val kmf = KeyManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        kmf.init(keyStore, KEY_STORE_PASSWORD.toCharArray())
        return kmf
    }


    private fun getTrustManager(): Array<TrustManager> = arrayOf(
        @SuppressLint("CustomX509TrustManager")
        object : X509TrustManager {
            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Throws(CertificateException::class)
            override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> {
                return arrayOf()
            }
        }
    )

    fun getSSLContext(resource: Int): SSLContext = SSLContext.getInstance(SSL_PROTOCOL).apply {
        init(getKeyManager(getKeyStore(getCertificateResource(resource)))?.keyManagers, getTrustManager(), null)
    }

    fun getX509TrustManager() = getTrustManager().first() as X509TrustManager

    private fun parseDERFromPEM(pem: ByteArray, beginDelimiter: String, endDelimiter: String): ByteArray {
        var tokens = String(pem).split(beginDelimiter).toTypedArray()
        tokens = tokens[1].split(endDelimiter).toTypedArray()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Base64.getDecoder().decode(tokens[0].replace("\n", ""))
        } else {
            android.util.Base64.decode(tokens[0].replace("\n", ""), android.util.Base64.DEFAULT)
        }
    }

    @Throws(InvalidKeySpecException::class, NoSuchAlgorithmException::class)
    private fun generatePrivateKeyFromDER(keyBytes: ByteArray) =
        KeyFactory.getInstance(KEY_FACTORY_ALGORITHM).generatePrivate(PKCS8EncodedKeySpec(keyBytes)) as RSAPrivateKey

    @Throws(CertificateException::class)
    private fun generateCertificateFromDER(certBytes: ByteArray) =
        CertificateFactory.getInstance(CERTIFICATE_FACTORY_TYPE)
            .generateCertificate(ByteArrayInputStream(certBytes)) as X509Certificate

    companion object {
        const val CA_PUBLIC_KEY_BEGIN = "-----BEGIN CA PUBLIC KEY CERTIFICATE-----"
        const val CA_PUBLIC_KEY_END = "-----END CA PUBLIC KEY CERTIFICATE-----"

        const val CLIENT_PRIVATE_KEY_BEGIN = "-----BEGIN RSA CLIENT PRIVATE KEY-----"
        const val CLIENT_PRIVATE_KEY_END = "-----END RSA CLIENT PRIVATE KEY-----"

        const val CLIENT_PUBLIC_KEY_BEGIN = "-----BEGIN CLIENT PUBLIC KEY CERTIFICATE-----"
        const val CLIENT_PUBLIC_KEY_END = "-----END CLIENT PUBLIC KEY CERTIFICATE-----"

        const val SSL_PROTOCOL = "TLS"
        const val KEY_FACTORY_ALGORITHM = "RSA"
        const val CERTIFICATE_FACTORY_TYPE = "X.509"

        const val KEY_STORE_ALIAS = "alias"
        const val KEY_STORE_PASSWORD = "password"
    }
}