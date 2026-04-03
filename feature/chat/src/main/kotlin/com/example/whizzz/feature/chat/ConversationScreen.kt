package com.example.whizzz.feature.chat

import android.annotation.SuppressLint
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.whizzz.core.strings.WhizzzStrings
import com.example.whizzz.core.ui.WhizzzProfileAvatar
import com.example.whizzz.core.ui.whizzzKeyboardInsetPadding
import com.example.whizzz.core.ui.theme.WhizzzScreenBackground
import com.example.whizzz.domain.model.ChatMessage
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ChatBarBlack = Color.Black
@SuppressLint("InvalidColorHexValue")
private val BubbleReceiver = Color(0xFFBA535353)
private val BubbleSender = Color(0xFF50DA88)
private val HintCompose = Color(0xFFAAA1A1)

/**
 * Single chat thread UI (original toolbar + bubbles + bottom bar).
 *
 * Composer lives in Scaffold.bottomBar so IME insets reliably keep the field above the keyboard.
 *
 * @author udit
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ConversationRoute(
    onBack: () -> Unit,
    viewModel: ConversationViewModel = hiltViewModel(),
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val peer by viewModel.peerUser.collectAsStateWithLifecycle()
    val draft by viewModel.draft.collectAsStateWithLifecycle()
    val myId by viewModel.myUserId.collectAsStateWithLifecycle()
    val streamError by viewModel.streamError.collectAsStateWithLifecycle()
    val sendMessageError by viewModel.sendMessageError.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val density = LocalDensity.current
    val imeBottomPx = WindowInsets.ime.getBottom(density)
    val composerInteraction = remember { MutableInteractionSource() }
    val composerFocused by composerInteraction.collectIsFocusedAsState()

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, viewModel) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    viewModel.setPresenceOnline()
                    viewModel.markPeerMessagesSeen()
                }
                Lifecycle.Event.ON_PAUSE -> viewModel.setPresenceOffline()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
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
        snackbarHostState.showSnackbar(
            message = msg,
            duration = SnackbarDuration.Long,
        )
        viewModel.clearSendMessageError()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = WhizzzScreenBackground,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 4.dp),
                    ) {
                        Box(modifier = Modifier.padding(end = 7.dp)) {
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
                    onValueChange = viewModel::onDraftChange,
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
                        onSend = { viewModel.send() },
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
                    onClick = { viewModel.send() },
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
                                onClick = { viewModel.retryStreams() },
                            ) {
                                Text(WhizzzStrings.Ui.TRY_AGAIN, color = Color.White)
                            }
                            TextButton(onClick = { viewModel.dismissStreamError() }) {
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

@Composable
private fun MessageBubble(message: ChatMessage, myUserId: String?) {
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

private fun formatChatTime(timestamp: String): String {
    val ms = timestamp.toLongOrNull() ?: return ""
    return SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(ms))
}
