package ru.remotecrm.exch.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import ru.remotecrm.exch.domain.LatestRates

interface ExchangeRatesRestApi {
    @GET("/latest")
    fun latest(@Query("base") base: String = "USD"): Call<LatestRates>
}

