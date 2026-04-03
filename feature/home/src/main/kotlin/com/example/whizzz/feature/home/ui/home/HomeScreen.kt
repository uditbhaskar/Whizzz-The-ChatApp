package com.example.whizzz.feature.home.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whizzz.core.ui.theme.WhizzzTheme
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.core.ui.R as UiR
import com.example.whizzz.core.ui.WhizzzPacificoFamily
import com.example.whizzz.core.ui.WhizzzProfileAvatar
import com.example.whizzz.core.ui.whizzzImeInsetPadding
import com.example.whizzz.core.ui.theme.WhizzzScreenBackground
import com.example.whizzz.domain.model.User
import com.example.whizzz.feature.home.presentation.conversations.ConversationsUiEvent
import com.example.whizzz.feature.home.presentation.conversations.ConversationsViewModel
import com.example.whizzz.feature.home.presentation.shell.HomeUiEvent
import com.example.whizzz.feature.home.presentation.shell.HomeViewModel
import com.example.whizzz.feature.home.presentation.users.UsersUiEvent
import com.example.whizzz.feature.home.presentation.users.UsersViewModel
import com.example.whizzz.feature.profile.ui.ProfileRoute
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
private val AppBarBlack = Color.Black
private val MutedTab = Color(0xFFAFACAC)
private val SearchHint = Color(0xFF797B7E)
/**
 * Home shell UI: top bar (avatar + name, tap opens Profile tab), tab row, optional offline banner, and three tab slots.
 *
 * @param isOnline When false, shows the offline banner under the tabs.
 * @param selectedTabIndex Selected tab index (`0` Chats, `1` Users, `2` Profile).
 * @param onTabSelected Called when the user selects a tab.
 * @param currentUser Signed-in user for header; null shows a neutral label until loaded.
 * @param onProfileHeaderClick Switches to the Profile tab (same as tapping Profile).
 * @param tab0 Composable content for the Chats tab.
 * @param tab1 Composable content for the Users tab.
 * @param tab2 Composable content for the Profile tab.
 * @author udit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeMainLayout(
    isOnline: Boolean,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    currentUser: User?,
    onProfileHeaderClick: () -> Unit,
    tab0: @Composable () -> Unit,
    tab1: @Composable () -> Unit,
    tab2: @Composable () -> Unit,
) {
    val headerTitle = currentUser?.username?.takeIf { it.isNotBlank() }
        ?: WhizzzStrings.Ui.PROFILE
    val headerImageUrl = currentUser?.imageUrl.orEmpty()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = WhizzzScreenBackground,
        topBar = {
            Surface(color = AppBarBlack) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        modifier = Modifier
                            .wrapContentWidth(align = Alignment.Start)
                            .clip(RoundedCornerShape(10.dp))
                            .clickable(onClick = onProfileHeaderClick)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        WhizzzProfileAvatar(
                            imageUrl = headerImageUrl,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop,
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = headerTitle,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.widthIn(max = 220.dp),
                        )
                    }
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(WhizzzScreenBackground),
        ) {
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = AppBarBlack,
                contentColor = Color.White,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTabIndex, matchContentSize = false),
                        width = Dp.Unspecified,
                        color = Color(0xFF63FFA3),
                    )
                },
                divider = {},
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { onTabSelected(0) },
                    text = {
                        Text(
                            WhizzzStrings.Ui.CHATS,
                            color = if (selectedTabIndex == 0) Color.White else MutedTab,
                        )
                    },
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { onTabSelected(1) },
                    text = {
                        Text(
                            WhizzzStrings.Ui.USERS,
                            color = if (selectedTabIndex == 1) Color.White else MutedTab,
                        )
                    },
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { onTabSelected(2) },
                    text = {
                        Text(
                            WhizzzStrings.Ui.PROFILE,
                            color = if (selectedTabIndex == 2) Color.White else MutedTab,
                        )
                    },
                )
            }
            if (!isOnline) {
                Surface(color = Color(0xFF5C3A2E)) {
                    Text(
                        text = WhizzzStrings.Ui.OFFLINE_INDICATOR,
                        color = Color.White,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                }
            }
            when (selectedTabIndex) {
                0 -> tab0()
                1 -> tab1()
                2 -> tab2()
            }
        }
    }
}

/**
 * Home feature route: wires [HomeViewModel] (connectivity, sign-out, FCM token) and embeds chats/users/profile tabs via [HomeMainLayout].
 *
 * @param onSignOut Navigate after local sign-out (e.g. to login).
 * @param onOpenChat Navigate to the chat thread for the given partner user id.
 * @param homeViewModel Shell MVI [HomeViewModel].
 * @author udit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    onSignOut: () -> Unit,
    onOpenChat: (String) -> Unit,
    homeViewModel: HomeViewModel = koinViewModel(),
) {
    LaunchedEffect(Unit) {
        homeViewModel.onEvent(HomeUiEvent.RegisterPushToken)
    }

    val homeState by homeViewModel.state.collectAsStateWithLifecycle()
    val isOnline = homeState.isOnline

    var tabIndex by rememberSaveable { mutableIntStateOf(0) }

    HomeMainLayout(
        isOnline = isOnline,
        selectedTabIndex = tabIndex,
        onTabSelected = { tabIndex = it },
        currentUser = homeState.currentUser,
        onProfileHeaderClick = { tabIndex = 2 },
        tab0 = { ConversationsTab(onOpenChat = onOpenChat) },
        tab1 = { UsersTab(onOpenChat = onOpenChat) },
        tab2 = {
            ProfileRoute(
                onSignOut = {
                    homeViewModel.onEvent(HomeUiEvent.SignOut)
                    onSignOut()
                },
            )
        },
    )
}

private val PreviewHomeUser = User(
    id = "1",
    username = "Jordan",
    emailId = "",
    timestamp = "0",
    imageUrl = "",
    bio = "",
    status = WhizzzStrings.Defaults.PRESENCE_ONLINE,
    searchKey = "j",
)

/**
 * Compose preview for [HomeMainLayout] on the Chats tab with sample [UserRow] items.
 * @author udit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = false, name = "Home · Chats tab")
@Composable
private fun HomeMainLayoutChatsPreview() {
    var tab by remember { mutableIntStateOf(0) }
    WhizzzTheme(darkTheme = true, dynamicColor = false, useBrandDarkColors = true) {
        HomeMainLayout(
            isOnline = true,
            selectedTabIndex = tab,
            onTabSelected = { tab = it },
            currentUser = PreviewHomeUser,
            onProfileHeaderClick = { tab = 2 },
            tab0 = {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(4) { UserRow(user = PreviewHomeUser, onClick = {}) }
                }
            },
            tab1 = { Box(Modifier.fillMaxSize()) },
            tab2 = { Box(Modifier.fillMaxSize()) },
        )
    }
}

/**
 * Compose preview for [HomeMainLayout] with the offline connectivity banner visible.
 * @author udit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = false, name = "Home · offline banner")
@Composable
private fun HomeMainLayoutOfflinePreview() {
    var tab by remember { mutableIntStateOf(0) }
    WhizzzTheme(darkTheme = true, dynamicColor = false, useBrandDarkColors = true) {
        HomeMainLayout(
            isOnline = false,
            selectedTabIndex = tab,
            onTabSelected = { tab = it },
            currentUser = PreviewHomeUser,
            onProfileHeaderClick = { tab = 2 },
            tab0 = {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item { UserRow(user = PreviewHomeUser, onClick = {}) }
                }
            },
            tab1 = { Box(Modifier.fillMaxSize()) },
            tab2 = { Box(Modifier.fillMaxSize()) },
        )
    }
}

/**
 * Compose preview for a single [UserRow] on the app background.
 * @author udit
 */
