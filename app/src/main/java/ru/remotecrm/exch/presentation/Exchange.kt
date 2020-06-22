package ru.remotecrm.exch.presentation

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_card.view.*
import ru.remotecrm.exch.data.ExchangeRatesRepo
import ru.remotecrm.exch.data.UserRepo
import ru.remotecrm.exch.domain.LatestRates
import ru.remotecrm.exch.view.activity.MainActivity
import ru.remotecrm.exch.view.adapter.ViewPagerAdapter

class Exchange(val activity: AppCompatActivity) {

    val viewPagerUpAmount by lazy { MutableLiveData<Float>().also { it.value = 0F } }
    val viewPagerDownAmount by lazy { MutableLiveData<Float>().also { it.value = 0F } }
    val viewPagerUpCurrent by lazy { MutableLiveData<String>() }
    val viewPagerDownCurrent by lazy { MutableLiveData<String>() }
    lateinit var firstAssignment: (LatestRates) -> Unit
    var latestRates: LatestRates? = null
    lateinit var adapterUp: ViewPagerAdapter
    lateinit var adapterDown: ViewPagerAdapter
    var isUpFocused = false

    fun resume() {
        ExchangeRatesRepo.running = true
    }

    fun pause() {
        ExchangeRatesRepo.running = false
    }

    fun firstAssignment(firstAssignment: (LatestRates) -> Unit) {
        this.firstAssignment = firstAssignment
        resume()
    }

    fun calculate(amount: Float, from: String, to: String): Float? {
        val fromP  = latestRates?.rates?.get(from) ?: return null
        val toP = latestRates?.rates?.get(to) ?: return null
        val k = (fromP  / toP)
        return amount / k

    }

    inline fun observeLatest(crossinline observer: (LatestRates?) -> Unit) =
        ExchangeRatesRepo.latest.observe(activity, Observer {
            if (it != null) {
                if (latestRates == null) {
                    currentPair.value = it.rates.keys.elementAt(0) to it.rates.keys.elementAt(0)
                    firstAssignment(it)
                }
                latestRates = it
                adapterUp.latestRates = it
                adapterUp.notifyDataSetChanged()
                adapterDown.latestRates = it
                adapterDown.notifyDataSetChanged()
            }
            observer(it)
        })

    fun getUserBalance(currency: String) = UserRepo.getBalance(activity, currency)
    fun setUserBalance(currency: String, value: Float) {
        UserRepo.setBalance(activity, currency, value)
        adapterUp.notifyDataSetChanged()
        adapterDown.notifyDataSetChanged()
    }

    fun exchangeUserBalance(amount: Float, from: String, to: String): Boolean {
        val fromBalance = getUserBalance(from)
        val toBalance = getUserBalance(to)

        if (amount > fromBalance)
            return false
        val exchanged = calculate(amount, from, to) ?: return false
        setUserBalance(from, fromBalance - amount)
        setUserBalance(to, toBalance + exchanged)
        return true
    }

    fun exchangeUserBalance() =
        exchangeUserBalance(
            viewPagerUpAmount.value!!,
            currentPair.value!!.first,
            currentPair.value!!.second
        )

    fun update() {
        currentPair.value = currentPair.value
    }

    val currentPair by lazy { MutableLiveData<Pair<String, String>>() }

    val onViewPagerUpPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            val currency = latestRates?.rates?.keys?.elementAt(position) ?: return
            viewPagerUpCurrent.value = currency
            currentPair.value = currency to (viewPagerDownCurrent.value ?: return)
            isUpFocused = true
            viewPagerDownAmount.value = viewPagerDownAmount.value
            activity as MainActivity
            activity.viewPagerUp.edExchange.setText(activity.viewPagerUp.edExchange.text)
            activity.viewPagerUp.post {
                adapterUp.notifyDataSetChanged()
            }
            activity.viewPagerDown.post {
                adapterDown.notifyDataSetChanged()
            }
        }
    }

    val onViewPagerDownPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            val currency = latestRates?.rates?.keys?.elementAt(position) ?: return
            viewPagerDownCurrent.value = currency
            currentPair.value = (viewPagerUpCurrent.value ?: return) to currency
            isUpFocused = false
            viewPagerUpAmount.value = viewPagerUpAmount.value
            activity as MainActivity
            activity.viewPagerDown.edExchange.setText(activity.viewPagerDown.edExchange.text)
            activity.viewPagerUp.post {
                adapterUp.notifyDataSetChanged()
            }
            activity.viewPagerDown.post {
                adapterDown.notifyDataSetChanged()
            }
        }
    }

}
