package com.echohabit.app.data.repository

import android.graphics.Bitmap
import android.net.Uri
import com.echohabit.app.data.model.Result
import com.echohabit.app.util.Constants
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.*

class PhotoRepository(
    private val storage: FirebaseStorage
) {

    /**
     * Upload activity photo
     * TESTING MODE: Returns dummy URL without actual upload
     */
    suspend fun uploadActivityPhoto(
        userId: String,
        imageUri: Uri
    ): Result<String> {
        return try {
            // TESTING MODE: Return dummy URL
            // Real upload disabled because Firebase Storage needs Blaze plan
            val dummyUrl = "https://via.placeholder.com/400x400.png?text=Photo+Uploaded"

            Result.Success(dummyUrl)

            /* PRODUCTION CODE (Enable when Storage is ready):
            val fileName = "activity_${System.currentTimeMillis()}.jpg"
            val path = "${Constants.STORAGE_PHOTOS}/$userId/$fileName"
            val storageRef = storage.reference.child(path)

            val uploadTask = storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await()

            Result.Success(downloadUrl.toString())
            */
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Upload generated card image
     * TESTING MODE: Returns dummy URL
     */
    suspend fun uploadCardImage(
        userId: String,
        bitmap: Bitmap
    ): Result<String> {
        return try {
            // TESTING MODE: Return dummy URL
            val dummyUrl = "https://via.placeholder.com/1080x1920.png?text=Card+Generated"

            Result.Success(dummyUrl)

            /* PRODUCTION CODE (Enable when Storage is ready):
            val fileName = "card_${System.currentTimeMillis()}.jpg"
            val path = "${Constants.STORAGE_CARDS}/$userId/$fileName"
            val storageRef = storage.reference.child(path)

            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, Constants.IMAGE_QUALITY, baos)
            val data = baos.toByteArray()

            storageRef.putBytes(data).await()
            val downloadUrl = storageRef.downloadUrl.await()

            Result.Success(downloadUrl.toString())
            */
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Upload profile photo
     * TESTING MODE: Returns dummy URL
     */
    suspend fun uploadProfilePhoto(
        userId: String,
        imageUri: Uri
    ): Result<String> {
        return try {
            // TESTING MODE: Return dummy URL
            val dummyUrl = "https://via.placeholder.com/200x200.png?text=Avatar"

            Result.Success(dummyUrl)

            /* PRODUCTION CODE (Enable when Storage is ready):
            val fileName = "avatar_${System.currentTimeMillis()}.jpg"
            val path = "${Constants.STORAGE_AVATARS}/$userId/$fileName"
            val storageRef = storage.reference.child(path)

            storageRef.putFile(imageUri).await()
            val downloadUrl = storageRef.downloadUrl.await()

            Result.Success(downloadUrl.toString())
            */
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Delete photo
     */
    suspend fun deletePhoto(photoUrl: String): Result<Unit> {
        return try {
            // Skip delete in testing mode
            Result.Success(Unit)

            /* PRODUCTION CODE:
            val storageRef = storage.getReferenceFromUrl(photoUrl)
            storageRef.delete().await()
            Result.Success(Unit)
            */
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}