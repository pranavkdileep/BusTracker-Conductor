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
    private val baseUrl = "https://bus-tracker-backend-one.vercel.app"
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
        val body = "{\n  \"sendertype\": \"conductor\",\n  \"senderconductorid\": \"$conductorId\",\n  \"receivertype\": \"all\",\n  \"receiverbookingid\": \"\",\n  \"messagetext\": \"$messageText\"\n}".toRequestBody(mediaType)
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
        val body = "{\n  \"sendertype\": \"conductor\",\n  \"senderconductorid\": \"$conductorId\",\n  \"receivertype\": \"user\",\n  \"receiverbookingid\": \"$bookingId\",\n  \"messagetext\": \"$messageText\"\n}".toRequestBody(mediaType)
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

    fun getBusInfo(conductorId: String,callback: (error: String?, response: Businfo?) -> Unit){
        val mediaType = "application/json".toMediaType()
        val body = "{\n  \"conductorId\": \"$conductorId\"\n}".toRequestBody(mediaType)
        val request = Request.Builder()
            .url("$baseUrl/api/client/getBusInfo")
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
                            val jsonObject = JSONObject(responseBody).getJSONObject("busData")
                            val busId = jsonObject.getString("busId")
                            val busName = jsonObject.getString("busName")
                            val state = jsonObject.getString("state")
                            val routes = jsonObject.getJSONArray("routes")
                            val routesList = mutableListOf<Routes>()
                            for (i in 0 until routes.length()){
                                val route = routes.getJSONObject(i)
                                val name = route.getString("name")
                                val iscompleted = route.getBoolean("completed")
                                routesList.add(Routes(name,iscompleted))
                            }
                            val businfo = Businfo(busId,busName,state,routesList)
                            callback(null, businfo)
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

    fun getPassengers(conductorId: String,callback: (error: String?, response: List<passenger>?) -> Unit){
        val mediaType = "application/json".toMediaType()
        val body = "{\n  \"conductorId\": \"$conductorId\"\n}".toRequestBody(mediaType)
        val request = Request.Builder()
            .url("$baseUrl/api/client/getPassengerInfo")
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
                            val jsonObject = JSONObject(responseBody).getJSONArray("passengerData")
                            val passengers = mutableListOf<passenger>()
                            for (i in 0 until jsonObject.length()){
                                val passenger = jsonObject.getJSONObject(i)
                                val bookingId = passenger.getString("bookingId")
                                val fullname = passenger.getString("fullname")
                                val email = passenger.getString("email")
                                val phone = passenger.getString("phone")
                                val gender = passenger.getString("gender")
                                val source = passenger.getString("source")
                                val destination = passenger.getString("destination")
                                passengers.add(passenger(bookingId,fullname,email,phone,gender,source,destination))
                            }
                            callback(null, passengers)
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

    fun changeState(conductorId: String, newState: String, callback: (error: String?) -> Unit){
        val mediaType = "application/json".toMediaType()
        val body = "{\n  \"conductorId\": \"$conductorId\",\n  \"newState\": \"$newState\"\n}".toRequestBody(mediaType)
        val request = Request.Builder()
            .url("$baseUrl/api/client/changeState")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()
        try{
            client.newCall(request).enqueue(object : okhttp3.Callback{
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("ApiHelper",e.message.toString())
                    callback(e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful) {
                        if(responseBody!=null){
                            Log.d("ApiHelper",responseBody)
                            callback(null)
                        }
                    } else {
                        callback(responseBody)
                    }
                }

            })
        }catch (e: Exception){
            Log.d("ApiHelper",e.message.toString())
            callback(e.message)
        }
    }

    fun changeNoOfSopes(conductorId: String, newNoOfStops:Int, callback: (error: String?) -> Unit){
        val mediaType = "application/json".toMediaType()
        val body = "{\n  \"conductorId\": \"$conductorId\",\n  \"newNoOfStops\": $newNoOfStops\n}".toRequestBody(mediaType)
        val request = Request.Builder()
            .url("$baseUrl/api/client/changeNoOfSopes")
            .post(body)
            .addHeader("Content-Type", "application/json")
            .build()
        try{
            client.newCall(request).enqueue(object : okhttp3.Callback{
                override fun onFailure(call: Call, e: IOException) {
                    Log.d("ApiHelper",e.message.toString())
                    callback(e.message)
                }

                override fun onResponse(call: Call, response: Response) {
                    val responseBody = response.body?.string()
                    if (response.isSuccessful) {
                        if(responseBody!=null){
                            Log.d("ApiHelper",responseBody)
                            callback(null)
                        }
                    } else {
                        Log.d("ApiHelper",responseBody.toString())
                        callback(responseBody)
                    }
                }

            })
        }catch (e: Exception){
            Log.d("ApiHelper",e.message.toString())
            callback(e.message)
        }
    }
}