package ru.remotecrm.exch.data

import androidx.lifecycle.MutableLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.remotecrm.exch.api.API
import ru.remotecrm.exch.domain.LatestRates
import kotlin.concurrent.thread

object ExchangeRatesRepo {
    private const val base = "USD"
    var running = false
        set(value) {
            field = value
            if (value) thread {
                while (running) {
                    API.exchangeRates.latest(base).enqueue(object : Callback<LatestRates> {
                        override fun onResponse(
                            call: Call<LatestRates>,
                            response: Response<LatestRates>
                        ) {
                            latest.value = response.body().apply {
                                this?.let {
                                    rates[base] = 1F
                                }
                            }
                        }

                        override fun onFailure(call: Call<LatestRates>, t: Throwable) {
                            latest.value = null
                            t.printStackTrace()
                        }
                    })
                    Thread.sleep(30000)
                }
            }
        }

    val latest by lazy { MutableLiveData<LatestRates?>() }

}