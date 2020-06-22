package ru.remotecrm.exch.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object API {
    private val httpClient = OkHttpClient()
    private val gsonConverterFactory = GsonConverterFactory.create()
    val exchangeRates = Retrofit.Builder()
        .client(httpClient)
        .baseUrl("https://api.exchangeratesapi.io")
        .addConverterFactory(gsonConverterFactory)
        .build()
        .create<ExchangeRatesRestApi>()
}