package com.example.whizzz.feature.home

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
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.core.ui.R as UiR
import com.example.whizzz.core.ui.WhizzzPacificoFamily
import com.example.whizzz.core.ui.WhizzzProfileAvatar
import com.example.whizzz.core.ui.whizzzImeInsetPadding
import com.example.whizzz.core.ui.theme.WhizzzScreenBackground
import com.example.whizzz.domain.model.User
import com.example.whizzz.feature.profile.ProfileRoute

private val AppBarBlack = Color.Black
private val MutedTab = Color(0xFFAFACAC)
private val SearchHint = Color(0xFF797B7E)

/**
 * Main shell: tabs for chats, users, profile (original dark toolbar + list styling).
 *
 * @author udit
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeRoute(
    onSignOut: () -> Unit,
    onOpenChat: (String) -> Unit,
    homeViewModel: HomeViewModel = hiltViewModel(),
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> homeViewModel.setPresenceOnline()
                Lifecycle.Event.ON_PAUSE -> homeViewModel.setPresenceOffline()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        homeViewModel.registerPushToken()
    }

    val isOnline by homeViewModel.isOnline.collectAsStateWithLifecycle()

    var tabIndex by rememberSaveable { mutableIntStateOf(0) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = WhizzzScreenBackground,
        topBar = {
            TopAppBar(
                title = { Text(WhizzzStrings.Ui.APP_NAME, color = Color.White) },
                actions = {
                    TextButton(
                        onClick = {
                            homeViewModel.signOut()
                            onSignOut()
                        },
                    ) {
                        Text(WhizzzStrings.Ui.LOG_OUT, color = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppBarBlack,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White,
                ),
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(WhizzzScreenBackground),
        ) {
            TabRow(
                selectedTabIndex = tabIndex,
                containerColor = AppBarBlack,
                contentColor = Color.White,
                indicator = { positions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(positions[tabIndex]),
                        color = Color(0xFF63FFA3),
                    )
                },
                divider = {},
            ) {
                Tab(
                    selected = tabIndex == 0,
                    onClick = { tabIndex = 0 },
                    text = {
                        Text(
                            WhizzzStrings.Ui.CHATS,
                            color = if (tabIndex == 0) Color.White else MutedTab,
                        )
                    },
                )
                Tab(
                    selected = tabIndex == 1,
                    onClick = { tabIndex = 1 },
                    text = {
                        Text(
                            WhizzzStrings.Ui.USERS,
                            color = if (tabIndex == 1) Color.White else MutedTab,
                        )
                    },
                )
                Tab(
                    selected = tabIndex == 2,
                    onClick = { tabIndex = 2 },
                    text = {
                        Text(
                            WhizzzStrings.Ui.PROFILE,
                            color = if (tabIndex == 2) Color.White else MutedTab,
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
            when (tabIndex) {
                0 -> ConversationsTab(onOpenChat = onOpenChat)
                1 -> UsersTab(onOpenChat = onOpenChat)
                2 -> ProfileRoute()
            }
        }
    }
}

@Composable
private fun ConversationsTab(
    onOpenChat: (String) -> Unit,
    viewModel: ConversationsViewModel = hiltViewModel(),
) {
    val partners by viewModel.partners.collectAsStateWithLifecycle()
    val listError by viewModel.listError.collectAsStateWithLifecycle()
    if (partners.isEmpty()) {
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
                Button(onClick = { viewModel.retryPartnersList() }) {
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
    } else {
        Column(Modifier.fillMaxSize()) {
            listError?.let { err ->
                Surface(color = Color(0xFF3E2723)) {
                    Column(Modifier.padding(10.dp)) {
                        Text(err, color = Color(0xFFFFCCBC), fontSize = 13.sp)
                        TextButton(onClick = { viewModel.retryPartnersList() }) {
                            Text(WhizzzStrings.Ui.TRY_AGAIN, color = Color.White)
                        }
                    }
                }
            }
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(WhizzzScreenBackground),
            ) {
                items(partners, key = { it.id }) { user ->
                    UserRow(user = user, showOnline = true, onClick = { onOpenChat(user.id) })
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UsersTab(
    onOpenChat: (String) -> Unit,
    viewModel: UsersViewModel = hiltViewModel(),
) {
    val users by viewModel.users.collectAsStateWithLifecycle()
    val search by viewModel.searchQuery.collectAsStateWithLifecycle()
    val listError by viewModel.listError.collectAsStateWithLifecycle()
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
                    TextButton(onClick = { viewModel.retryUsersList() }) {
                        Text(WhizzzStrings.Ui.TRY_AGAIN, color = Color.White)
                    }
                }
            }
        }
        TextField(
            value = search,
            onValueChange = viewModel::onSearchChange,
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
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .imeNestedScroll()
                .padding(top = 10.dp),
        ) {
            items(users, key = { it.id }) { user ->
                UserRow(user = user, showOnline = false, onClick = { onOpenChat(user.id) })
            }
        }
    }
}

@Composable
private fun UserRow(
    user: User,
    showOnline: Boolean,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 1.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = WhizzzScreenBackground),
        elevation = CardDefaults.cardElevation(0.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .size(63.dp),
            ) {
                WhizzzProfileAvatar(
                    imageUrl = user.imageUrl,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                )
                if (showOnline) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(13.dp)
                            .clip(CircleShape)
                            .background(
                                if (user.isOnline) Color(0xFF63FFA3) else Color(0xFFC9CACD),
                            ),
                    )
                }
            }
            Text(
                text = user.username,
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp, top = 15.dp, bottom = 5.dp),
            )
        }
    }
}
