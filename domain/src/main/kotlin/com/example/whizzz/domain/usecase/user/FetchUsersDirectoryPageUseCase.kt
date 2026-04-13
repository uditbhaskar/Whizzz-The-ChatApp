package com.example.whizzz.domain.usecase.user

import com.example.whizzz.domain.model.PagedUsersResult
import com.example.whizzz.domain.model.UserListCursor
import com.example.whizzz.domain.repository.UserRepository

/**
 * One page of the searchable user directory (indexed or fallback implementation in data).
 * @author udit
 */
class FetchUsersDirectoryPageUseCase(
    private val userRepository: UserRepository,
) {
    /**
     *
     * @param prefix Lowercase search prefix or blank for full list ordered by username.
     * @param pageSize Rows to return (repository may request one extra for `hasMore`).
     * @param cursor Continuation token, or `null` for the first page.
     * @param excludeUserId Uid to omit (typically self), or `null`.
     * @author udit
     */
    suspend operator fun invoke(
        prefix: String,
        pageSize: Int,
        cursor: UserListCursor?,
        excludeUserId: String?,
    ): Result<PagedUsersResult> = userRepository.fetchUsersDirectoryPage(
        prefix = prefix,
        pageSize = pageSize,
        cursor = cursor,
        excludeUserId = excludeUserId,
    )
}
