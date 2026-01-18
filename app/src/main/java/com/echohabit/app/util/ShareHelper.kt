package com.echohabit.app.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class ShareHelper(private val context: Context) {

    /**
     * Save bitmap to cache and get URI
     */
    private fun saveBitmapToCache(bitmap: Bitmap, fileName: String = "card_${System.currentTimeMillis()}.jpg"): Uri? {
        return try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()

            val file = File(cachePath, fileName)
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            context.showToast("Failed to save image: ${e.message}")
            null
        }
    }

    /**
     * Share to Instagram Story
     */
    fun shareToInstagramStory(bitmap: Bitmap): Boolean {
        return try {
            val imageUri = saveBitmapToCache(bitmap, "ig_story_${System.currentTimeMillis()}.jpg")
                ?: return false

            // Instagram Story intent
            val intent = Intent("com.instagram.share.ADD_TO_STORY").apply {
                setDataAndType(imageUri, "image/jpeg")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                putExtra("interactive_asset_uri", imageUri)
                putExtra("top_background_color", "#238636")
                putExtra("bottom_background_color", "#2EA043")
            }

            if (isAppInstalled("com.instagram.android")) {
                context.grantUriPermission(
                    "com.instagram.android",
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                context.startActivity(intent)
                context.showToast("Opening Instagram...")
                true
            } else {
                context.showToast("Instagram not installed")
                // Fallback to generic share
                shareGeneric(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            context.showToast("Failed to share to Instagram: ${e.message}")
            false
        }
    }

    /**
     * Share to TikTok
     */
    fun shareToTikTok(bitmap: Bitmap): Boolean {
        return try {
            val imageUri = saveBitmapToCache(bitmap, "tiktok_${System.currentTimeMillis()}.jpg")
                ?: return false

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, "Check out my eco-friendly impact! 🌍 #EchoHabit")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setPackage("com.zhiliaoapp.musically") // TikTok package
            }

            if (isAppInstalled("com.zhiliaoapp.musically")) {
                context.grantUriPermission(
                    "com.zhiliaoapp.musically",
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                context.startActivity(intent)
                context.showToast("Opening TikTok...")
                true
            } else {
                context.showToast("TikTok not installed")
                // Fallback to generic share
                shareGeneric(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            context.showToast("Failed to share to TikTok: ${e.message}")
            false
        }
    }

    /**
     * Share to WhatsApp Status
     */
    fun shareToWhatsApp(bitmap: Bitmap): Boolean {
        return try {
            val imageUri = saveBitmapToCache(bitmap, "whatsapp_${System.currentTimeMillis()}.jpg")
                ?: return false

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, "Check out my eco-friendly impact! 🌍 #EchoHabit")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setPackage("com.whatsapp") // WhatsApp package
            }

            if (isAppInstalled("com.whatsapp")) {
                context.grantUriPermission(
                    "com.whatsapp",
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                context.startActivity(intent)
                context.showToast("Opening WhatsApp...")
                true
            } else {
                context.showToast("WhatsApp not installed")
                // Fallback to generic share
                shareGeneric(bitmap)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            context.showToast("Failed to share to WhatsApp: ${e.message}")
            false
        }
    }

    /**
     * Generic share (system share sheet)
     */
    fun shareGeneric(bitmap: Bitmap): Boolean {
        return try {
            val imageUri = saveBitmapToCache(bitmap) ?: return false

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, imageUri)
                putExtra(Intent.EXTRA_TEXT, "Check out my eco-friendly impact! 🌍 #EchoHabit")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(intent, "Share via")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            context.showToast("Failed to share: ${e.message}")
            false
        }
    }

    /**
     * Save to gallery
     */
    fun saveToGallery(bitmap: Bitmap): Boolean {
        return try {
            val filename = "EchoHabit_${System.currentTimeMillis()}.jpg"
            var fos: OutputStream? = null

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ - Use MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/EchoHabit")
                }

                val imageUri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    contentValues
                )

                imageUri?.let {
                    fos = context.contentResolver.openOutputStream(it)
                }
            } else {
                // Android 9 and below
                val imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES
                ).toString() + "/EchoHabit"
                val image = File(imagesDir, filename)

                // Create directory if not exists
                File(imagesDir).mkdirs()

                fos = FileOutputStream(image)

                // Notify media scanner
                val intent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
                intent.data = Uri.fromFile(image)
                context.sendBroadcast(intent)
            }

            fos?.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
                context.showToast("✅ Saved to gallery!")
            }

            true
        } catch (e: Exception) {
            e.printStackTrace()
            context.showToast("Failed to save: ${e.message}")
            false
        }
    }

    /**
     * Check if app is installed
     */
    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }
}