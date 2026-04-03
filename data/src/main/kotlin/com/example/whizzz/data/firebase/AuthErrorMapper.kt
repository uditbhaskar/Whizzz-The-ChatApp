package com.example.whizzz.data.firebase

import com.example.whizzz.core.strings.WhizzzStrings
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

/**
 * Maps Firebase Auth and client configuration failures to user-visible copy from [WhizzzStrings].
 *
 * @author udit
 */
internal fun mapFirebaseAuthThrowable(e: Throwable): String {
    when (e) {
        is FirebaseAuthWeakPasswordException ->
            return WhizzzStrings.Errors.WEAK_PASSWORD
        is FirebaseAuthUserCollisionException ->
            return WhizzzStrings.Errors.EMAIL_ALREADY_IN_USE
        is FirebaseAuthInvalidUserException ->
            return WhizzzStrings.Errors.INVALID_EMAIL_OR_PASSWORD
        is FirebaseAuthInvalidCredentialsException ->
            return WhizzzStrings.Errors.INVALID_EMAIL_OR_PASSWORD
        is FirebaseAuthException -> {
            return when (e.errorCode) {
                "ERROR_INVALID_EMAIL" -> WhizzzStrings.Errors.INVALID_EMAIL
                "ERROR_WRONG_PASSWORD",
                "ERROR_USER_NOT_FOUND",
                "ERROR_INVALID_CREDENTIAL",
                -> WhizzzStrings.Errors.INVALID_EMAIL_OR_PASSWORD
                "ERROR_USER_DISABLED" -> WhizzzStrings.Errors.USER_DISABLED
                "ERROR_TOO_MANY_REQUESTS" -> WhizzzStrings.Errors.TOO_MANY_ATTEMPTS
                "ERROR_OPERATION_NOT_ALLOWED" -> WhizzzStrings.Errors.OPERATION_NOT_ALLOWED
                "ERROR_NETWORK_REQUEST_FAILED" -> WhizzzStrings.Errors.NETWORK_ERROR
                else ->
                    e.message?.takeIf { it.isNotBlank() }
                        ?: WhizzzStrings.Errors.SIGN_IN_FAILED
            }
        }
        else -> {
            val blob = "${e.message.orEmpty()} ${e.cause?.message.orEmpty()}"
            return when {
                blob.contains("API key", ignoreCase = true) ||
                    blob.contains("api_key", ignoreCase = true) ->
                    WhizzzStrings.Errors.FIREBASE_CONFIG_API_KEY
                blob.contains("CONFIGURATION_NOT_FOUND", ignoreCase = true) ->
                    WhizzzStrings.Errors.FIREBASE_CONFIG_API_KEY
                else ->
                    e.message?.takeIf { it.isNotBlank() }
                        ?: WhizzzStrings.Errors.GENERIC
            }
        }
    }
}
