package com.bignerdranch.jams.androidnetworkrequestswithetagsexample

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.viewModelScope
import com.bignerdranch.jams.androidnetworkrequestswithetagsexample.repository.DownloadRepository
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class MainViewModel : ViewModel() {

    val cacheResultLiveData: MutableLiveData<Event<String?>> = MutableLiveData()

    fun downloadAndUnzipResource(context: Context, downloadRepository: DownloadRepository, downloadUrl: String, saveFilename: String?) {
        if (downloadUrl.isEmpty()) {
            Timber.e("download url cannot be null or empty. Video prop download url = $downloadUrl")
            return
        }
        if (saveFilename.isNullOrEmpty()) {
            Timber.e("Input save file name for video prop zip file cannot be null or empty.")
            return
        }
        // Internal Android files path variable
        lateinit var internalFilesDirectoryPath: String
        try {
            internalFilesDirectoryPath = context.filesDir.path
            Timber.d("zip url = $downloadUrl")
            Timber.d("Downloading zip file to internalFilesDirectoryPath = $internalFilesDirectoryPath")
        } catch (e: Exception) {
            Timber.d("Invalid internalStorageVolumes file path. %s", e.message)
        }
        val file = File(internalFilesDirectoryPath, saveFilename)
        viewModelScope.launch(Dispatchers.IO) {
            val result = Event(downloadRepository.downloadAndUnzipResource(downloadUrl, file))
            cacheResultLiveData.postValue(result)
        }
    }
}