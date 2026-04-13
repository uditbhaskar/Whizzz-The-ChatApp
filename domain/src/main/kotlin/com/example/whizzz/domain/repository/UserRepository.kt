package com.example.whizzz.domain.repository

import com.example.whizzz.domain.model.PagedUsersResult
import com.example.whizzz.domain.model.User
import com.example.whizzz.domain.model.UserListCursor
import kotlinx.coroutines.flow.Flow

/**
 * Port for profile CRUD and queries over stored user documents.
 * @author udit
 */
interface UserRepository {
    /**
     * Observes the signed-in user's profile document.
     *
     * @return Hot [Flow] of profile snapshots or `null` when signed out / missing.
     * @author udit
     */
    fun observeCurrentUser(): Flow<User?>

    /**
     * Observes another user's profile by [userId].
     *
     * @param userId Peer UID.
     * @return Hot [Flow] of that user's profile or `null`.
     * @author udit
     */
    fun observeUser(userId: String): Flow<User?>

    /**
     * All users ordered for directory-style listing (backed by DB ordering).
     *
     * @return Hot [Flow] of ordered users.
     * @author udit
     */
    fun observeAllUsersOrdered(): Flow<List<User>>

    /**
     * Prefix search on the stored search key (e.g. lowercase username).
     *
     * @param prefix Query prefix; empty may mean “no filter” depending on implementation.
     * @return Hot [Flow] of matching users.
     * @author udit
     */
    fun searchUsers(prefix: String): Flow<List<User>>

    /**
     * Fetches one page of users for the directory (blank [prefix] → order by `username`; else prefix range on `search`).
     *
     * Implementations may use indexed Realtime Database queries and fall back to in-memory paging when indexed reads fail
     * (except for permission errors).
     *
     * @param prefix Lowercase search prefix, or blank for full directory order.
     * @param pageSize Max users to return (implementation may request one extra to detect [PagedUsersResult.hasMore]).
     * @param cursor Continuation from the previous page, or `null` for the first page.
     * @param excludeUserId Optional uid to omit from results (e.g. current user).
     * @return [Result] containing [PagedUsersResult] on success.
     * @author udit
     */
    suspend fun fetchUsersDirectoryPage(
        prefix: String,
        pageSize: Int,
        cursor: UserListCursor?,
        excludeUserId: String?,
    ): Result<PagedUsersResult>

    /**
     * Loads profiles for the given ids (order preserved; missing nodes are skipped).
     *
     * @param ids Distinct or duplicate partner uids.
     * @return [Result] of users in the same order as first-seen ids in [ids].
     * @author udit
     */
    suspend fun fetchUsersByIds(ids: List<String>): Result<List<User>>

    /**
     * Creates the initial `Users/{userId}` record after registration.
     *
     * @param userId New account UID.
     * @param username Display name.
     * @param email Account email.
     * @param timestamp Registration time string for the schema.
     * @param imageUrl Initial avatar reference or placeholder token.
     * @return [Result] success when write completes.
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
     * @param username New display name.
     * @return [Result] success when updated.
     * @author udit
     */
    suspend fun updateUsername(username: String): Result<Unit>

    /**
     * Updates the current user's bio text.
     *
     * @param bio New bio string.
     * @return [Result] success when updated.
     * @author udit
     */
    suspend fun updateBio(bio: String): Result<Unit>

    /**
     * Persists profile image reference (HTTPS URL, or `data:image/...;base64,...` when using RTDB-only avatars).
     *
     * @param url Image URL or data URI string.
     * @return [Result] success when updated.
     * @author udit
     */
    suspend fun updateImageUrl(url: String): Result<Unit>

    /**
     * Updates presence / status string (e.g. online vs offline).
     *
     * @param status Presence string stored in the backend.
     * @return [Result] success when updated or no-op when not applicable.
     * @author udit
     */
    suspend fun setPresence(status: String): Result<Unit>

    /**
     * Stores profile photo in Realtime Database as a data-URI (no Cloud Storage; works on Spark).
     * Writes WhizzzStrings.Db.CHILD_IMAGE_URL for the current user and returns the same value.
     *
     * @param jpegBytes Compressed image bytes (keep small; see max size in implementation).
     * @param fileExtension `"jpg"` / `"jpeg"` / `"png"` for MIME type in the data URI.
     * @return [Result] with the persisted data URI on success.
     * @author udit
     */
    suspend fun uploadProfileImage(jpegBytes: ByteArray, fileExtension: String): Result<String>
}
