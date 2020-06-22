package ru.remotecrm.exch.view.adapter

import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_card.view.*
import ru.remotecrm.exch.R
import ru.remotecrm.exch.domain.LatestRates
import ru.remotecrm.exch.presentation.Exchange
import java.util.*

class ViewPagerAdapter(
    private val presentation: Exchange,
    var latestRates: LatestRates,
    val isUp: Boolean
) : RecyclerView.Adapter<ViewPagerAdapter.VH>() {

    lateinit var editText: EditText
    private var notCalc = false

    inner class MyTextWatcher : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            if (notCalc)
            {
                notCalc = false
                return
            }
            if (isUp)
                presentation.viewPagerUpAmount.value = s.toString().toFloatOrNull() ?: 0F
            else
                presentation.viewPagerDownAmount.value = s.toString().toFloatOrNull() ?: 0F
            if (isUp)
            {
                val newValue = presentation.calculate(
                    s.toString().toFloatOrNull() ?: 0F,
                    presentation.currentPair.value?.first ?: "USD",
                    presentation.currentPair.value?.second ?: "USD")

                presentation.viewPagerDownAmount.value = newValue

                presentation.adapterDown.SetValueAmount(newValue ?: 0F)
            }
            else {
                val newValue = presentation.calculate(
                    s.toString().toFloatOrNull() ?: 0F,
                    presentation.currentPair.value?.second ?: "USD",
                    presentation.currentPair.value?.first ?: "USD"
                )
                presentation.viewPagerUpAmount.value = newValue

                presentation.adapterUp.SetValueAmount(newValue ?: 0F)
            }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        }
    }

    private val textWatcher = MyTextWatcher()
    private val stopCalc = false

    fun SetValueAmount(f: Float) {
        // TODO1
        notCalc = true
        editText.setText(f.toString())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(
            LayoutInflater.from(parent.context).inflate(R.layout.item_card, parent, false)
        )

    override fun getItemCount(): Int = latestRates.rates.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VH, position: Int) = holder.itemView.run {
        editText = edExchange
        edExchange.removeTextChangedListener(textWatcher)
        val currency = latestRates.rates.keys.elementAt(position)
        tvTitle.text = currency
        tvBalance.text =
            "You have ${presentation.getUserBalance(currency)} ${Currency.getInstance(currency).symbol}"
        val secondCurrency = (
                if (isUp)
                    presentation.currentPair.value?.second
                else
                    presentation.currentPair.value?.first) ?: "USD"
        edExchange.setText(
            (if (isUp)
                presentation.viewPagerUpAmount.value
            else
                presentation.viewPagerDownAmount.value).toString()
        )
        edExchange.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) presentation.isUpFocused = isUp
        }
        edExchange.addTextChangedListener(textWatcher)
        val amountTO = presentation.calculate(
            1F,
            currency,
            secondCurrency
        )
        tvRate.text = "1 ${Currency.getInstance(currency).symbol} = ${amountTO} ${Currency.getInstance(secondCurrency).symbol}"
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView)
}