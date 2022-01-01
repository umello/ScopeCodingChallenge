package com.sandbox.scopecodingchallenge.model

import com.sandbox.scopecodingchallenge.BuildConfig
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query

class MobiService {
    private val baseUrl = BuildConfig.MOBI_API
    private val api: MobiApi

    init {
        val httpClient = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            httpClient.addInterceptor(logging)
        }

        api = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(httpClient.build())
            .build()
            .create(MobiApi::class.java)
    }

    fun getUserData() : Single<UserDataList> = api.getUserData()
    fun getUserVehicleCoordsList(@Query("userid") userId : Long) : Single<VehicleCoordinateList> = api.getUserVehicleCoordsList(userId)
}