package com.example.data.remote

import android.content.Context
import android.util.Log

class FirebaseFashionBridge(private val context: Context) {
    private val tag = "FirebaseFashionBridge"

    var isFirebaseReady: Boolean = false
        private set

    init {
        checkFirebaseStatus()
    }

    private fun checkFirebaseStatus() {
        try {
            val firebaseAppClass = Class.forName("com.google.firebase.FirebaseApp")
            val getAppsMethod = firebaseAppClass.getMethod("getApps", Context::class.java)
            val apps = getAppsMethod.invoke(null, context) as? List<*>
            isFirebaseReady = !apps.isNullOrEmpty()
            Log.d(tag, "Firebase initialized status: $isFirebaseReady")
        } catch (e: Exception) {
            isFirebaseReady = false
            Log.d(tag, "Firebase not yet provisioned: using secure Room local persistence & direct Gemini REST engine.")
        }
    }

    fun syncWardrobeStatus(): String {
        return if (isFirebaseReady) {
            "Cloud Sync Active (Firebase Firestore)"
        } else {
            "Offline First (Encrypted Local Storage)"
        }
    }
}
