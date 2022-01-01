package com.sandbox.scopecodingchallenge.model

import android.graphics.Bitmap
import com.google.android.gms.maps.model.Marker

class MarkerData {
    lateinit var vehicle: Vehicle
    lateinit var vehiclePicture: Bitmap
    lateinit var coordinates: VehicleCoordinates
    lateinit var address: String
    lateinit var marker: Marker
}