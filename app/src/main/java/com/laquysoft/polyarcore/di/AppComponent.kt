package com.laquysoft.polyarcore.di

import com.laquysoft.polyarcore.PolyArcoreApp
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

/**
 * Created by joaobiriba on 22/12/2017.
 */
@Component(modules = arrayOf(
        AndroidInjectionModule::class,
        ApiModule::class,
        ActivitiesModule::class
)) interface AppComponent {
    fun inject(app: PolyArcoreApp)
}