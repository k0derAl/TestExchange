package ru.remotecrm.exch.view.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.activity_main.*
import ru.remotecrm.exch.R
import ru.remotecrm.exch.presentation.Exchange
import ru.remotecrm.exch.view.adapter.ViewPagerAdapter
import java.util.*

class MainActivity : AppCompatActivity() {

    private val presentation = Exchange(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar2)
        presentation.firstAssignment {
            presentation.adapterUp = ViewPagerAdapter(presentation, it, true)
            presentation.adapterDown = ViewPagerAdapter(presentation, it, false)
            viewPagerUp.adapter = presentation.adapterUp
            viewPagerDown.adapter = presentation.adapterDown
            loading.visibility = View.INVISIBLE
            content.visibility = View.VISIBLE
        }
        presentation.observeLatest {

        }
        presentation.viewPagerUpAmount.observe(this, Observer {
            if (presentation.isUpFocused)
                try {
                    presentation.adapterDown.notifyDataSetChanged()
                } catch (_: Exception) {
                }
        })
        presentation.viewPagerDownAmount.observe(this, Observer {
            if (!presentation.isUpFocused)
                try {
                    presentation.adapterUp.notifyDataSetChanged()
                } catch (_: Exception) {
                }
        })
        presentation.currentPair.observe(this, Observer {
            title = "1 ${Currency.getInstance(it.first).symbol} = ${presentation.calculate(
                1F,
                it.first,
                it.second
            )} ${Currency.getInstance(it.second).symbol}"
            try {
                presentation.adapterUp.notifyDataSetChanged()
            } catch (_: Exception) {
            }
            try {
                presentation.adapterDown.notifyDataSetChanged()
            } catch (_: Exception) {
            }
        })
        viewPagerUp.registerOnPageChangeCallback(
            presentation.onViewPagerUpPageChangeCallback
        )
        viewPagerDown.registerOnPageChangeCallback(
            presentation.onViewPagerDownPageChangeCallback
        )
    }

    override fun onPause() {
        super.onPause()
        presentation.pause()
    }

    override fun onResume() {
        super.onResume()
        presentation.resume()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewPagerUp.unregisterOnPageChangeCallback(presentation.onViewPagerUpPageChangeCallback)
        viewPagerDown.unregisterOnPageChangeCallback(presentation.onViewPagerDownPageChangeCallback)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.exchange -> {
                if (presentation.currentPair.value!!.first==presentation.currentPair.value!!.second) {
                    Toast.makeText(this, "Change pair", Toast.LENGTH_SHORT).show()
                    return false
                }

                if (!presentation.exchangeUserBalance()) {
                    Toast.makeText(this, "Not enough money.", Toast.LENGTH_SHORT).show()
                    return false
                }
            }
            else -> return false
        }
        return true
    }
}
