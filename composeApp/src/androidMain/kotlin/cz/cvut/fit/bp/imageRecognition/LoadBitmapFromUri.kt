package cz.cvut.fit.bp.imageRecognition

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

fun loadBitmapFromUri(context: Context, uriString: String): Bitmap {
    val uri = Uri.parse(uriString)

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        ImageDecoder.decodeBitmap(source)
    } else {
        @Suppress("DEPRECATION")
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }
}

fun prepareBitmapForProcessing(bitmap: Bitmap, maxSize: Int = 1600): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val maxDimension = maxOf(width, height)

    val scaled = if (maxDimension > maxSize) {
        val scale = maxSize.toFloat() / maxDimension.toFloat()
        Bitmap.createScaledBitmap(
            bitmap,
            (width * scale).toInt(),
            (height * scale).toInt(),
            true
        )
    } else {
        bitmap
    }

    return if (scaled.config != Bitmap.Config.ARGB_8888) {
        scaled.copy(Bitmap.Config.ARGB_8888, false)
    } else {
        scaled
    }
}