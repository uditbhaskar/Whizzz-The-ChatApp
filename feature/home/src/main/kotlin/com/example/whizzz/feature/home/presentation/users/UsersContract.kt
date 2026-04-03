package com.example.whizzz.feature.home.presentation.users

import com.example.whizzz.domain.model.User
import com.example.whizzz.domain.model.UserListCursor

/**
 * Immutable UI model for the Users tab (search + paged directory).
 *
 *
 * @property searchFieldText Bound to the search [androidx.compose.material3.TextField] (immediate keystrokes).
 * @property committedSearchQuery Debounced value that triggers directory reloads (blank commits immediately).
 * @property users Accumulated rows for the current prefix.
 * @property listError User-visible load failure, or `null`.
 * @property isInitialLoading First page / prefix change in progress.
 * @property isLoadingMore Next-page request in progress.
 * @property hasMore Whether another server page may exist.
 * @property nextCursor Paging token for [UsersUiEvent.LoadMore]; not shown in UI.
 * @property lastLoadedPrefix Normalized prefix used with [nextCursor] for pagination.
 * @property currentUserId Cached signed-in uid for guards; `null` when signed out.
 * @author udit
 */
data class UsersUiState(
    val searchFieldText: String = "",
    val committedSearchQuery: String = "",
    val users: List<User> = emptyList(),
    val listError: String? = null,
    val isInitialLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasMore: Boolean = false,
    val nextCursor: UserListCursor? = null,
    val lastLoadedPrefix: String = "",
    val currentUserId: String? = null,
)

/**
 * User intents for the Users tab (MVI).
 * @author udit
 */
sealed interface UsersUiEvent {

    /**
     * Search box text changed (debounce applied inside the ViewModel for non-blank text).
     * @author udit
     */
    data class SearchFieldChanged(val value: String) : UsersUiEvent

    /**
     * Scroll reached near end of list; loads next page when allowed.
     * @author udit
     */
    data object LoadMore : UsersUiEvent

    /**
     * User tapped retry after a list error.
     * @author udit
     */
    data object RetryList : UsersUiEvent
}
