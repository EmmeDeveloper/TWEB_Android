package com.tweb.project30.util

import android.util.Log
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory


object RetrofitHelper {
//    var BASE_URL = "http://192.168.1.29:8080/Project30/"
    var BASE_URL = "http://172.20.10.3:8080/Project30/"

    fun getInstance(): Retrofit {

        val client: OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(CookiesInterceptor)
            .addInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)
                Log.d("CookieInterceptor", "Request: ${request.headers}")
                Log.d("CookieInterceptor", "Response: ${response.headers}")
                response
            }
            .build()

        val converter = JacksonConverterFactory.create(
            ObjectMapper()
                .registerModule(
                    KotlinModule.Builder()
                        .withReflectionCacheSize(512)
                        .configure(KotlinFeature.NullToEmptyCollection, false)
                        .configure(KotlinFeature.NullToEmptyMap, false)
                        .configure(KotlinFeature.NullIsSameAsDefault, false)
                        .configure(KotlinFeature.StrictNullChecks, false)
                        .build()
                )
                .registerModule(JavaTimeModule())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        )

        return Retrofit.Builder()
            .addConverterFactory(converter)
            .client(client)
            .baseUrl(BASE_URL)
            .build()
    }
}

object CookiesInterceptor : Interceptor {

    private const val COOKIE_KEY = "Cookie"
    private const val SET_COOKIE_KEY = "Set-Cookie"
    private var cookie: String? = null
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()
        cookie?.let { requestBuilder.addHeader(COOKIE_KEY, it) }

        val response = chain.proceed(requestBuilder.build())
        response.headers
            .toMultimap()[SET_COOKIE_KEY]
            ?.getOrNull(0)
            ?.also {
                cookie = it
            }

        return response
    }

}
