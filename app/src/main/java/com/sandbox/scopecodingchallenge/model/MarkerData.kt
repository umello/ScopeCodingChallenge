package com.sandbox.scopecodingchallenge.model

import android.graphics.Bitmap
import com.google.android.gms.maps.model.Marker

class MarkerData(val vehicle: Vehicle) {
    var vehiclePicture: Bitmap? = null
    var coordinates: VehicleCoordinates? = null
    var address: String? = null
    var marker: Marker? = null
}