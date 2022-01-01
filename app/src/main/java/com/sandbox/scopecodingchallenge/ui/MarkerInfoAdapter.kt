package com.sandbox.scopecodingchallenge.ui

import android.content.Context
import android.graphics.Color
import android.graphics.ColorFilter
import android.location.Address
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import com.bumptech.glide.Glide
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.sandbox.scopecodingchallenge.R
import com.sandbox.scopecodingchallenge.model.MarkerData
import com.sandbox.scopecodingchallenge.model.Vehicle
import com.sandbox.scopecodingchallenge.model.VehicleCoordinates
import java.util.*

class MarkerInfoAdapter(private val context: Context): GoogleMap.InfoWindowAdapter {
    override fun getInfoContents(p0: Marker): View? {
        return null
    }

    override fun getInfoWindow(marker: Marker): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.marker_layout, null)
        val markerData = marker.tag as MarkerData
        val vehicle = markerData.vehicle
        val coords = markerData.coordinates
        val imageView = view.findViewById<ImageView>(R.id.carPicture)

        if (markerData.vehiclePicture != null)
            imageView.setImageBitmap(markerData.vehiclePicture)
        else
            imageView.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_baseline_image_not_supported_24))

        view.findViewById<TextView>(R.id.carMake).text = vehicle.make
        view.findViewById<TextView>(R.id.carModel).text = vehicle.model
        view.findViewById<TextView>(R.id.carAddress).text = getCoordinateAddress(LatLng(coords?.lat!!, coords.lon!!))
        view.findViewById<ImageView>(R.id.carColor).setColorFilter(Color.parseColor(vehicle.color))
        return view
    }

    private fun getCoordinateAddress(coords: LatLng): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(coords.latitude, coords.longitude, 1)
        val address = addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        val city = addresses[0].locality
        val state = addresses[0].adminArea
        val country = addresses[0].countryName
        val postalCode = addresses[0].postalCode
        val addressStr = StringBuilder()
        addressStr.append(address).append("\n")
        addressStr.append(city).append(", ").append(state).append(" ").append(postalCode).append("\n")
        addressStr.append(country).append("\n")
        return addressStr.toString()
    }
}