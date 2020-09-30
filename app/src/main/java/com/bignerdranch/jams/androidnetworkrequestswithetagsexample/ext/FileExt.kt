package com.bignerdranch.jams.androidnetworkrequestswithetagsexample.ext

import okhttp3.ResponseBody
import timber.log.Timber
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

fun File.ensureExists() {
    if (!exists()) {
        parentFile.mkdirs()
        createNewFile()
    }
}

fun writeResponseBodyToDisk(body: ResponseBody, saveFile: File): Boolean {
    saveFile.ensureExists()

    body.byteStream().use { inputStream ->
        FileOutputStream(saveFile).use { outputStream ->
            return try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded: Long = 0
                while (true) {
                    val read: Int = inputStream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    outputStream.write(fileReader, 0, read)
                    fileSizeDownloaded += read.toLong()
                    Timber.d("File downloaded: $fileSizeDownloaded/$fileSize bytes")
                }
                outputStream.flush()
                true
            } catch (e: IOException) {
                Timber.e(e, "Unable to save file to disk.")
                false
            } finally {
                inputStream.close()
                outputStream.close()
            }
        }
    }
}

fun unzipFileToDisk(zipFilePath: String, zipFilename: String?): String? {
    try {
        val zipFile = File(zipFilePath, zipFilename)
        val fileInputStream = FileInputStream(zipFile)
        val zipInputStream = ZipInputStream(BufferedInputStream(fileInputStream))
        return try {
            val buffer = ByteArray(1024)
            var count: Int
            lateinit var zipEntry: ZipEntry
            val unzipFilePath = zipFilePath + zipFilename?.replace(".zip", File.separator)
            while (zipInputStream.nextEntry?.also { zipEntry = it } != null) {
                val file = File(unzipFilePath, zipEntry.name)
                file.ensureExists()

                Timber.d("Unzipping file ${file.path} to ${file.parent}")
                val dir: File = if (zipEntry.isDirectory) file else file.parentFile
                if (!dir.isDirectory && !dir.mkdirs()) throw FileNotFoundException("Failed to ensure directory: " + dir.absolutePath)
                if (zipEntry.isDirectory) continue
                FileOutputStream(file)
                    .use { fileOutputStream ->
                        Timber.d("unzipping... writing $file to $zipEntry")
                        while (zipInputStream.read(buffer).also { count = it } != -1) {
                            fileOutputStream.write(buffer, 0, count)
                        }
                        fileOutputStream.flush()
                        fileOutputStream.close()
                    }
            }
            Timber.d("Successfully unzipped files to $unzipFilePath")
            unzipFilePath
        } catch (e: Exception) {
            Timber.e("Error occurred while unzipping file to disk.\n%s", e.message)
            null
        } finally {
            fileInputStream.close()
            zipInputStream.close()
        }
    } catch (e: Exception) {
        Timber.e(e, "Error occurred while initializing File, FileInputStream, and/or ZipInputStream objects.")
        return null
    }
}