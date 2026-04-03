package com.example.whizzz

import android.content.ContentProvider
import android.content.ContentValues
import android.content.pm.ApplicationInfo
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory

/**
 * Runs **after** [com.google.firebase.provider.FirebaseInitProvider] (initOrder 99 &lt; 100).
 *
 * Play Integrity is used only when [BuildConfig.DEBUG] is false, the Gradle flag allows it, and the
 * process is not debuggable. Otherwise, the App Check **debug** provider is used — you must register
 * that token in Firebase Console or protected APIs keep failing.
 *
 * @author udit
 */
class AppCheckInitProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        val ctx = context ?: return false
        val appCheck = FirebaseAppCheck.getInstance()
        val debuggable = (ctx.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
        val usePlayIntegrity =
            !BuildConfig.DEBUG &&
                BuildConfig.USE_PLAY_INTEGRITY_APP_CHECK &&
                !debuggable
        Log.i(
            TAG,
            "buildType=${BuildConfig.BUILD_TYPE} DEBUG=${BuildConfig.DEBUG} USE_PLAY_INTEGRITY_APP_CHECK=${BuildConfig.USE_PLAY_INTEGRITY_APP_CHECK} debuggable=$debuggable → provider=${if (usePlayIntegrity) "PlayIntegrity" else "Debug"}",
        )
        if (usePlayIntegrity) {
            appCheck.installAppCheckProviderFactory(PlayIntegrityAppCheckProviderFactory.getInstance())
        } else {
            Log.i(
                TAG,
                "Using App Check debug provider: add the \"App Check debug token\" from logcat under Firebase Console → App Check → Android app → Manage debug tokens.",
            )
            appCheck.installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?,
    ): Int = 0

    private companion object {
        private const val TAG = "WhizzzAppCheck"
    }
}
