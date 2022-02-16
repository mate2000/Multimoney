package com.multimoney.data.util.connectivity

import android.content.ContentValues.TAG
import android.util.Log
import java.io.IOException
import java.net.InetSocketAddress
import javax.net.SocketFactory

object InternetCheckHelper {
    /**
     * Send a ping to googles primary DNS.
     * If successful, that means we have internet.
     * For more info check: https://github.com/mitchtabian/food2fork-compose/blob/master/app/src/main/java/com/codingwithmitch/food2forkcompose/interactors/app/DoesNetworkHaveInternet.kt
     */

    private const val PORT = 53
    private const val TIMEOUT = 1500

    // Make sure to execute this on a background thread.
    fun execute(socketFactory: SocketFactory): Boolean {
        return try {
            Log.d(TAG, "PINGING google.")
            val socket = socketFactory.createSocket() ?: throw IOException("Socket is null.")
            socket.connect(InetSocketAddress("8.8.8.8", PORT), TIMEOUT)
            socket.close()
            Log.d(TAG, "PING success.")
            true
        } catch (e: IOException) {
            Log.e(TAG, "No internet connection. $e")
            false
        }
    }
}
