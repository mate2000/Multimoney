package com.multimoney.multimoney.presentation.util

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat.PNG
import android.net.Uri
import android.os.Environment
import android.view.View
import androidx.compose.ui.geometry.Rect
import androidx.core.content.FileProvider
import androidx.core.graphics.applyCanvas
import com.multimoney.multimoney.BuildConfig
import com.multimoney.multimoney.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import kotlin.math.roundToInt

class ShareHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private fun createScreenShot(view: View, capturingViewBounds: Rect): Bitmap {
        return Bitmap.createBitmap(
            capturingViewBounds.width.roundToInt(),
            capturingViewBounds.height.roundToInt(),
            Bitmap.Config.ARGB_8888
        ).applyCanvas {
            translate(-capturingViewBounds.left, -capturingViewBounds.top)
            view.draw(this)
        }
    }

    private fun getLocalBitmapUri(bitmap: Bitmap): Uri? {
        var bmpUri: Uri? = null
        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            IMAGE_NAME + System.currentTimeMillis() + IMAGE_TYPE
        )
        try {
            val out = FileOutputStream(file)
            bitmap.compress(PNG, QUALITY_FINAL, out)
            try {
                out.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            bmpUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID.plus(PROVIDER_TYPE), file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return bmpUri
    }

    fun sharedScreenShot(view: View, capturingViewBounds: Rect) {
        val bitmap = createScreenShot(view, capturingViewBounds)
        val bmpUri: Uri? = getLocalBitmapUri(bitmap)

        // Construct share intent as described above based on bitmap
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
        shareIntent.type = IMAGE_INTENT_SEND_TYPE
        shareIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(
            Intent.createChooser(shareIntent, context.getString(R.string.shared)).apply {
                addFlags(FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }

    fun shareTextPlain(text: String) {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                text
            )
            type = PLANT_TEXT_SEND_TYPE
            addFlags(FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, context.getString(R.string.shared))
        chooser.addFlags(FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    companion object {
        const val IMAGE_NAME = "share_image_"
        const val IMAGE_TYPE = ".png"
        const val QUALITY_FINAL = 90
        const val PROVIDER_TYPE = ".provider"
        const val IMAGE_INTENT_SEND_TYPE = "image/*"
        const val PLANT_TEXT_SEND_TYPE = "text/plain"
    }
}
