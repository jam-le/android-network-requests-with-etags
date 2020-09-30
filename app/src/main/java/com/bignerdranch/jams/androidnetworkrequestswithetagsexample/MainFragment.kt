package com.bignerdranch.jams.androidnetworkrequestswithetagsexample

import com.bignerdranch.jams.androidnetworkrequestswithetagsexample.service.DownloadResourceService
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bignerdranch.jams.androidnetworkrequestswithetagsexample.network.CacheControlInterceptor
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import androidx.fragment.app.viewModels
import com.bignerdranch.jams.androidnetworkrequestswithetagsexample.repository.DownloadRepository

class MainFragment : Fragment() {

    val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val loggingInterceptor = HttpLoggingInterceptor().apply { setLevel(level) }
        val cacheSize: Long =
            100 * 1024 * 1024 // 100 MB; 10 MB would probably be fine for responses that do not contain large resources
        val cache = Cache(requireContext().cacheDir!!, cacheSize)
        val cacheControlInterceptor = CacheControlInterceptor(requireContext())
        val okHttpClient = OkHttpClient.Builder()
            .cache(cache)
            .addNetworkInterceptor(cacheControlInterceptor)
            .addNetworkInterceptor(loggingInterceptor)
            .build()

        val baseUrl = "https://www.learningcontainer.com/wp-content/uploads/2020/05/"
        val gson = GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
        val gsonConverterFactory = GsonConverterFactory.create(gson)
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()

        val downloadService = retrofit.create(DownloadResourceService::class.java)
        val downloadRepository = DownloadRepository(downloadService)

        // Example zip url
        val downloadUrl = "https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-zip-file.zip"
        val button = view.findViewById<Button>(R.id.download_button)
        val xCacheText = view.findViewById<TextView>(R.id.x_cache_text)
        val saveFileName = "downloaded.zip"
        button.setOnClickListener {
            viewModel.downloadAndUnzipResource(requireContext(), downloadRepository, downloadUrl, saveFileName)
        }
        viewModel.cacheResultLiveData.observe(viewLifecycleOwner) {
            xCacheText.text = it.getContentIfNotHandled() ?: "Cache result is null"
        }

        val clearCacheButton = view.findViewById<Button>(R.id.clear_cache_button)
        clearCacheButton.setOnClickListener {
            cache.evictAll()
            xCacheText.text = "Cache cleared!"
        }

    }
}