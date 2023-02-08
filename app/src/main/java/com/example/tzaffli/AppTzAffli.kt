package com.example.tzaffli

import android.app.Application
import com.example.tzaffli.pref.AppPrefTz

class AppTzAffli : Application() {

    override fun onCreate() {
        super.onCreate()
        AppPrefTz.initPref(this)
    }
}