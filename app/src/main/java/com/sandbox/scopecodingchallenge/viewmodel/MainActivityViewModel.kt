package com.sandbox.scopecodingchallenge.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sandbox.scopecodingchallenge.model.UserData
import com.sandbox.scopecodingchallenge.model.MobiService
import com.sandbox.scopecodingchallenge.model.UserDataList
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class MainActivityViewModel: ViewModel() {
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