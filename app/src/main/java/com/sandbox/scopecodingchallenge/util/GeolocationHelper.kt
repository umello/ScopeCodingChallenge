package com.sandbox.scopecodingchallenge.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.maps.model.LatLng
import java.util.*

object GeolocationHelper {

    fun getCoordinateAddress(context: Context, coords: LatLng): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses: List<Address> =
            geocoder.getFromLocation(coords.latitude, coords.longitude, 1)
        val address =
            addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        val city = addresses[0].locality
        val state = addresses[0].adminArea
        val country = addresses[0].countryName
        val postalCode = addresses[0].postalCode
        val addressStr = StringBuilder()
        addressStr.append(address).append("\n")
        addressStr.append(city).append(", ").append(state).append(" ").append(postalCode)
            .append("\n")
        addressStr.append(country)
        return addressStr.toString()
    }

}