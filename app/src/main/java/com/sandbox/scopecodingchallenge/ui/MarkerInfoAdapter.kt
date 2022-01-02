package com.sandbox.scopecodingchallenge.ui

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.sandbox.scopecodingchallenge.R
import com.sandbox.scopecodingchallenge.model.MarkerData

class MarkerInfoAdapter(private val context: Context): GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(p0: Marker): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.marker_layout, null)
        val markerData = marker.tag as MarkerData
        val vehicle = markerData.vehicle
        val imageView = view.findViewById<ImageView>(R.id.carPicture)

        if (markerData.vehiclePicture != null)
            imageView.setImageBitmap(markerData.vehiclePicture)
        else {
            imageView.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_baseline_image_not_supported_24
                )
            )
            imageView.scaleType = ImageView.ScaleType.CENTER
        }

        view.findViewById<TextView>(R.id.carMake).text = vehicle.make
        view.findViewById<TextView>(R.id.carModel).text = vehicle.model
        view.findViewById<TextView>(R.id.carAddress).text = markerData.address
        view.findViewById<ImageView>(R.id.carColor).setColorFilter(Color.parseColor(vehicle.color))
        return view
    }
}