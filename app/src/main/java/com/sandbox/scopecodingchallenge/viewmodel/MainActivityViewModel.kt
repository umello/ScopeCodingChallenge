package com.sandbox.scopecodingchallenge.viewmodel

import android.service.autofill.UserData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sandbox.scopecodingchallenge.model.Data
import com.sandbox.scopecodingchallenge.model.MobiService
import com.sandbox.scopecodingchallenge.model.UserDataResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class MainActivityViewModel: ViewModel() {
    private var mobiService = MobiService()
    private val disposable = CompositeDisposable()

    val userList = MutableLiveData<List<Data>>()
    val waitingResponse = MutableLiveData<Boolean>()
    val requestError = MutableLiveData<Throwable?>()

    fun getUserList() {
        waitingResponse.value = true
        requestError.value = null
        disposable.add(
            mobiService.getUserData()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<UserDataResponse>() {
                    override fun onSuccess(value: UserDataResponse) {
                        waitingResponse.value = false
                        userList.value = value.data.filter { it.owner != null }
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