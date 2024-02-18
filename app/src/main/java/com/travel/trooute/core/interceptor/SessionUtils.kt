package com.travel.trooute.core.interceptor

import android.content.Context
import com.travel.trooute.R

class SessionUtils {
    companion object{
        fun sessionMsg1(context: Context):String{
            return context.getString(R.string.session_expired_1).trim().lowercase()
        }

        fun sessionMsg2(context: Context):String{
            return context.getString(R.string.session_expired_2).trim().lowercase()
        }

        fun sessionMsg3(context: Context):String{
            return context.getString(R.string.session_expired_3).trim().lowercase()
        }
    }
}