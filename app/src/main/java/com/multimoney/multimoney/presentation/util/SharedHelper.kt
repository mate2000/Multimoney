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
import com.multimoney.multimoney.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import kotlin.math.roundToInt

class SharedHelper @Inject constructor(
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
            "share_image_" + System.currentTimeMillis() + ".png"
        )
        try {
            val out = FileOutputStream(file)
            bitmap.compress(PNG, 90, out)
            try {
                out.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            bmpUri = FileProvider.getUriForFile(context, "com.multimoney.multimoney".plus(".provider"), file)
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

        shareIntent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.payment_vaucher_title_chooser))
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri)
        shareIntent.type = "image/*"
        shareIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.addFlags(FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(
            Intent.createChooser(shareIntent, "voucer").apply {
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
            type = "text/plain"
            addFlags(FLAG_ACTIVITY_NEW_TASK)
        }
        val chooser = Intent.createChooser(intent, "")
        chooser.addFlags(FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
