package com.example.whizzz.feature.chat.ui

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whizzz.core.ui.theme.WhizzzTheme
import com.example.whizzz.domain.model.ChatMessage
import com.example.whizzz.domain.model.User
import com.example.whizzz.feature.chat.presentation.ConversationUiState
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.core.ui.WhizzzProfileAvatar
import com.example.whizzz.core.ui.whizzzKeyboardInsetPadding
import com.example.whizzz.core.ui.theme.WhizzzScreenBackground
import com.example.whizzz.feature.chat.presentation.ConversationUiEvent
import com.example.whizzz.feature.chat.presentation.ConversationViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
private val ChatBarBlack = Color.Black

/**
 * Incoming message bubble fill.
 * @author udit
 */
@SuppressLint("InvalidColorHexValue")
private val BubbleReceiver = Color(0xFFBA535353)
private val BubbleSender = Color(0xFF50DA88)
private val HintCompose = Color(0xFFAAA1A1)

/**
 * Stateless 1:1 conversation UI: peer top bar (avatar, name, online dot), message list, composer, and stream error banner.
 *
 * @param state MVI [ConversationUiState] (messages, draft, peer, errors).
 * @param listState [LazyListState] for the message [LazyColumn].
 * @param snackBarHostState Snackbar host for send errors and similar.
 * @param composerInteraction [MutableInteractionSource] for the message field (focus / IME behavior).
 * @param onEvent Dispatches [ConversationUiEvent] to the ViewModel.
 * @param onBack Navigate up.
 * @param onOpenPeerProfile Open peer profile (avatar / title region).
 * @author udit
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
internal fun ConversationScreenContent(
    state: ConversationUiState,
    listState: LazyListState,
    snackBarHostState: SnackbarHostState,
    composerInteraction: MutableInteractionSource,
    onEvent: (ConversationUiEvent) -> Unit,
    onBack: () -> Unit,
    onOpenPeerProfile: () -> Unit,
) {
    val messages = state.messages
    val peer = state.peerUser
    val draft = state.draft
    val myId = state.myUserId
    val streamError = state.streamError

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = WhizzzScreenBackground,
        snackbarHost = { SnackbarHost(snackBarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(end = 7.dp)
                                .clickable(
                                    enabled = peer != null,
                                    onClick = onOpenPeerProfile,
                                ),
                        ) {
                            WhizzzProfileAvatar(
                                imageUrl = peer?.imageUrl.orEmpty(),
                                modifier = Modifier
                                    .padding(5.dp)
                                    .size(50.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(bottom = 5.dp)
                                    .size(13.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (peer?.isOnline == true) Color(0xFF63FFA3) else Color(0xFFC9CACD),
                                    ),
                            )
                        }
                        Text(
                            text = peer?.username ?: WhizzzStrings.Ui.CHATS,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = WhizzzStrings.Ui.BACK,
                            tint = Color.White,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ChatBarBlack,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                ),
            )
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .whizzzKeyboardInsetPadding()
                    .padding(2.dp)
                    .background(Color.Black, RoundedCornerShape(27.dp))
                    .padding(5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextField(
                    value = draft,
                    onValueChange = { onEvent(ConversationUiEvent.DraftChanged(it)) },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp, end = 4.dp),
                    interactionSource = composerInteraction,
                    placeholder = {
                        Text(WhizzzStrings.Ui.TYPE_MESSAGE, color = HintCompose, fontSize = 18.sp)
                    },
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = { onEvent(ConversationUiEvent.Send) },
                    ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = Color.White,
                        fontSize = 18.sp,
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color.White,
                    ),
                )
                IconButton(
                    onClick = { onEvent(ConversationUiEvent.Send) },
                    modifier = Modifier.padding(4.dp),
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = WhizzzStrings.Ui.SEND,
                        tint = Color.White,
                    )
                }
            }
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            streamError?.let { err ->
                Surface(color = Color(0xFF3E2723)) {
                    Column(Modifier.padding(10.dp)) {
                        Text(
                            text = err,
                            color = Color(0xFFFFCCBC),
                            fontSize = 13.sp,
                        )
                        Row(Modifier.padding(top = 4.dp)) {
                            TextButton(
                                onClick = { onEvent(ConversationUiEvent.RetryStreams) },
                            ) {
                                Text(WhizzzStrings.Ui.TRY_AGAIN, color = Color.White)
                            }
                            TextButton(onClick = { onEvent(ConversationUiEvent.DismissStreamError) }) {
                                Text(WhizzzStrings.Ui.CLOSE, color = Color.White)
                            }
                        }
                    }
                }
            }
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(WhizzzScreenBackground)
                    .imeNestedScroll(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp),
            ) {
                items(messages, key = { it.pushId ?: "${it.timestamp}_${it.senderId}" }) { msg ->
                    MessageBubble(message = msg, myUserId = myId)
                }
            }
        }
    }
}

/**
 * Conversation route: [ConversationViewModel] (MVI), scroll/snackbar side effects, and [ConversationScreenContent].
 *
 * @param onBack Navigate up.
 * @param onOpenPeerProfile Navigate to peer profile for the given user id.
 * @param viewModel MVI [ConversationViewModel] (peer id from navigation).
 * @author udit
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ConversationRoute(
    onBack: () -> Unit,
    onOpenPeerProfile: (String) -> Unit,
    viewModel: ConversationViewModel = koinViewModel(),
) {
    val s by viewModel.state.collectAsStateWithLifecycle()
    val messages = s.messages
    val sendMessageError = s.sendError
    val snackBarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val imeBottomPx = WindowInsets.ime.getBottom(density)
    val composerInteraction = remember { MutableInteractionSource() }
    val composerFocused by composerInteraction.collectIsFocusedAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onEvent(ConversationUiEvent.MarkPeerSeen)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {         lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // ON_RESUME can run before myUserId is in state; mark seen again once uid is known.
    LaunchedEffect(s.myUserId, viewModel.peerId) {
        if (s.myUserId != null) {
            viewModel.onEvent(ConversationUiEvent.MarkPeerSeen)
        }
    }

    val lastMessageKey = messages.lastOrNull()?.let { it.pushId ?: "${it.timestamp}_${it.senderId}" }
    LaunchedEffect(messages.size, lastMessageKey) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }
    LaunchedEffect(imeBottomPx) {
        if (imeBottomPx > 0 && messages.isNotEmpty()) {
            delay(120)
            listState.animateScrollToItem(messages.lastIndex)
        }
    }
    LaunchedEffect(composerFocused) {
        if (composerFocused && messages.isNotEmpty()) {
            delay(150)
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    LaunchedEffect(sendMessageError) {
        val msg = sendMessageError ?: return@LaunchedEffect
        snackBarHostState.showSnackbar(
            message = msg,
            duration = SnackbarDuration.Long,
        )
        viewModel.onEvent(ConversationUiEvent.ClearSendError)
    }

    ConversationScreenContent(
        state = s,
        listState = listState,
        snackBarHostState = snackBarHostState,
        composerInteraction = composerInteraction,
        onEvent = viewModel::onEvent,
        onBack = onBack,
        onOpenPeerProfile = { onOpenPeerProfile(viewModel.peerId) },
    )
}

/**
 * Compose preview for ConversationScreenContent with fake peer, messages, and draft.
 * @author udit
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Preview(showBackground = true, showSystemUi = false)
@Composable
private fun ConversationScreenPreview() {
    val peer = User(
        id = "peer",
        username = "Alex",
        emailId = "",
        timestamp = "0",
        imageUrl = "",
        bio = "",
        status = WhizzzStrings.Defaults.PRESENCE_ONLINE,
        searchKey = "alex",
    )
    val now = System.currentTimeMillis().toString()
    val messages = listOf(
        ChatMessage("1", "peer", "me", "Hey!", now, true),
        ChatMessage("2", "me", "peer", "Hi there — preview message.", now, false),
    )
    val state = ConversationUiState(
        myUserId = "me",
        messages = messages,
        peerUser = peer,
        draft = "Type here…",
        streamError = null,
        sendError = null,
    )
    WhizzzTheme(darkTheme = true, dynamicColor = false, useBrandDarkColors = true) {
        ConversationScreenContent(
            state = state,
            listState = rememberLazyListState(),
            snackBarHostState = remember { SnackbarHostState() },
            composerInteraction = remember { MutableInteractionSource() },
            onEvent = {},
            onBack = {},
            onOpenPeerProfile = {},
        )
    }
}

/**
 * Renders a single [ChatMessage] bubble aligned by sender with optional seen label.
 *
 * @param message Message model to display.
 * @param myUserId Current user id for left/right alignment; null treats as non-mine.
 * @author udit
 */
