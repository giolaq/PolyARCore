package com.laquysoft.polyarcore.di

import com.laquysoft.polyarcore.api.PolyService
import dagger.Module
import dagger.Provides
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit



/**
 * Created by joaobiriba on 23/12/2017.
 */
@Module class ApiModule {
    @Provides fun provideApi(): PolyService {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://poly.googleapis.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        return retrofit.create(PolyService::class.java)
    }


}