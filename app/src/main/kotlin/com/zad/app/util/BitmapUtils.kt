package com.zad.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream

object BitmapUtils {

    /** Decode an image file, downsampled to [maxDim] on its longest edge, with EXIF orientation applied. */
    fun decodeOriented(path: String, maxDim: Int = 1600): Bitmap? {
        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(path, opts)
        val longest = maxOf(opts.outWidth, opts.outHeight)
        if (longest <= 0) return null
        var sample = 1
        while (longest / sample > maxDim) sample *= 2
        val decoded = BitmapFactory.decodeFile(path, BitmapFactory.Options().apply { inSampleSize = sample })
            ?: return null

        val exif = runCatching { ExifInterface(path) }.getOrNull()
        val rotation = when (exif?.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        if (rotation == 0f) return decoded
        val m = Matrix().apply { postRotate(rotation) }
        return Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, m, true)
    }

    fun newCaptureFile(context: Context): File {
        val dir = File(context.cacheDir, "captures").apply { mkdirs() }
        return File(dir, "capture_${System.currentTimeMillis()}.jpg")
    }

    fun persistThumbnail(context: Context, src: Bitmap): String {
        val dir = File(context.filesDir, "thumbs").apply { mkdirs() }
        val file = File(dir, "thumb_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { src.compress(Bitmap.CompressFormat.JPEG, 85, it) }
        return file.absolutePath
    }
}
