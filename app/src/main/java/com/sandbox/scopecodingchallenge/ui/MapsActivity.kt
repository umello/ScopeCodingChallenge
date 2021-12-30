package com.sandbox.scopecodingchallenge.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import com.google.android.gms.maps.model.MarkerOptions
import com.sandbox.scopecodingchallenge.R
import com.sandbox.scopecodingchallenge.databinding.ActivityMapsBinding
import com.sandbox.scopecodingchallenge.model.Vehicles
import com.sandbox.scopecodingchallenge.viewmodel.MapsActivityViewModel

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModel: MapsActivityViewModel
    private var idUser = 0L
    private val vehicleList = mutableListOf<Vehicles>()
    private var googleMap: GoogleMap? = null

    companion object {
        val TAG : String = this::class.java.name
        private const val ARG_USER_ID = "arg_user_id"

        fun newIntent(context: Context, userId: Int) =
            Intent(context, MapsActivity::class.java).apply {
                putExtra(ARG_USER_ID, userId)
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idUser = intent.getParcelableExtra(ARG_USER_ID)!!

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel = ViewModelProvider(this)[MapsActivityViewModel::class.java]
        observeViewModel()

        viewModel.getUserVehicleList(idUser)
    }

    private fun observeViewModel() {
        viewModel.vehicleList.observe(this, { response ->
            Log.d(MainActivity.TAG, "observerViewModel: ${response.size}")

            if (response.isNotEmpty()) {
                vehicleList.clear()
                vehicleList.addAll(response)
                tryToPlaceMarkers()
            }
        })

        viewModel.requestError.observe(this, {
            Toast.makeText(applicationContext, getString(R.string.maps_activity_loading_error), Toast.LENGTH_SHORT).show()
            Log.d(MainActivity.TAG, "observerViewModel: $it")
        })

        viewModel.waitingResponse.observe(this, {
            binding.progressIndicator.visibility = if (it) View.VISIBLE else View.INVISIBLE
        })
    }

    private fun tryToPlaceMarkers() {
        if (vehicleList.isNotEmpty() && googleMap != null) {
            for (position in vehicleList) {
                // Add a marker in Sydney and move the camera
                //val coords = LatLng(position., 151.0)
                //mMap.addMarker(MarkerOptions().position(coords).title("Marker in Sydney"))
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(coords))
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        tryToPlaceMarkers()
    }
}