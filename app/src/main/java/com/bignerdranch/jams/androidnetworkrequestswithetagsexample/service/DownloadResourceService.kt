package com.bignerdranch.jams.androidnetworkrequestswithetagsexample.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Streaming
import retrofit2.http.Url

interface DownloadResourceService {
    @Streaming
    @GET
    suspend fun downloadResourceIfNoneMatch(
        @Url url: String,
        @Header("If-None-Match") etag: String
    ): Response<ResponseBody?>
}