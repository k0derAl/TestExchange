package ru.remotecrm.exch.domain

import com.google.gson.annotations.SerializedName

data class LatestRates(
    @SerializedName("base")
    val base: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("rates")
    val rates: HashMap<String, Float>
)