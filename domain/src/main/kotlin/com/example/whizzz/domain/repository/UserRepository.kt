package com.example.whizzz.domain.repository

import com.example.whizzz.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Reads and updates user profiles, search, presence, and profile media metadata.
 *
 * @author udit
 */
interface UserRepository {
    /**
     * Observes the signed-in user's profile document.
     *
     * @author udit
     */
    fun observeCurrentUser(): Flow<User?>

    /**
     * Observes another user's profile by [userId].
     *
     * @param userId Peer UID.
     *
     * @author udit
     */
    fun observeUser(userId: String): Flow<User?>

    /**
     * All users ordered for directory-style listing (backed by DB ordering).
     *
     * @author udit
     */
    fun observeAllUsersOrdered(): Flow<List<User>>

    /**
     * Prefix search on the stored search key (e.g. lowercase username).
     *
     * @param prefix Query prefix; empty may mean “no filter” depending on implementation.
     *
     * @author udit
     */
    fun searchUsers(prefix: String): Flow<List<User>>

    /**
     * Creates the initial `Users/{userId}` record after registration.
     *
     * @author udit
     */
    suspend fun createUserProfile(
        userId: String,
        username: String,
        email: String,
        timestamp: String,
        imageUrl: String,
    ): Result<Unit>

    /**
     * Updates the current user's display name and derived search field.
     *
     * @author udit
     */
    suspend fun updateUsername(username: String): Result<Unit>

    /**
     * Updates the current user's bio text.
     *
     * @author udit
     */
    suspend fun updateBio(bio: String): Result<Unit>

    /**
     * Persists profile image reference (HTTPS URL, or `data:image/...;base64,...` when using RTDB-only avatars).
     *
     * @author udit
     */
    suspend fun updateImageUrl(url: String): Result<Unit>

    /**
     * Updates presence / status string (e.g. online vs offline).
     *
     * @author udit
     */
    suspend fun setPresence(status: String): Result<Unit>

    /**
     * Stores profile photo in Realtime Database as a data-URI (no Cloud Storage; works on Spark).
     * Writes WhizzzStrings.Db.CHILD_IMAGE_URL for the current user and returns the same value.
     *
     * @param jpegBytes Compressed image bytes (keep small; see max size in implementation).
     * @param fileExtension `"jpg"` / `"jpeg"` / `"png"` for MIME type in the data URI.
     *
     * @author udit
     */
    suspend fun uploadProfileImage(jpegBytes: ByteArray, fileExtension: String): Result<String>
}
