package com.sandbox.scopecodingchallenge.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.sandbox.scopecodingchallenge.R
import com.sandbox.scopecodingchallenge.databinding.ActivityMapsBinding
import com.sandbox.scopecodingchallenge.model.VehicleCoordinates
import com.sandbox.scopecodingchallenge.viewmodel.MapsActivityViewModel

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModel: MapsActivityViewModel
    private var idUser = 0L
    private val vehicleList = mutableListOf<VehicleCoordinates>()
    private var googleMap: GoogleMap? = null
    private val markerList = mutableListOf<Marker>()
    private var runnableUpdaterHandler: Handler? = null
    private val runnableUpdater = object: Runnable {
        override fun run() {
            Log.d(TAG, "Requesting vehicle coordinates update...")
            viewModel.getUserVehicleList(idUser)
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
        viewModel.vehicleList.observe(this, { response ->
            Log.d(MainActivity.TAG, "observerViewModel: ${response.size}")

            if (response.isNotEmpty()) {
                vehicleList.clear()

                // Check if there are vehicles with null coordinates
                val nullCount = response.count { it.lat == null || it.lon == null }

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

                    vehicleList.addAll(response.filter { it.lat != null && it.lon != null })
                } else
                    vehicleList.addAll(response)

                updateMarkers()
            }
        })

        viewModel.requestError.observe(this, {
            if (it != null)
                Toast.makeText(applicationContext, getString(R.string.maps_activity_loading_error), Toast.LENGTH_SHORT).show()

            Log.d(MainActivity.TAG, "observerViewModel: $it")
        })

        viewModel.waitingResponse.observe(this, {
            binding.progressIndicator.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
    }

    private fun updateMarkers() {
        markerList.forEach { marker ->
            marker.remove()
        }

        markerList.clear()
        tryToPlaceMarkers()
    }

    private fun tryToPlaceMarkers() {
        if (vehicleList.isNotEmpty() && googleMap != null) {
            if (markerList.size != vehicleList.size) {
                markerList.clear()
                vehicleList.forEach { position ->
                    val coords = LatLng(position.lat!!, position.lon!!)
                    googleMap?.addMarker(MarkerOptions().position(coords).title("Car"))
                        ?.let { markerList.add(it) }
                }
                Log.d(TAG, "Markers updated (${markerList.size})")
            }

            val bounds = LatLngBounds.builder()
            vehicleList.forEach { position ->
                val coords = LatLng(position.lat!!, position.lon!!)
                bounds.include(coords)
            }

            googleMap?.setOnMapLoadedCallback {
                if (vehicleList.size >= 2)
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 200))
                else {
                    val coords = LatLng(vehicleList.first().lat!!, vehicleList.first().lon!!)
                    googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(coords, 16f))
                }
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        tryToPlaceMarkers()
    }
}