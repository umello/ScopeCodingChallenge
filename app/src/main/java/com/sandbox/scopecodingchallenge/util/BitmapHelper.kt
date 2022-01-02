package com.sandbox.scopecodingchallenge.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object BitmapHelper {
    fun bitmapFromVector(
        context: Context,
        @DrawableRes srcId: Int,
        @ColorInt color: Int,
        scale: Float = 1f
    ): BitmapDescriptor {
        val source = ResourcesCompat.getDrawable(context.resources, srcId, null)
            ?: return BitmapDescriptorFactory.defaultMarker()
        val bmp = Bitmap.createBitmap(
            (source.intrinsicWidth * scale).toInt(),
            (source.intrinsicHeight * scale).toInt(),
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bmp)
        source.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(source, color)
        source.draw(canvas)

        return BitmapDescriptorFactory.fromBitmap(bmp)
    }

    fun loadBitmap(context: Context, urlStr: String, target: (bitmap: Bitmap?) -> Unit) {
        Glide.with(context)
            .asBitmap().load(urlStr)
            .listener(object : RequestListener<Bitmap?> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap?>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap?>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    target(resource)
                    return false
                }
            }
            ).submit()
    }
}