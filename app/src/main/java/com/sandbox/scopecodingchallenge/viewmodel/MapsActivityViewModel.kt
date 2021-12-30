package com.sandbox.scopecodingchallenge.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sandbox.scopecodingchallenge.model.Data
import com.sandbox.scopecodingchallenge.model.MobiService
import com.sandbox.scopecodingchallenge.model.UserDataResponse
import com.sandbox.scopecodingchallenge.model.Vehicles
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class MapsActivityViewModel: ViewModel() {
    private var mobiService = MobiService()
    private val disposable = CompositeDisposable()

    val vehicleList = MutableLiveData<List<Vehicles>>()
    val waitingResponse = MutableLiveData<Boolean>()
    val requestError = MutableLiveData<Throwable?>()

    fun getUserVehicleList(userId: Long) {
        waitingResponse.value = true
        requestError.value = null
        disposable.add(
            mobiService.getUserVehicleList(userId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<Vehicles>>() {
                    override fun onSuccess(value: List<Vehicles>) {
                        waitingResponse.value = false
                        vehicleList.value = value
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