package com.laquysoft.bernini

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by joaobiriba on 02/01/2018.
 */
class Bernini {

    private val baseUrl: String = "https://poly.googleapis.com"

    val polyService: PolyService

    private var apiKey: String = "not set"

    init {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val keyInterceptor = Interceptor { chain ->
            var request = chain.request()
            val url = request.url().newBuilder().addQueryParameter("key", apiKey).build()
            request = request.newBuilder().url(url).build()
            chain.proceed(request)
        }

        val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor)
                .addInterceptor(keyInterceptor).build()


        val retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        polyService = retrofit.create(PolyService::class.java)
    }


    fun withApiKey(apiKey: String) = fluently {
        this.apiKey = apiKey
    }
}

fun <T : Any> T.fluently(func: () -> Unit): T {
    return this.apply { func() }
}