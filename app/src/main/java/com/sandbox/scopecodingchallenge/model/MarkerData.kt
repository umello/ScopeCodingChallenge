package com.sandbox.scopecodingchallenge.model

import android.graphics.Bitmap

class MarkerData {
    lateinit var vehicle: Vehicle
    lateinit var vehiclePicture: Bitmap
    lateinit var coordinates: VehicleCoordinates
    lateinit var address: String
}