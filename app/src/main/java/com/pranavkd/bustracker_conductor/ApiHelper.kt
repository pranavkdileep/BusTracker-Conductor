package com.pranavkd.bustracker_conductor

import android.util.Log
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ApiHelper {
    private val baseUrl = "http://207.211.188.157:4578"
    private val client = OkHttpClient()

    fun login(conductorId: String, password: String,callback : (error: String?, response: String?) -> Unit) {
        val contentType = "application/json".toMediaType()
        val body =
            "{\n  \"conductorId\": \"$conductorId\",\n  \"password\": \"$password\"\n}".toRequestBody(
                contentType
            )
        val request = Request.Builder()
            .url("$baseUrl/api/admin/conductor/login")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()
        try{
            client.newCall(request).enqueue(object : okhttp3.Callback{
                override fun onFailure(call: Call, e: IOException) {
                    callback(e.message, null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful) {
                        if(responseBody!=null){
                            val jsonObject = JSONObject(responseBody)
                            val token = jsonObject.getString("token")
                            callback(null, token)
                        }
                    } else {
                        callback(responseBody, null)
                    }
                }

            })
        }catch (e: Exception){
            callback(e.message, null)
        }
    }

    fun sendMessageConductorToAll(conductorId: String, messageText:String,callback: (error: String?, response: JSONObject?) -> Unit){

        val mediaType = "application/json".toMediaType()
        val body = "{\n  \"senderType\": \"conductor\",\n  \"senderConductorId\": \"$conductorId\",\n  \"receiverType\": \"all\",\n  \"receiverBookingId\": \"\",\n  \"messageText\": \"$messageText\"\n}".toRequestBody(mediaType)
        val request = Request.Builder()
            .url("$baseUrl/api/admin/chat")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()
        try{
            client.newCall(request).enqueue(object : okhttp3.Callback{
                override fun onFailure(call: Call, e: IOException) {
                    callback(e.message, null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful) {
                        if(responseBody!=null){
                            callback(null, JSONObject(responseBody))
                        }
                    } else {
                        callback(responseBody, null)
                    }
                }

            })
        }catch (e: Exception){
            callback(e.message, null)
        }
    }

    fun sendMessageConductorToUser(conductorId: String, messageText:String,bookingId:String,callback: (error: String?, response: JSONObject?) -> Unit){
        val mediaType = "application/json".toMediaType()
        val body = "{\n  \"senderType\": \"conductor\",\n  \"senderConductorId\": \"$conductorId\",\n  \"receiverType\": \"user\",\n  \"receiverBookingId\": \"$bookingId\",\n  \"messageText\": \"$messageText\"\n}".toRequestBody(mediaType)
        val request = Request.Builder()
            .url("$baseUrl/api/admin/chat")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()
        try{
            client.newCall(request).enqueue(object : okhttp3.Callback{
                override fun onFailure(call: Call, e: IOException) {
                    callback(e.message, null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful) {
                        if(responseBody!=null){
                            callback(null, JSONObject(responseBody))
                        }
                    } else {
                        callback(responseBody, null)
                    }
                }

            })
        }catch (e: Exception){
            callback(e.message, null)
        }
    }

    fun getMessages(conductorId: String, callback: (error: String?, response: JSONArray?) -> Unit){
        val mediaType = "application/json".toMediaType()
        val body = "{\n  \"conductorId\": \"$conductorId\"\n}\n\n\n\n\n".toRequestBody(mediaType)
        val request = Request.Builder()
            .url("$baseUrl/api/admin/getChat")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()
        try{
            client.newCall(request).enqueue(object : okhttp3.Callback{
                override fun onFailure(call: Call, e: IOException) {
                    callback(e.message, null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful) {
                        if(responseBody!=null){
                            callback(null, JSONArray(responseBody))
                        }
                    } else {
                        callback(responseBody, null)
                    }
                }

            })
        }catch (e: Exception){
            callback(e.message, null)
        }
    }
}