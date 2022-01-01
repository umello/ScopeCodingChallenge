package com.sandbox.scopecodingchallenge.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sandbox.scopecodingchallenge.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class MainActivityViewModel(application: Application): BaseCoroutineViewModel(application) {
    private var mobiService = MobiService()
    private val disposable = CompositeDisposable()

    val userList = MutableLiveData<List<UserData>>()
    val waitingResponse = MutableLiveData<Boolean>()
    val requestError = MutableLiveData<Throwable?>()

    fun getUserList() {
        waitingResponse.value = true
        requestError.value = null
        disposable.add(
            mobiService.getUserData()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<UserDataList>() {
                    override fun onSuccess(value: UserDataList) {
                        val userDataList = value.data.filter { it.owner != null }
                        val vehicleList = mutableListOf<Vehicle>()

                        userDataList.forEach { userData ->
                            userData.vehicles.forEach { vehicle ->
                                vehicle.ownerId = userData.userid
                                vehicleList.add(vehicle)
                            }
                        }

                        launch {
                            val dao = VehicleDatabase(getApplication()).vehicleDao()
                            dao.deleteAllVehicles()
                            dao.insertAll(*vehicleList.toTypedArray())
                            waitingResponse.value = false
                            userList.value = userDataList
                        }
                    }

                    override fun onError(e: Throwable) {
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