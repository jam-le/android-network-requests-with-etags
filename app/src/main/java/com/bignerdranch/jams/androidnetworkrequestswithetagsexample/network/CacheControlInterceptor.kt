package com.bignerdranch.jams.androidnetworkrequestswithetagsexample.network

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okio.IOException

class CacheControlInterceptor constructor(val context: Context) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        if (request.method == "GET") {
            request = request.newBuilder()
                .build()
        }
        val originalResponse: Response = chain.proceed(request)
        return originalResponse.newBuilder()
            .header("Cache-Control", "private, must-revalidate")
            .build()
    }
}