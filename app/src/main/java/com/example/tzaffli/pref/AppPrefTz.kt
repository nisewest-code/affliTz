package com.example.tzaffli.pref

import android.content.Context
import android.content.SharedPreferences
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.FutureTask

class AppPrefTz {
    companion object {
        private var mPreference: SharedPreferences? = null
        private val APP_PREFERENCE = "shared_pref"
        private val START_URL = "start_url_pref"
        private val LAST_URL = "last_url_pref"
        private val STATUS_PREF = "status_pref"
        private val CAMPAIGN_PREF = "campaign_pref"

        fun initPref(context: Context) {
            val observableCats: Observable<SharedPreferences>
            val futureTask = FutureTask {
                context.applicationContext
                    .getSharedPreferences(APP_PREFERENCE, Context.MODE_PRIVATE)
            }
            observableCats = Observable.fromFuture(futureTask)
                .doOnSubscribe { futureTask.run() }
                .subscribeOn(Schedulers.io())
            observableCats
                .subscribe { pref ->
                    mPreference = pref
                }
        }

        fun saveStartUrl(url: String?) {
            val futureTask = FutureTask {
                mPreference!!.edit()
                    .putString(START_URL, url)
                    .apply()
                true
            }
            Observable.fromFuture(futureTask)
                .doOnSubscribe {
                    futureTask.run()
                }
                .subscribeOn(Schedulers.io())
                .subscribe {

                }
        }

        fun getStartUrl(): Observable<String> {
            val observableFitnessTraining: Observable<String>
            val futureTask = FutureTask<String> {
                mPreference!!.getString(
                    START_URL,
                    ""
                )
            }
            observableFitnessTraining = Observable.fromFuture(futureTask)
                .doOnSubscribe { futureTask.run() }
                .subscribeOn(AndroidSchedulers.mainThread())
            return observableFitnessTraining
        }

        fun saveLastUrl(url: String?) {
            val futureTask = FutureTask {
                mPreference!!.edit()
                    .putString(LAST_URL, url)
                    .apply()
                true
            }
            Observable.fromFuture(futureTask)
                .doOnSubscribe {
                    futureTask.run()
                }
                .subscribeOn(Schedulers.io())
                .subscribe {

                }
        }

        fun getLastUrl(): Observable<String> {
            val observableFitnessTraining: Observable<String>
            val futureTask = FutureTask<String> {
                mPreference!!.getString(
                    LAST_URL,
                    ""
                )
            }
            observableFitnessTraining = Observable.fromFuture(futureTask)
                .doOnSubscribe { futureTask.run() }
                .subscribeOn(AndroidSchedulers.mainThread())
            return observableFitnessTraining
        }

        fun saveStatus(url: String?) {
            val futureTask = FutureTask {
                mPreference!!.edit()
                    .putString(STATUS_PREF, url)
                    .apply()
                true
            }
            Observable.fromFuture(futureTask)
                .doOnSubscribe {
                    futureTask.run()
                }
                .subscribeOn(Schedulers.io())
                .subscribe {

                }
        }

        fun getStatus(): Observable<String> {
            val observableFitnessTraining: Observable<String>
            val futureTask = FutureTask<String> {
                mPreference!!.getString(
                    STATUS_PREF,
                    ""
                )
            }
            observableFitnessTraining = Observable.fromFuture(futureTask)
                .doOnSubscribe { futureTask.run() }
                .subscribeOn(AndroidSchedulers.mainThread())
            return observableFitnessTraining
        }

        fun saveCampaign(url: String?) {
            val futureTask = FutureTask {
                mPreference!!.edit()
                    .putString(CAMPAIGN_PREF, url)
                    .apply()
                true
            }
            Observable.fromFuture(futureTask)
                .doOnSubscribe {
                    futureTask.run()
                }
                .subscribeOn(Schedulers.io())
                .subscribe {

                }
        }

        fun getCampaign(): Observable<String> {
            val observableFitnessTraining: Observable<String>
            val futureTask = FutureTask<String> {
                mPreference!!.getString(
                    CAMPAIGN_PREF,
                    ""
                )
            }
            observableFitnessTraining = Observable.fromFuture(futureTask)
                .doOnSubscribe { futureTask.run() }
                .subscribeOn(AndroidSchedulers.mainThread())
            return observableFitnessTraining
        }
    }
}