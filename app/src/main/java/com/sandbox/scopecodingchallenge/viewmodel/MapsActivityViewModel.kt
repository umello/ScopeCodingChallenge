package com.sandbox.scopecodingchallenge.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sandbox.scopecodingchallenge.model.MobiService
import com.sandbox.scopecodingchallenge.model.VehicleCoordinateList
import com.sandbox.scopecodingchallenge.model.VehicleCoordinates
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class MapsActivityViewModel: ViewModel() {
    private var mobiService = MobiService()
    private val disposable = CompositeDisposable()

    val vehicleList = MutableLiveData<List<VehicleCoordinates>>()
    val waitingResponse = MutableLiveData<Boolean>()
    val requestError = MutableLiveData<Throwable?>()

    fun getUserVehicleList(userId: Long) {
        waitingResponse.value = true
        requestError.value = null
        disposable.add(
            mobiService.getUserVehicleList(userId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<VehicleCoordinateList>() {
                    override fun onSuccess(value: VehicleCoordinateList) {
                        waitingResponse.value = false
                        vehicleList.value = value.data
                    }

                    override fun onError(e: Throwable?) {
                        waitingResponse.value = false
                        requestError.value = e
                    }
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}