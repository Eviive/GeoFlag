package com.iut.geoflag.api

import com.iut.geoflag.models.Country
import retrofit2.Response
import retrofit2.http.GET

interface CountriesAPI {

    @GET("all")
    suspend fun getCountries(): Response<List<Country>>

}