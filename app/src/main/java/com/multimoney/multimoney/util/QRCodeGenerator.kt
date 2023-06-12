package com.multimoney.multimoney.util

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import timber.log.Timber

object QRCodeGenerator {

    private const val TAG = "QrCodeGenerator"

    /**
     * Generates a QR code using the specified text and size.
     *
     * @param text the text to encode in the QR code
     * @param size the size of the QR code (in pixels)
     *
     * @return a Bitmap representation of the QR code
     */
    fun generateQrCode(text: String, size: Int): Bitmap? {
        val bitMatrix = getBitMatrix(text, size)
        return if (bitMatrix != null) getBitmap(bitMatrix) else null
    }

    private fun getBitMatrix(text: String, size: Int): BitMatrix? {
        try {
            val writer = QRCodeWriter()
            return writer.encode(text, BarcodeFormat.QR_CODE, size, size)
        } catch (e: WriterException) {
            Timber.e(TAG, "Error generating QR code: ${e.message}")
        }
        return null
    }

    private fun getBitmap(bitMatrix: BitMatrix): Bitmap {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }

        return bitmap
    }
}