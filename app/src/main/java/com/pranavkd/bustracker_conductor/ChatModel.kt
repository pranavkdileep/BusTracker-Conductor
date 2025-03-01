package com.pranavkd.bustracker_conductor

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import org.json.JSONArray


data class Message(
    val id : String,
    val messageText : String,
    val sentAt : String,
    val direction : String,
    val from : String,
    val bookingId : String?
)

class ChatModel : ViewModel() {
    private val _messageList = mutableStateListOf<Message>()
    val apiHelper = ApiHelper()
    val messageList: List<Message> get() = _messageList

    fun sendMessage(messagetext:String, conductorId:String,bookingId: String?){
        if(bookingId!=null){
            apiHelper.sendMessageConductorToUser(
                conductorId = conductorId,
                messageText = messagetext,
                bookingId = bookingId,
                callback = { error, response ->
                    if (error != null) {
                       Log.e("ChatModel", "Error: $error")
                    } else {
                        Log.d("ChatModel", "Response: $response")
                    }
                }
            )
        }else{
            apiHelper.sendMessageConductorToAll(
                conductorId = conductorId,
                messageText = messagetext,
                callback = { error, response ->
                    if (error != null) {
                        Log.e("ChatModel", "Error: $error")
                    } else {
                        Log.d("ChatModel", "Response: $response")
                    }
                }
            )
        }
        receiveMessage(conductorId)
    }
    fun receiveMessage(conductorId: String){
        apiHelper.getMessages(
            conductorId = conductorId,
            callback = { error, response ->
                if (error != null) {
                    Log.e("ChatModel", "Error: $error")
                } else {
                    Log.d("ChatModel", "Response: $response")
                    syncMessages(response!!, _messageList)
                }
            }
        )
    }

    fun syncMessages(resJson: JSONArray, _messageList: SnapshotStateList<Message>) {
        val _messageListlen = _messageList.size
        val resJsonlen = resJson.length()
        if(_messageListlen == 0){
            for (i in 0 until resJsonlen) {
                val message = resJson.getJSONObject(i)
                if(message.getString("from") == "conductor") {
                    _messageList.add(
                        Message(
                            id = message.getString("id"),
                            messageText = message.getString("messageText"),
                            sentAt = message.getString("sentAt"),
                            direction = "send",
                            from = "conductor",
                            bookingId = null
                        )
                    )
                }
                else{
                    _messageList.add(
                        Message(
                            id = message.getString("id"),
                            messageText = message.getString("messageText"),
                            sentAt = message.getString("sentAt"),
                            direction = "incoming",
                            from = "user",
                            bookingId = message.getString("bookingId")
                        )
                    )
                }
            }
        }else{
            for (i in _messageListlen until resJsonlen) {
                val message = resJson.getJSONObject(i)
                if(message.getString("from") == "conductor") {
                    _messageList.add(
                        Message(
                            id = message.getString("id"),
                            messageText = message.getString("messageText"),
                            sentAt = message.getString("sentAt"),
                            direction = message.getString("direction"),
                            from = "conductor",
                            bookingId = null
                        )
                    )
                }
                else{
                    _messageList.add(
                        Message(
                            id = message.getString("id"),
                            messageText = message.getString("messageText"),
                            sentAt = message.getString("sentAt"),
                            direction = message.getString("direction"),
                            from = "user",
                            bookingId = message.getString("bookingId")
                        )
                    )
                }
            }
        }
    }

    fun realTime(conductorId: String){
        Thread{
            while(true){
                Thread.sleep(5000)
                receiveMessage(conductorId)
            }
        }.start()
    }
}