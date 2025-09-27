package com.sam.sheriaapp

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Base64
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object ImageUtils {

    // ===== NEW: SIMPLE IMAGE PROCESSING FUNCTIONS (Like your first working code) =====

    /**
     * Simple image compression function (matches your first working code approach)
     */
    fun compressImage(imageData: ByteArray, quality: Int = 50): ByteArray {
        return try {
            val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            outputStream.toByteArray()
        } catch (e: Exception) {
            // Return original if compression fails
            imageData
        }
    }

    /**
     * Alias for backward compatibility (exact same function name as your first code)
     */
    fun ImageCompress(imageData: ByteArray): ByteArray {
        return compressImage(imageData, 50)
    }

    /**
     * Convert ByteArray to Base64 string for easy storage in database
     */
    fun byteArrayToBase64(byteArray: ByteArray): String {
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    /**
     * Convert Base64 string back to ByteArray for display
     */
    fun base64ToByteArray(base64String: String): ByteArray {
        return Base64.decode(base64String, Base64.DEFAULT)
    }

    /**
     * Check if image size is within limits (1MB default)
     */
    fun isImageSizeValid(byteArray: ByteArray, maxSizeInBytes: Int = 1024 * 1024): Boolean {
        return byteArray.size <= maxSizeInBytes
    }

    /**
     * Simple URI to ByteArray conversion (like your first working code)
     */
    suspend fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    inputStream.readBytes()
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Complete image processing pipeline (matches your first code's logic)
     */
    suspend fun processSelectedImage(context: Context, uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                println("DEBUG: Starting image processing for URI: $uri")

                // Read URI to byte array (like first code)
                val byteArray = uriToByteArray(context, uri) ?: return@withContext null

                // Compress image (like first code)
                val compressedImage = ImageCompress(byteArray)

                // Check size (like first code's 1MB check)
                if (!isImageSizeValid(compressedImage)) {
                    println("DEBUG: Image too large: ${compressedImage.size} bytes")
                    return@withContext null
                }

                // Convert to Base64 for storage
                val base64Image = byteArrayToBase64(compressedImage)
                println("DEBUG: Image processing successful, size: ${base64Image.length} chars")
                base64Image
            } catch (e: Exception) {
                println("DEBUG: Image processing failed: ${e.message}")
                null
            }
        }
    }

    // ===== EXISTING FILE-BASED FUNCTIONS (KEPT FOR BACKWARD COMPATIBILITY) =====

    suspend fun copyImageToAppStorage(context: Context, uri: Uri): String {
        return withContext(Dispatchers.IO) {
            try {
                val contentResolver = context.contentResolver

                // Create a dedicated directory for profile images
                val profileDir = File(context.filesDir, "profile_images")
                if (!profileDir.exists()) {
                    profileDir.mkdirs()
                }

                // Generate unique filename with timestamp
                val uniqueFileName = "profile_${System.currentTimeMillis()}.jpg"
                val destinationFile = File(profileDir, uniqueFileName)

                // Copy the file
                contentResolver.openInputStream(uri)?.use { inputStream ->
                    FileOutputStream(destinationFile).use { outputStream ->
                        val buffer = ByteArray(4 * 1024) // 4KB buffer
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            outputStream.write(buffer, 0, bytesRead)
                        }
                        outputStream.flush()
                    }
                }

                // Return the absolute path
                println("DEBUG: Image saved to: ${destinationFile.absolutePath}")
                destinationFile.absolutePath

            } catch (e: Exception) {
                println("DEBUG: Error copying image: ${e.message}")
                throw Exception("Failed to copy image: ${e.message}")
            }
        }
    }

    // Alternative simplified version for testing
    suspend fun copyImageToAppStorageSimple(context: Context, uri: Uri): String {
        return withContext(Dispatchers.IO) {
            try {
                // Create profile images directory
                val profileDir = File(context.filesDir, "profile_images")
                if (!profileDir.exists()) {
                    profileDir.mkdirs()
                }

                // Create unique file
                val outputFile = File(profileDir, "user_profile_${System.currentTimeMillis()}.jpg")

                context.contentResolver.openInputStream(uri)?.use { input ->
                    FileOutputStream(outputFile).use { output ->
                        input.copyTo(output)
                    }
                }

                println("DEBUG: Simple copy successful: ${outputFile.absolutePath}")
                outputFile.absolutePath
            } catch (e: Exception) {
                println("DEBUG: Simple copy failed: ${e.message}")
                throw e
            }
        }
    }

    // NEW: Save byte array to file (useful if you want file storage alternative)
    suspend fun saveImageToFile(context: Context, byteArray: ByteArray): String {
        return withContext(Dispatchers.IO) {
            try {
                val profileDir = File(context.filesDir, "profile_images")
                if (!profileDir.exists()) {
                    profileDir.mkdirs()
                }

                val outputFile = File(profileDir, "profile_${System.currentTimeMillis()}.jpg")
                FileOutputStream(outputFile).use { outputStream ->
                    outputStream.write(byteArray)
                }
                println("DEBUG: ByteArray saved to file: ${outputFile.absolutePath}")
                outputFile.absolutePath
            } catch (e: Exception) {
                throw Exception("Failed to save image: ${e.message}")
            }
        }
    }

    // NEW: Load image from file as byte array
    suspend fun loadImageFromFile(context: Context, filePath: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                val bytes = File(filePath).readBytes()
                println("DEBUG: Loaded ${bytes.size} bytes from file: $filePath")
                bytes
            } catch (e: Exception) {
                println("DEBUG: Failed to load file: ${e.message}")
                null
            }
        }
    }

    // ===== EXISTING UTILITY FUNCTIONS (UNCHANGED) =====

    // Function to convert file path to content URI for Coil
    fun getUriForFilePath(filePath: String): Uri {
        return try {
            if (filePath.startsWith("content://") || filePath.startsWith("file://")) {
                Uri.parse(filePath)
            } else {
                // For absolute file paths, create file:// URI
                Uri.fromFile(File(filePath))
            }
        } catch (e: Exception) {
            println("DEBUG: Error creating URI from path: $filePath - ${e.message}")
            Uri.EMPTY
        }
    }

    // Function to check if file exists at given path
    fun doesFileExist(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            file.exists() && file.isFile && file.length() > 0
        } catch (e: Exception) {
            false
        }
    }

    // Function to get file size for debugging
    fun getFileSize(filePath: String): Long {
        return try {
            File(filePath).length()
        } catch (e: Exception) {
            -1L
        }
    }

    // Function to clean up old profile images
    suspend fun cleanupOldProfileImages(context: Context, keepRecent: Int = 3) {
        withContext(Dispatchers.IO) {
            try {
                val profileDir = File(context.filesDir, "profile_images")
                if (profileDir.exists() && profileDir.isDirectory) {
                    val files = profileDir.listFiles()?.sortedByDescending { it.lastModified() }
                    files?.let {
                        if (it.size > keepRecent) {
                            it.subList(keepRecent, it.size).forEach { file ->
                                val deleted = file.delete()
                                println("DEBUG: Cleanup deleted ${file.name}: $deleted")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("DEBUG: Cleanup error: ${e.message}")
            }
        }
    }

    // Function to list all profile images for debugging
    fun listProfileImages(context: Context): List<String> {
        return try {
            val profileDir = File(context.filesDir, "profile_images")
            if (profileDir.exists() && profileDir.isDirectory) {
                profileDir.listFiles()?.map { it.absolutePath } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Function to get file name from URI (safe version)
    private fun getFileName(contentResolver: ContentResolver, uri: Uri): String? {
        return try {
            when (uri.scheme) {
                "content" -> {
                    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (displayNameIndex >= 0) {
                                cursor.getString(displayNameIndex)
                            } else {
                                null
                            }
                        } else {
                            null
                        }
                    }
                }
                "file" -> {
                    File(uri.path).name
                }
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Function to check storage permissions (placeholder)
    fun hasStoragePermissions(context: Context): Boolean {
        // Implement actual permission checking logic here
        return true
    }

    // Function to validate image file
    fun isValidImageFile(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            file.exists() && file.isFile && file.length() > 1024 && // At least 1KB
                    (file.extension.lowercase() in listOf("jpg", "jpeg", "png", "webp"))
        } catch (e: Exception) {
            false
        }
    }

    // Function to get debug info about a file
    fun getFileDebugInfo(filePath: String): String {
        return try {
            val file = File(filePath)
            "Exists: ${file.exists()}, Size: ${file.length()} bytes, Readable: ${file.canRead()}"
        } catch (e: Exception) {
            "Error getting file info: ${e.message}"
        }
    }
}
//object ImageUtils {
//
//
//    suspend fun copyImageToAppStorage(context: Context, uri: Uri): String {
//        return withContext(Dispatchers.IO) {
//            try {
//                val contentResolver = context.contentResolver
//
//                // Create a dedicated directory for profile images
//                val profileDir = File(context.filesDir, "profile_images")
//                if (!profileDir.exists()) {
//                    profileDir.mkdirs()
//                }
//
//                // Generate unique filename with timestamp
//                val uniqueFileName = "profile_${System.currentTimeMillis()}.jpg"
//                val destinationFile = File(profileDir, uniqueFileName)
//
//                // Copy the file
//                contentResolver.openInputStream(uri)?.use { inputStream ->
//                    FileOutputStream(destinationFile).use { outputStream ->
//                        val buffer = ByteArray(4 * 1024) // 4KB buffer
//                        var bytesRead: Int
//                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
//                            outputStream.write(buffer, 0, bytesRead)
//                        }
//                        outputStream.flush()
//                    }
//                }
//
//                // Return the absolute path
//                println("DEBUG: Image saved to: ${destinationFile.absolutePath}")
//                destinationFile.absolutePath
//
//            } catch (e: Exception) {
//                println("DEBUG: Error copying image: ${e.message}")
//                throw Exception("Failed to copy image: ${e.message}")
//            }
//        }
//    }
//
//    // Alternative simplified version for testing
//    suspend fun copyImageToAppStorageSimple(context: Context, uri: Uri): String {
//        return withContext(Dispatchers.IO) {
//            try {
//                // Create profile images directory
//                val profileDir = File(context.filesDir, "profile_images")
//                if (!profileDir.exists()) {
//                    profileDir.mkdirs()
//                }
//
//                // Create unique file
//                val outputFile = File(profileDir, "user_profile_${System.currentTimeMillis()}.jpg")
//
//                context.contentResolver.openInputStream(uri)?.use { input ->
//                    FileOutputStream(outputFile).use { output ->
//                        input.copyTo(output)
//                    }
//                }
//
//                println("DEBUG: Simple copy successful: ${outputFile.absolutePath}")
//                outputFile.absolutePath
//            } catch (e: Exception) {
//                println("DEBUG: Simple copy failed: ${e.message}")
//                throw e
//            }
//        }
//    }
//
//    // Function to convert file path to content URI for Coil
//    fun getUriForFilePath(filePath: String): Uri {
//        return try {
//            if (filePath.startsWith("content://") || filePath.startsWith("file://")) {
//                Uri.parse(filePath)
//            } else {
//                // For absolute file paths, create file:// URI
//                Uri.fromFile(File(filePath))
//            }
//        } catch (e: Exception) {
//            println("DEBUG: Error creating URI from path: $filePath - ${e.message}")
//            Uri.EMPTY
//        }
//    }
//
//    // Function to check if file exists at given path
//    fun doesFileExist(filePath: String): Boolean {
//        return try {
//            val file = File(filePath)
//            file.exists() && file.isFile && file.length() > 0
//        } catch (e: Exception) {
//            false
//        }
//    }
//
//    // Function to get file size for debugging
//    fun getFileSize(filePath: String): Long {
//        return try {
//            File(filePath).length()
//        } catch (e: Exception) {
//            -1L
//        }
//    }
//
//    // Function to clean up old profile images
//    suspend fun cleanupOldProfileImages(context: Context, keepRecent: Int = 3) {
//        withContext(Dispatchers.IO) {
//            try {
//                val profileDir = File(context.filesDir, "profile_images")
//                if (profileDir.exists() && profileDir.isDirectory) {
//                    val files = profileDir.listFiles()?.sortedByDescending { it.lastModified() }
//                    files?.let {
//                        if (it.size > keepRecent) {
//                            it.subList(keepRecent, it.size).forEach { file ->
//                                val deleted = file.delete()
//                                println("DEBUG: Cleanup deleted ${file.name}: $deleted")
//                            }
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                println("DEBUG: Cleanup error: ${e.message}")
//            }
//        }
//    }
//
//    // Function to list all profile images for debugging
//    fun listProfileImages(context: Context): List<String> {
//        return try {
//            val profileDir = File(context.filesDir, "profile_images")
//            if (profileDir.exists() && profileDir.isDirectory) {
//                profileDir.listFiles()?.map { it.absolutePath } ?: emptyList()
//            } else {
//                emptyList()
//            }
//        } catch (e: Exception) {
//            emptyList()
//        }
//    }
//
//    // Function to get file name from URI (safe version)
//    private fun getFileName(contentResolver: ContentResolver, uri: Uri): String? {
//        return try {
//            when (uri.scheme) {
//                "content" -> {
//                    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
//                        if (cursor.moveToFirst()) {
//                            val displayNameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
//                            if (displayNameIndex >= 0) {
//                                cursor.getString(displayNameIndex)
//                            } else {
//                                null
//                            }
//                        } else {
//                            null
//                        }
//                    }
//                }
//                "file" -> {
//                    File(uri.path).name
//                }
//                else -> null
//            }
//        } catch (e: Exception) {
//            null
//        }
//    }
//
//    // Function to check storage permissions (placeholder)
//    fun hasStoragePermissions(context: Context): Boolean {
//        // Implement actual permission checking logic here
//        return true
//    }
//
//    // Function to validate image file
//    fun isValidImageFile(filePath: String): Boolean {
//        return try {
//            val file = File(filePath)
//            file.exists() && file.isFile && file.length() > 1024 && // At least 1KB
//                    (file.extension.lowercase() in listOf("jpg", "jpeg", "png", "webp"))
//        } catch (e: Exception) {
//            false
//        }
//    }
//
//    // Function to get debug info about a file
//    fun getFileDebugInfo(filePath: String): String {
//        return try {
//            val file = File(filePath)
//            "Exists: ${file.exists()}, Size: ${file.length()} bytes, Readable: ${file.canRead()}"
//        } catch (e: Exception) {
//            "Error getting file info: ${e.message}"
//        }
//    }
//}

