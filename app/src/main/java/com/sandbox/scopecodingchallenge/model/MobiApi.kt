package com.sandbox.scopecodingchallenge.model

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface MobiApi {
    @GET("api/?op=list")
    fun getUserData() : Single<UserDataResponse>

    @GET("api/?op=getlocations")
    fun getUserVehicleList(@Query("userid") userId : Long) : Single<List<Vehicles>>
}