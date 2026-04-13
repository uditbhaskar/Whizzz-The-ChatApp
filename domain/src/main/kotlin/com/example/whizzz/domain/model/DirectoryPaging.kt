package com.example.whizzz.domain.model

/**
 * Cursor for the next page of the user directory (indexed RTDB query or in-memory full-scan pager).
 *
 * @property orderValue Indexed child value at the boundary row (`username` for full-directory mode, else `search`).
 * @property childKey Firebase node key under `Users` (the user id), used as the secondary sort key.
 * @author udit
 */
data class UserListCursor(
    val orderValue: String,
    val childKey: String,
)

/**
 * One page of directory users from [com.example.whizzz.domain.repository.UserRepository.fetchUsersDirectoryPage].
 *
 * @property users Rows in ascending directory order (`username` or `search`, then stable id tie-break).
 * @property nextCursor Cursor for the following page, or `null` when [hasMore] is false.
 * @property hasMore `true` when the data source had at least one additional row beyond this page.
 * @author udit
 */
data class PagedUsersResult(
    val users: List<User>,
    val nextCursor: UserListCursor?,
    val hasMore: Boolean,
)