@Composable
private fun MessageBubble(
    message: ChatMessage,
    myUserId: String?,
) {
    val mine = message.senderId == myUserId
    val time = formatChatTime(message.timestamp)
    val receiverShape = RoundedCornerShape(
        topStart = 0.dp,
        topEnd = 30.dp,
        bottomEnd = 30.dp,
        bottomStart = 30.dp,
    )
    val senderShape = RoundedCornerShape(
        topStart = 30.dp,
        topEnd = 0.dp,
        bottomEnd = 30.dp,
        bottomStart = 30.dp,
    )
    if (mine) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
            ) {
                Text(
                    text = time,
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(end = 7.dp),
                )
                Text(
                    text = message.message,
                    color = Color.White,
                    fontSize = 18.sp,
                    modifier = Modifier
                        .background(BubbleSender, senderShape)
                        .padding(14.dp),
                )
            }
            if (message.seen) {
                Text(
                    text = WhizzzStrings.Ui.SEEN,
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .padding(end = 12.dp, bottom = 4.dp)
                        .align(Alignment.End),
                )
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
        ) {
            Text(
                text = message.message,
                color = Color.White,
                fontSize = 18.sp,
                modifier = Modifier
                    .background(BubbleReceiver, receiverShape)
                    .padding(14.dp),
            )
            Text(
                text = time,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 7.dp),
            )
        }
    }
}

/**
 * Formats a millisecond string into a short local time (for example "3:45 PM").
 *
 * @param timestamp Epoch millis as string from the backend.
 * @return Formatted time or empty string if parsing fails.
 * @author udit
 */
private fun formatChatTime(timestamp: String): String {
    val ms = timestamp.toLongOrNull() ?: return ""
    return SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(ms))
}
