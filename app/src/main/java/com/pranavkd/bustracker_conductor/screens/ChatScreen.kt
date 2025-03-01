package com.pranavkd.bustracker_conductor.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pranavkd.bustracker_conductor.ChatModel
import com.pranavkd.bustracker_conductor.Message

@Composable
fun ChatScreen(chatModel: ChatModel, Conductorid: String) {
    var replyshow = remember { mutableStateOf(false) }
    var replyTo by remember { mutableStateOf("") }
    var lock = true;
    if(lock){
        chatModel.receiveMessage(Conductorid)
        lock = false
    }
    chatModel.realTime(conductorId = Conductorid)
    Scaffold (

    ){ paddingValues ->
        val listState = rememberLazyListState()

        LaunchedEffect(chatModel.messageList.size) {
            listState.animateScrollToItem(chatModel.messageList.size )
        }
        Column(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                //reverseLayout = true
                state = listState
            ) {
                items(chatModel.messageList) { message ->
                    ChatMessageItem(message, Conductorid,onReply={
                        replyshow.value = true
                        replyTo = message.bookingId!!
                    })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            BottomSendView(
                replyshow = replyshow,
                replyTo = replyTo,
                onSendMessage = { message,isReply ->
                    if(isReply) {
                        chatModel.sendMessage(message, Conductorid, replyTo)
                    }else{
                        chatModel.sendMessage(message, Conductorid, null)
                    }
                },
                onReplyClose = {
                    replyshow.value = false
                    replyTo = ""
                },
            )
        }
    }
}

@Composable
fun ChatMessageItem(message: Message, Conductorid: String, onReply: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.direction == "send")
            Alignment.End else Alignment.Start
    ) {
        if (message.direction != "send") {
            Text(
                text = "User : ${message.bookingId}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
            )
        }
        else{
            Text(
                text = "You: $Conductorid",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, bottom = 4.dp, end = 16.dp)
            )
        }

        Row {
            Surface(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (message.direction == "send") 16.dp else 0.dp,
                    bottomEnd = if (message.direction == "send") 0.dp else 16.dp
                ),
                color = if (message.direction == "send")
                    MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = message.messageText,
                        color = if (message.direction == "send")
                            MaterialTheme.colorScheme.onPrimary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = message.sentAt,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (message.direction == "send")
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                        else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
            if(message.direction != "send"){
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Message Sent",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp).clickable { onReply() },
                )
            }
        }

    }
}

@Composable
fun BottomSendView(
    modifier: Modifier = Modifier,
    onSendMessage: (
        message: String,
            isReply : Boolean ,
            ) -> Unit,
    replyshow: MutableState<Boolean>,
    replyTo: String,
    onReplyClose: () -> Unit
) {
    var message by remember { mutableStateOf("") }
    Column {
        if (replyshow.value) {
            Row(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Divider(
                        modifier = Modifier
                            .width(2.dp)
                            .height(36.dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                RoundedCornerShape(2.dp)
                            )
                    )
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .weight(1f)
                    ) {
                        Text(
                            text = "Reply",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = replyTo,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
                IconButton(
                    onClick = { onReplyClose() },
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 0.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                placeholder = { Text("Type a message...") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (message.isNotBlank()) {
                                if(replyshow.value) {
                                    onSendMessage(message, true)
                                }else{
                                    onSendMessage(message, false)
                                }
                                message = ""
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Send,
                            contentDescription = "Send",
                            tint = if (message.isNotBlank())
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        }
    }
}

