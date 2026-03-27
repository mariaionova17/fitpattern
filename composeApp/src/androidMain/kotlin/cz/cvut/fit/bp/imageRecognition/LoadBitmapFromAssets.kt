package cz.cvut.fit.bp.imageRecognition

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory

fun loadBitmapFromAssets(context: Context, fileName: String): Bitmap {
    context.assets.open(fileName).use { input ->
        return BitmapFactory.decodeStream(input)
            ?: error("Failed to decode bitmap: $fileName")
    }
}