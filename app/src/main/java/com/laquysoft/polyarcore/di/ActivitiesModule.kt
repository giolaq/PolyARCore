package com.laquysoft.polyarcore.di

import com.laquysoft.polyarcore.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by joaobiriba on 23/12/2017.
 */
@Module
abstract class ActivitiesModule {
    @ActivityScope
    @ContributesAndroidInjector
    abstract fun contributeMainActivityInjector(): MainActivity

}