package com.sandbox.scopecodingchallenge.viewmodel

import android.app.Application
import android.location.Address
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sandbox.scopecodingchallenge.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import android.location.Geocoder
import java.util.*


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