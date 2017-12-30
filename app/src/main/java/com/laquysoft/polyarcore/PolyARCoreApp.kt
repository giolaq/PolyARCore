package com.laquysoft.polyarcore

import android.app.Activity
import android.app.Application
import com.laquysoft.polyarcore.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

/**
 * Created by joaobiriba on 23/12/2017.
 */
class PolyArcoreApp : Application(), HasActivityInjector {
    @Inject lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> {
        return activityInjector
    }


    override fun onCreate() {
        super.onCreate()
        DaggerAppComponent.create()
                .inject(this)
    }
}