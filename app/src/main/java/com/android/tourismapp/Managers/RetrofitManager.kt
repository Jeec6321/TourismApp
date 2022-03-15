package com.android.tourismapp.Managers

import com.android.tourismapp.interfaces.Service
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitManager {

    companion object {
        const val BASE_URL = "https://maps.googleapis.com/maps/api/place/"
        const val TIMEOUT = 2000.0

        fun createService(): Service{
            val interceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val okHttpClient = OkHttpClient().newBuilder()
                .connectTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .readTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                .addInterceptor(interceptor)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl(BASE_URL)
                //.addCallAdapterFactory(NetworkResponseAdapter())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(Service::class.java)
        }
    }

}