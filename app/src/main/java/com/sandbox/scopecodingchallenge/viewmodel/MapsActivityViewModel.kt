package com.sandbox.scopecodingchallenge.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.sandbox.scopecodingchallenge.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch


class MapsActivityViewModel(application: Application): BaseCoroutineViewModel(application) {
    private var mobiService = MobiService()
    private val disposable = CompositeDisposable()

    val vehicleCoordList = MutableLiveData<List<VehicleCoordinates>>()
    val vehicleList = MutableLiveData<List<Vehicle>>()
    val waitingResponse = MutableLiveData<Boolean>()
    val requestError = MutableLiveData<Throwable?>()

    fun getUserVehicleCoordsList(userId: Long) {
        waitingResponse.value = true
        requestError.value = null
        disposable.add(
            mobiService.getUserVehicleCoordsList(userId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<VehicleCoordinateList>() {
                    override fun onSuccess(value: VehicleCoordinateList) {
                        waitingResponse.value = false
                        vehicleCoordList.value = value.data
                    }

                    override fun onError(e: Throwable) {
                        waitingResponse.value = false
                        requestError.value = e
                    }
                })
        )
    }

    fun getUserVehicleList(userId: Long) {
        launch {
            val dao = VehicleDatabase(getApplication()).vehicleDao()
            val list = dao.getUserVehicles(userId)
            vehicleList.value = list
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}

/*
    fun getUserVehicleCoordsList(userId: Long) {
        waitingResponse.value = true
        requestError.value = null
        disposable.add(
            mobiService.getUserVehicleCoordsList(userId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<VehicleCoordinateList>() {
                    override fun onSuccess(coordinateList: VehicleCoordinateList) {
                        waitingResponse.value = false
                        var failureCount = 0

                        coordinateList.data.forEach { coordinates ->
                            if (coordinates.lat != null && coordinates.lon != null)
                                markerDataMap[coordinates.vehicleid]?.coordinates = coordinates
                            else
                                failureCount++
                        }

                        if (failureCount > 0)
                            nullCoordinateCount.value = failureCount
                    }

                    override fun onError(e: Throwable) {
                        waitingResponse.value = false
                        requestError.value = e
                    }
                })
        )
    }

    fun getUserVehicleList(userId: Long, context: Context) {
        launch {
            val dao = VehicleDatabase(getApplication()).vehicleDao()
            val list = dao.getUserVehicles(userId)

            list.forEach { vehicle ->
                val markerData = MarkerData(vehicle)
                markerDataMap[vehicle.vehicleid] = markerData
                Glide.with(context)
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
        }
    }

 */