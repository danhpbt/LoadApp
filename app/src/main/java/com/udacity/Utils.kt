package com.udacity

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.VectorDrawable
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

class Utils {
    companion object {
        fun dp2px(resource: Resources, dp: Float): Float {
            var scale = resource.displayMetrics.density
            return dp * scale;
        }

        fun sp2px(resource: Resources, sp: Float): Float {
            var scale = resource.displayMetrics.scaledDensity
            return sp * scale
        }

        fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap? {
            val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        }



    }

}