package com.sandbox.scopecodingchallenge.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.sandbox.scopecodingchallenge.R
import com.sandbox.scopecodingchallenge.databinding.ActivityMapsBinding
import com.sandbox.scopecodingchallenge.model.MarkerData
import com.sandbox.scopecodingchallenge.viewmodel.MapsActivityViewModel

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModel: MapsActivityViewModel
    private var idUser = 0L
    private var googleMap: GoogleMap? = null
    private val markerDataMap = mutableMapOf<Long, MarkerData>()
    private var runnableUpdaterHandler: Handler? = null
    private val runnableUpdater = object: Runnable {
        override fun run() {
            Log.d(TAG, "Requesting vehicle coordinates update...")
            viewModel.getUserVehicleCoordsList(idUser)
            runnableUpdaterHandler = Handler(Looper.getMainLooper())
            runnableUpdaterHandler?.postDelayed(this, UPDATE_MARKERS_INTERVAL)
        }
    }

    companion object {
        val TAG : String = this::class.java.name
        private const val ARG_USER_ID = "arg_user_id"
        private const val UPDATE_MARKERS_INTERVAL = 1000L * 60

        fun newIntent(context: Context, userId: Long) =
            Intent(context, MapsActivity::class.java).apply {
                putExtra(ARG_USER_ID, userId)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(R.string.app_name)

        idUser = intent.getLongExtra(ARG_USER_ID, 0L)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel = ViewModelProvider(this)[MapsActivityViewModel::class.java]
        observeViewModel()
        viewModel.getUserVehicleList(idUser)
    }

    override fun onResume() {
        super.onResume()
        runnableUpdater.run()
    }

    override fun onPause() {
        super.onPause()
        runnableUpdaterHandler?.removeCallbacks(runnableUpdater);
    }

    private fun observeViewModel() {
        viewModel.vehicleCoordList.observe(this, { coordinateList ->
            Log.d(MainActivity.TAG, "Vehicle coordinates retrieved: ${coordinateList.size}")

            if (coordinateList.isNotEmpty()) {
                var nullCount = 0

                coordinateList.forEach { coordinates ->
                    if (coordinates.lat != null && coordinates.lon != null)
                        markerDataMap[coordinates.vehicleid]?.coordinates = coordinates
                    else
                        nullCount++
                }

                if (nullCount > 0) {
                    Toast.makeText(
                        applicationContext,
                        resources.getQuantityString(
                            R.plurals.maps_activity_null_coords,
                            nullCount,
                            nullCount
                        ),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                updateMarkers()
            }
        })

        viewModel.vehicleList.observe(this, { list ->
            Log.d(MainActivity.TAG, "Vehicles retrieved from local db: ${list.size}")
            markerDataMap.clear()

            list.forEach { vehicle ->
                val markerData = MarkerData(vehicle)
                markerDataMap[vehicle.vehicleid] = markerData
                Glide.with(this)
                    .asBitmap().load(vehicle.foto)
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
                            markerData.vehiclePicture = resource
                            return false
                        }
                    }
                    ).submit()

            }
        })

        viewModel.requestError.observe(this, {
            if (it != null)
                Toast.makeText(applicationContext, getString(R.string.maps_activity_loading_error), Toast.LENGTH_SHORT).show()

            Log.d(MainActivity.TAG, "Request error: $it")
        })

        viewModel.waitingResponse.observe(this, {
            binding.progressIndicator.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
    }

    private fun updateMarkers() {
        markerDataMap.values.forEach { markerData ->
            markerData.marker?.remove()
            markerData.marker = null
        }

        tryToPlaceMarkers()
    }

    private fun tryToPlaceMarkers() {
        if (markerDataMap.isNotEmpty() && googleMap != null) {
            val bounds = LatLngBounds.builder()
            var coordCount = 0
            var coords: LatLng? = null
            markerDataMap.values.forEach { markerData ->
                if (markerData.marker == null && markerData.coordinates != null) {
                    val coordinates = markerData.coordinates!!
                    coords = LatLng(coordinates.lat!!, coordinates.lon!!)
                    googleMap?.addMarker(MarkerOptions().position(coords!!).title(markerData.vehicle.make))
                        ?.let { marker ->
                            marker.tag = markerData
                            markerData.marker = marker
                        }

                    bounds.include(coords!!)
                    coordCount++
                }
            }

            googleMap?.setOnMapLoadedCallback {
                if (coordCount >= 2)
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 200))
                else {
                    if (coords != null)
                        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(coords!!, 16f))
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        googleMap?.setInfoWindowAdapter(MarkerInfoAdapter(this))
        tryToPlaceMarkers()
    }
}