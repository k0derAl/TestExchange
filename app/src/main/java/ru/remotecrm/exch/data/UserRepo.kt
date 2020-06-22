package ru.remotecrm.exch.data

import android.content.Context
import androidx.core.content.edit

object UserRepo {

    private const val SP_NAME = "user"

    fun getBalance(ctx: Context, currency: String) =
        ctx.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
            .getFloat("balance-$currency", 100F)

    fun setBalance(ctx: Context, currency: String, value: Float) =
        ctx.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
            .edit {
                putFloat("balance-$currency", value)
            }

}