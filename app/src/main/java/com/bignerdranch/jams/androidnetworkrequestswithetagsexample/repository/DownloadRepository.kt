package com.bignerdranch.jams.androidnetworkrequestswithetagsexample.repository

import com.bignerdranch.jams.androidnetworkrequestswithetagsexample.ext.unzipFileToDisk
import com.bignerdranch.jams.androidnetworkrequestswithetagsexample.service.DownloadResourceService
import com.bignerdranch.jams.androidnetworkrequestswithetagsexample.ext.writeResponseBodyToDisk
import timber.log.Timber
import java.io.File

const val cache_field = "cf-cache-status"
const val cache_miss = "MISS"
const val cache_hit = "HIT"
class DownloadRepository constructor(val downloadService: DownloadResourceService) {

    suspend fun downloadAndUnzipResource(downloadUrl: String, file: File): String {
        val response = downloadService.downloadResourceIfNoneMatch(downloadUrl, "")
        val responseBody = response.body()
        val xCacheValue = response.headers()[cache_field]
        var message = ""
        if (response.isSuccessful && responseBody != null) {
            // Download only if you MISS cache
            if (xCacheValue == cache_miss || !file.exists()) {
                Timber.d("Downloading file...")
                message += "Downloading file...\n"
                writeResponseBodyToDisk(responseBody, file)
            } else {
                Timber.d("File is already on device.")
                message += "File is already on device.\n"
            }
            message += "x-cache value: ${xCacheValue}\n"
            Timber.d(message)
            message
        } else {
            Timber.e("Error occurred while writing response body to disk.")
            "Error: Could not download file."
        }
        if (file.exists()) {
            val unzipFile = File(file.parent, file.name.replace(".zip", ""))
            if (unzipFile.exists()) {
                message += "File already unzipped. Skipping unzip process."
            } else {
                message += "Unzipping file ${file.parent}/${file.name}\n"
                var fileParentDirectory = file.parent + File.separator
                unzipFileToDisk(fileParentDirectory, file.name)
            }
        } else {
            message += "Cannot unzip file; file does not exist\n"
        }

        return message
    }
}