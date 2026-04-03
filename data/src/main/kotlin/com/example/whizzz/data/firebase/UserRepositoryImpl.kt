package com.example.whizzz.data.firebase

import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.data.mapper.toUserOrNull
import com.example.whizzz.domain.model.User
import com.example.whizzz.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.util.Base64
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Realtime Database implementation of [UserRepository] (profile photos as data-URIs; no Cloud Storage).
 *
 * @author udit
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    database: FirebaseDatabase,
) : UserRepository {

    private val usersRef: DatabaseReference = database.reference.child(WhizzzStrings.Db.NODE_USERS)

    override fun observeCurrentUser(): Flow<User?> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val ref = usersRef.child(uid)
        val listener = listener(this::trySend)
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun observeUser(userId: String): Flow<User?> = callbackFlow {
        val ref = usersRef.child(userId)
        val listener = listener(this::trySend)
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun observeAllUsersOrdered(): Flow<List<User>> = callbackFlow {
        val ref = usersRef.orderByChild(WhizzzStrings.Db.ORDER_BY_USERNAME)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.toUserOrNull() }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        ref.addValueEventListener(listener)
        awaitClose { ref.removeEventListener(listener) }
    }

    override fun searchUsers(prefix: String): Flow<List<User>> = callbackFlow {
        if (prefix.isBlank()) {
            val ref = usersRef.orderByChild(WhizzzStrings.Db.ORDER_BY_USERNAME)
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    trySend(snapshot.children.mapNotNull { it.toUserOrNull() })
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }
            ref.addValueEventListener(listener)
            awaitClose { ref.removeEventListener(listener) }
        } else {
            val ref = usersRef.orderByChild(WhizzzStrings.Db.ORDER_BY_SEARCH)
                .startAt(prefix)
                .endAt(prefix + WhizzzStrings.Db.SEARCH_SUFFIX_HIGH)
            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    trySend(snapshot.children.mapNotNull { it.toUserOrNull() })
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }
            ref.addValueEventListener(listener)
            awaitClose { ref.removeEventListener(listener) }
        }
    }

    override suspend fun createUserProfile(
        userId: String,
        username: String,
        email: String,
        timestamp: String,
        imageUrl: String,
    ): Result<Unit> = runCatching {
        val map = mapOf(
            WhizzzStrings.Db.CHILD_ID to userId,
            WhizzzStrings.Db.CHILD_USERNAME to username,
            WhizzzStrings.Db.CHILD_EMAIL_ID to email,
            WhizzzStrings.Db.CHILD_TIMESTAMP to timestamp,
            WhizzzStrings.Db.CHILD_IMAGE_URL to imageUrl,
            WhizzzStrings.Db.CHILD_BIO to WhizzzStrings.Defaults.NEW_USER_BIO,
            WhizzzStrings.Db.CHILD_STATUS to WhizzzStrings.Defaults.STATUS_OFFLINE,
            WhizzzStrings.Db.CHILD_SEARCH to username.lowercase(),
        )
        usersRef.child(userId).setValue(map).await()
    }

    override suspend fun updateUsername(username: String): Result<Unit> = runCatching {
        val uid = auth.currentUser?.uid ?: error(WhizzzStrings.Errors.NOT_SIGNED_IN)
        val updates = mapOf(
            WhizzzStrings.Db.CHILD_USERNAME to username,
            WhizzzStrings.Db.CHILD_SEARCH to username.lowercase(),
        )
        usersRef.child(uid).updateChildren(updates).await()
    }

    override suspend fun updateBio(bio: String): Result<Unit> = runCatching {
        val uid = auth.currentUser?.uid ?: error(WhizzzStrings.Errors.NOT_SIGNED_IN)
        usersRef.child(uid).child(WhizzzStrings.Db.CHILD_BIO).setValue(bio).await()
    }

    override suspend fun updateImageUrl(url: String): Result<Unit> = runCatching {
        val uid = auth.currentUser?.uid ?: error(WhizzzStrings.Errors.NOT_SIGNED_IN)
        usersRef.child(uid).child(WhizzzStrings.Db.CHILD_IMAGE_URL).setValue(url).await()
    }

    override suspend fun setPresence(status: String): Result<Unit> = runCatching {
        val uid = auth.currentUser?.uid ?: return Result.success(Unit)
        val snap = usersRef.child(uid).get().await()
        if (snap.exists()) {
            val statusRef = usersRef.child(uid).child(WhizzzStrings.Db.CHILD_STATUS)
            statusRef.setValue(status).await()
            if (status.equals(WhizzzStrings.Defaults.PRESENCE_ONLINE, ignoreCase = true)) {
                statusRef.onDisconnect().setValue(WhizzzStrings.Defaults.STATUS_OFFLINE).await()
            }
        }
    }

    override suspend fun uploadProfileImage(jpegBytes: ByteArray, fileExtension: String): Result<String> =
        runCatching {
            val uid = auth.currentUser?.uid ?: error(WhizzzStrings.Errors.NOT_SIGNED_IN)
            val mime = when (fileExtension.lowercase()) {
                "png" -> "image/png"
                else -> "image/jpeg"
            }
            val b64 = Base64.encodeToString(jpegBytes, Base64.NO_WRAP)
            val dataUri = "data:$mime;base64,$b64"
            usersRef.child(uid).child(WhizzzStrings.Db.CHILD_IMAGE_URL).setValue(dataUri).await()
            dataUri
        }

    private fun listener(emit: (User?) -> Unit) = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            emit(snapshot.toUserOrNull())
        }

        override fun onCancelled(error: DatabaseError) {
            emit(null)
        }
    }
}
