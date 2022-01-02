package com.sandbox.scopecodingchallenge.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.sandbox.scopecodingchallenge.model.*
import com.sandbox.scopecodingchallenge.util.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class MainActivityViewModel(application: Application): BaseCoroutineViewModel(application) {
    private var mobiService = MobiService()
    private val disposable = CompositeDisposable()
    private val preferencesHelper = SharedPreferencesHelper(getApplication())

    val userList = MutableLiveData<List<UserData>>()
    val waitingResponse = MutableLiveData<Boolean>()
    val loadingLocally = MutableLiveData<Boolean>()
    val requestError = MutableLiveData<Throwable?>()

    companion object {
//        const val MAX_CACHE_AGE_MILLIS = 24 * 60 * 60 * 1000
        const val MAX_CACHE_AGE_MILLIS = 5 * 60 * 1000
    }

    fun getUserList() {
        if (preferencesHelper.getCacheDataAge() > MAX_CACHE_AGE_MILLIS) {
            loadingLocally.value = false
            getUserListFromRemote()
        } else {
            loadingLocally.value = true
            getUserListLocally()
        }
    }

    private fun getUserListFromRemote() {
        waitingResponse.value = true
        requestError.value = null
        disposable.add(
            mobiService.getUserData()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<UserDataList>() {
                    override fun onSuccess(value: UserDataList) {
                        val userDataList = value.data.filter { it.owner != null }
                        storeUserDataLocally(userDataList)
                    }

                    override fun onError(e: Throwable) {
                        waitingResponse.value = false
                        requestError.value = e
                    }
                })
        )
    }

    private fun getUserListLocally() {
        waitingResponse.value = true
        requestError.value = null

        launch {
            val userDataDao = VehicleDatabase(getApplication()).userDataDao()
            val ownerDao = VehicleDatabase(getApplication()).ownerDao()
            val vehicleDao = VehicleDatabase(getApplication()).vehicleDao()
            val userDataList = userDataDao.getAll()

            userDataList.forEach { userData ->
                userData.owner = ownerDao.getByUserId(userData.userid!!)
                userData.vehicles = vehicleDao.getUserVehicles(userData.userid)
            }

            onUserListRetrieved(userDataList)
        }
    }

    private fun onUserListRetrieved(userDataList: List<UserData>) {
        waitingResponse.value = false
        userList.value = userDataList
    }

    private fun storeUserDataLocally(userDataList: List<UserData>) {
        launch {
            val vehicleList = mutableListOf<Vehicle>()
            val ownerList = mutableListOf<Owner>()

            userDataList.forEach { userData ->
                val owner = userData.owner
                owner!!.userId = userData.userid!!
                ownerList.add(owner)

                userData.vehicles.forEach { vehicle ->
                    vehicle.ownerId = userData.userid
                    vehicleList.add(vehicle)
                }
            }

            val vehicleDao = VehicleDatabase(getApplication()).vehicleDao()
            vehicleDao.deleteAll()
            vehicleDao.insertAll(*vehicleList.toTypedArray())

            val ownerDao = VehicleDatabase(getApplication()).ownerDao()
            ownerDao.deleteAll()
            ownerDao.insertAll(*ownerList.toTypedArray())

            val userDataDao = VehicleDatabase(getApplication()).userDataDao()
            userDataDao.deleteAll()
            userDataDao.insertAll(*userDataList.toTypedArray())

            onUserListRetrieved(userDataList)
        }

        preferencesHelper.saveCacheDataTime()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}