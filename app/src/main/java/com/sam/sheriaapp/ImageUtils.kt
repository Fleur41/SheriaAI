package com.sam.sheriaapp

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    suspend fun copyImageToAppStorage(context: Context, uri: Uri): String {
        return withContext(Dispatchers.IO) {
            try {
                val contentResolver = context.contentResolver

                // Get the original file name
                val fileName = getFileName(contentResolver, uri) ?: "profile_${System.currentTimeMillis()}"

                // Create destination file
                val destinationFile = File(context.filesDir, "${fileName}.jpg")

                // Copy the file
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    FileOutputStream(destinationFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }

                destinationFile.absolutePath

            } catch (e: Exception) {
                throw Exception("Failed to copy image: ${e.message}")
            }
        }
    }

    private fun getFileName(contentResolver: ContentResolver, uri: Uri): String? {
        var name: String? = null
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                name = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        return name
    }

    // Function to check if we have storage permissions
    fun hasStoragePermissions(context: Context): Boolean {
        return true // You'll implement actual permission checking here
    }
}
//object ImageUtils {
//    suspend fun copyImageToAppStorage(context: Context, uri: Uri): String {
//        return withContext(Dispatchers.IO) {
//            val contentResolver = context.contentResolver
//            val inputStream = contentResolver.openInputStream(uri)
//            val file = File(context.filesDir, "profile_${System.currentTimeMillis()}.jpg")
//            val outputStream = FileOutputStream(file)
//
//            inputStream?.use { input ->
//                outputStream.use { output ->
//                    input.copyTo(output)
//                }
//            }
//
//            return@withContext file.absolutePath
//        }
//    }
//}