@Preview(showBackground = true, showSystemUi = false, name = "User row")
@Composable
private fun UserRowPreview() {
    WhizzzTheme(darkTheme = true, dynamicColor = false, useBrandDarkColors = true) {
        Box(Modifier.background(WhizzzScreenBackground)) {
            UserRow(user = PreviewHomeUser, onClick = {})
        }
    }
}

/**
 * Chats tab: empty state, error + retry, or a paged [LazyColumn] of partners with a load-more footer.
 *
 * Near-end scroll dispatches [ConversationsUiEvent.LoadMore] through [LazyListNearEndLoadEffect].
 *
 * @param onOpenChat Opens chat for the selected partner id.
 * @param viewModel MVI ConversationsUiState + [ConversationsViewModel.onEvent].
 * @author udit
 */
@Composable
private fun ConversationsTab(
    onOpenChat: (String) -> Unit,
    viewModel: ConversationsViewModel = koinViewModel(),
) {
    val ui by viewModel.state.collectAsStateWithLifecycle()
    val partners = ui.partners
    val listError = ui.listError
    val isInitialLoading = ui.isInitialLoading
    val isLoadingMore = ui.isLoadingMore
    val hasMore = ui.hasMore
    val listState = rememberLazyListState()
    LazyListNearEndLoadEffect(
        listState = listState,
        itemCount = partners.size,
        hasMore = hasMore,
        isLoadingMore = isLoadingMore,
        onLoadMore = { viewModel.onEvent(ConversationsUiEvent.LoadMore) },
    )
    when {
        isInitialLoading && partners.isEmpty() && listError == null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(WhizzzScreenBackground),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
        partners.isEmpty() -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(WhizzzScreenBackground)
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                listError?.let { err ->
                    Text(
                        text = err,
                        color = Color(0xFFFFAB91),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { viewModel.onEvent(ConversationsUiEvent.RetryList) }) {
                        Text(WhizzzStrings.Ui.TRY_AGAIN)
                    }
                    Spacer(Modifier.height(24.dp))
                }
                if (listError == null) {
                    Image(
                        painter = painterResource(UiR.drawable.ic_whizzz_empty_chats),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(330.dp)
                            .padding(horizontal = 15.dp, vertical = 20.dp),
                        contentScale = ContentScale.Fit,
                    )
                    Text(
                        text = WhizzzStrings.Ui.NO_CHATS_YET,
                        color = Color.White,
                        fontFamily = WhizzzPacificoFamily,
                        fontSize = 25.sp,
                        modifier = Modifier.padding(top = 30.dp),
                    )
                }
            }
        }
        else -> {
            Column(Modifier.fillMaxSize()) {
                listError?.let { err ->
                    Surface(color = Color(0xFF3E2723)) {
                        Column(Modifier.padding(10.dp)) {
                            Text(err, color = Color(0xFFFFCCBC), fontSize = 13.sp)
                            TextButton(onClick = { viewModel.onEvent(ConversationsUiEvent.RetryList) }) {
                                Text(WhizzzStrings.Ui.TRY_AGAIN, color = Color.White)
                            }
                        }
                    }
                }
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(WhizzzScreenBackground),
                ) {
                    items(partners, key = { it.id }) { user ->
                        UserRow(user = user, onClick = { onOpenChat(user.id) })
                    }
                    if (isLoadingMore || hasMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (isLoadingMore) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(28.dp),
                                        color = Color(0xFF63FFA3),
                                        strokeWidth = 2.dp,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Users tab: search field, error surface, paged [LazyColumn], and end-of-list loading footer.
 *
 * **Search / paging:** Backed by [UsersViewModel]; near-end scroll dispatches [UsersUiEvent.LoadMore] via
 * [LazyListNearEndLoadEffect].
 *
 * **IME:** [androidx.compose.ui.platform.LocalFocusManager] clears focus when the list scrolls so the keyboard
 * does not re-open during pagination. The column uses [whizzzImeInsetPadding] only (no `imeNestedScroll` on the list).
 *
 * @param onOpenChat Starts a chat with the tapped user id.
 * @param viewModel MVI [UsersUiState] + [UsersViewModel.onEvent].
 * @author udit
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UsersTab(
    onOpenChat: (String) -> Unit,
    viewModel: UsersViewModel = koinViewModel(),
) {
    val ui by viewModel.state.collectAsStateWithLifecycle()
    val users = ui.users
    val search = ui.searchFieldText
    val listError = ui.listError
    val isInitialLoading = ui.isInitialLoading
    val isLoadingMore = ui.isLoadingMore
    val hasMore = ui.hasMore
    val listState = rememberLazyListState()
    LazyListNearEndLoadEffect(
        listState = listState,
        itemCount = users.size,
        hasMore = hasMore,
        isLoadingMore = isLoadingMore,
        onLoadMore = { viewModel.onEvent(UsersUiEvent.LoadMore) },
    )
    val focusManager = LocalFocusManager.current
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { it }
            .collect { focusManager.clearFocus(force = true) }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WhizzzScreenBackground)
            .whizzzImeInsetPadding(),
    ) {
        listError?.let { err ->
            Surface(color = Color(0xFF3E2723)) {
                Column(Modifier.padding(10.dp)) {
                    Text(err, color = Color(0xFFFFCCBC), fontSize = 13.sp)
                    TextButton(onClick = { viewModel.onEvent(UsersUiEvent.RetryList) }) {
                        Text(WhizzzStrings.Ui.TRY_AGAIN, color = Color.White)
                    }
                }
            }
        }
        TextField(
            value = search,
            onValueChange = { viewModel.onEvent(UsersUiEvent.SearchFieldChanged(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, top = 5.dp, end = 15.dp),
            placeholder = {
                Text(WhizzzStrings.Ui.SEARCH, color = SearchHint, fontSize = 15.sp)
            },
            trailingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF3F3F3F))
            },
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color.Black,
                fontSize = 15.sp,
            ),
            shape = RoundedCornerShape(5.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                cursorColor = Color.Black,
                focusedTrailingIconColor = Color(0xFF3F3F3F),
                unfocusedTrailingIconColor = Color(0xFF3F3F3F),
            ),
        )
        when {
            isInitialLoading && users.isEmpty() && listError == null -> {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }
            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                ) {
                    items(users, key = { it.id }) { user ->
                        UserRow(user = user, onClick = { onOpenChat(user.id) })
                    }
                    if (isLoadingMore || hasMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                if (isLoadingMore) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(28.dp),
                                        color = Color(0xFF63FFA3),
                                        strokeWidth = 2.dp,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Fires [onLoadMore] once when the last visible item index is within three rows of the end, while [hasMore] is true
 * and a load is not already running.
 *
 * @param listState [LazyListState] of the column to observe.
 * @param itemCount Current row count (user rows only; footer items are excluded from this count by callers).
 * @param hasMore Whether the ViewModel reports additional server pages.
 * @param isLoadingMore Suppresses re-entry while a page request is in flight.
 * @param onLoadMore Typically the ViewModel’s `loadMore*` function.
 * @author udit
 */
@Composable
private fun LazyListNearEndLoadEffect(
    listState: LazyListState,
    itemCount: Int,
    hasMore: Boolean,
    isLoadingMore: Boolean,
    onLoadMore: () -> Unit,
) {
    LaunchedEffect(listState, itemCount, hasMore, isLoadingMore) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val lastVisible = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: return@snapshotFlow false
            itemCount > 0 && lastVisible >= itemCount - 3
        }
            .distinctUntilChanged()
            .filter { nearEnd -> nearEnd && hasMore && !isLoadingMore }
            .collect { onLoadMore() }
    }
}

/**
 * Single row for a [User] with avatar and display name.
 *
 * @param user User to display.
 * @param onClick Invoked when the row is tapped.
 * @author udit
 */
@Composable
private fun UserRow(
    user: User,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 1.dp),
        colors = CardDefaults.cardColors(containerColor = WhizzzScreenBackground),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WhizzzProfileAvatar(
                imageUrl = user.imageUrl,
                modifier = Modifier
                    .padding(start = 5.dp)
                    .size(52.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
            )
            Text(
                text = user.username,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, top = 8.dp, bottom = 8.dp),
            )
        }
    }
}
