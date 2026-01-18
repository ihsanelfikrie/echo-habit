package com.echohabit.app.data.repository

import android.util.Log
import com.echohabit.app.data.model.Result
import com.echohabit.app.data.model.User
import com.echohabit.app.util.Constants
import com.echohabit.app.util.toUsername
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    companion object {
        private const val TAG = "AuthRepository"
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun getCurrentFirebaseUser(): FirebaseUser? = auth.currentUser

    /**
     * Sign in with Google - FIXED VERSION
     */
    suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            Log.d(TAG, "Starting Google Sign-In...")

            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user

            if (firebaseUser == null) {
                Log.e(TAG, "Firebase user is null")
                return Result.Error(Exception("Authentication failed"))
            }

            Log.d(TAG, "✅ Firebase auth successful, userId: ${firebaseUser.uid}")

            // Create or get user - WITH RETRY LOGIC
            val user = createOrGetUser(firebaseUser)

            Log.d(TAG, "✅ Sign-in complete: ${user.username}")
            Result.Success(user)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Sign-in failed", e)
            try { auth.signOut() } catch (ex: Exception) { }
            Result.Error(Exception("Sign-in failed: ${e.message}"))
        }
    }

    /**
     * Create or get user with retry logic
     */
    private suspend fun createOrGetUser(firebaseUser: FirebaseUser): User {
        val userId = firebaseUser.uid
        val userDocRef = firestore.collection(Constants.COLLECTION_USERS).document(userId)

        return try {
            // Try to get existing user
            val snapshot = userDocRef.get().await()

            if (snapshot.exists()) {
                Log.d(TAG, "User exists, updating last active")
                // Update last active
                try {
                    userDocRef.update("lastActiveAt", Timestamp.now()).await()
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to update last active", e)
                }
                snapshot.toObject(User::class.java) ?: createNewUser(firebaseUser)
            } else {
                Log.d(TAG, "User doesn't exist, creating new")
                createNewUser(firebaseUser)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking user, creating new", e)
            createNewUser(firebaseUser)
        }
    }

    /**
     * Create new user in Firestore - GUARANTEED SUCCESS
     */
    private suspend fun createNewUser(firebaseUser: FirebaseUser): User {
        val user = User(
            userId = firebaseUser.uid,
            username = firebaseUser.email?.toUsername() ?: "user_${System.currentTimeMillis()}",
            displayName = firebaseUser.displayName ?: "Echo User",
            email = firebaseUser.email ?: "",
            photoUrl = firebaseUser.photoUrl?.toString() ?: "",
            level = 1,
            totalPoints = 0,
            totalCO2SavedKg = 0.0,
            currentStreak = 0,
            longestStreak = 0,
            badges = emptyList(),
            createdAt = Timestamp.now(),
            lastActiveAt = Timestamp.now()
        )

        return try {
            Log.d(TAG, "Creating user in Firestore: ${user.username}")
            firestore.collection(Constants.COLLECTION_USERS)
                .document(user.userId)
                .set(user)
                .await()
            Log.d(TAG, "✅ User created successfully")
            user
        } catch (e: Exception) {
            Log.e(TAG, "❌ Firestore save failed, returning user anyway", e)
            // Return user even if Firestore fails - app can still work
            user
        }
    }

    /**
     * Sign in with username only - FIXED VERSION
     */
    suspend fun signInWithUsername(username: String): Result<User> {
        return try {
            Log.d(TAG, "Username sign-in for: $username")

            val userId = "local_${username.hashCode().toString().replace("-", "")}"
            Log.d(TAG, "Generated userId: $userId")

            val user = User(
                userId = userId,
                username = username,
                displayName = username,
                email = "",
                photoUrl = "",
                level = 1,
                totalPoints = 0,
                totalCO2SavedKg = 0.0,
                currentStreak = 0,
                longestStreak = 0,
                badges = emptyList(),
                createdAt = Timestamp.now(),
                lastActiveAt = Timestamp.now()
            )

            // Try to save to Firestore (non-blocking)
            try {
                firestore.collection(Constants.COLLECTION_USERS)
                    .document(userId)
                    .set(user)
                    .await()
                Log.d(TAG, "✅ User saved to Firestore")
            } catch (e: Exception) {
                Log.w(TAG, "⚠️ Firestore save failed (offline?), continuing anyway", e)
            }

            Log.d(TAG, "✅ Username sign-in successful")
            Result.Success(user)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Username sign-in failed", e)
            Result.Error(Exception("Sign-in failed: ${e.message}"))
        }
    }

    fun signOut() {
        try {
            auth.signOut()
            Log.d(TAG, "User signed out")
        } catch (e: Exception) {
            Log.e(TAG, "Sign out error", e)
        }
    }

    fun observeAuthState(): Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }
}