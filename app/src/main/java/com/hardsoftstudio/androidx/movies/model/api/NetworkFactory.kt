package com.hardsoftstudio.androidx.movies.model.api

import com.hardsoftstudio.androidx.movies.BuildConfig
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class NetworkFactory(moshi: Moshi, apiKey: String, callAdapterFactory: LiveDataCallAdapterFactory) {

    private val appHttpClient: OkHttpClient

    private val retrofitBuilder: Retrofit.Builder

    init {
        val baseRetrofitBuilder = Retrofit.Builder()
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .addCallAdapterFactory(callAdapterFactory)

        val baseHttpClientBuilder = OkHttpClient.Builder().addInterceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url()

            val url = originalHttpUrl.newBuilder()
                    .addQueryParameter("api_key", apiKey)
                    .build()

            val requestBuilder = original.newBuilder().url(url)
            chain.proceed(requestBuilder.build())
        }
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            baseHttpClientBuilder.addInterceptor(loggingInterceptor)
        }

        appHttpClient = baseHttpClientBuilder.build()
        retrofitBuilder = baseRetrofitBuilder.client(appHttpClient)
    }

    fun <T> createApi(apiClass: Class<T>, baseUrl: String, interceptors: List<Interceptor> = emptyList()): T {
        if (interceptors.isNotEmpty()) {
            val newClient = appHttpClient.newBuilder()
            interceptors.forEach {
                newClient.addInterceptor(it)
            }
            retrofitBuilder.client(newClient.build())
        }
        return retrofitBuilder.baseUrl(baseUrl).build().create(apiClass)
    }
}
