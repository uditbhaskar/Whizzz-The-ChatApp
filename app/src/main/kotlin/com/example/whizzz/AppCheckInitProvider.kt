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
 * Runs after [com.google.firebase.provider.FirebaseInitProvider] (initOrder 99 &lt; 100).
 * Chooses Play Integrity or the debug App Check provider from build type and process flags.
 * @author udit
 */
class AppCheckInitProvider : ContentProvider() {

    /**
     * Installs the appropriate App Check provider factory on [FirebaseAppCheck].
     *
     * @return False when context is null; otherwise true after installing a provider factory.
     * @author udit
     */
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

    /**
     * Unused; this provider only runs [onCreate].
     *
     * @param uri Request uri.
     * @param projection Projected columns.
     * @param selection SQL selection.
     * @param selectionArgs Selection arguments.
     * @param sortOrder Sort order.
     * @return Always null.
     * @author udit
     */
    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor? = null

    /**
     * @param uri Request uri.
     * @return Always null.
     * @author udit
     */
    override fun getType(uri: Uri): String? = null

    /**
     * @param uri Request uri.
     * @param values Row values.
     * @return Always null.
     * @author udit
     */
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    /**
     * @param uri Request uri.
     * @param selection SQL selection.
     * @param selectionArgs Selection arguments.
     * @return Always 0.
     * @author udit
     */
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    /**
     * @param uri Request uri.
     * @param values Row values.
     * @param selection SQL selection.
     * @param selectionArgs Selection arguments.
     * @return Always 0.
     * @author udit
     */
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
