package com.pranavkd.bustracker_conductor

import android.content.SharedPreferences
import android.util.Log
import androidx.compose.runtime.MutableState
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.nio.charset.StandardCharsets

data class Conductor(val id: String, val BusId: String)

fun Authentication(sharedPreferences: SharedPreferences, condutorId: MutableState<String>, busId: MutableState<String>): Boolean {
    val token = sharedPreferences.getString("token", "")
    if (token != null) {
        val conductor = decodeJwt(token)
        if (conductor != null) {
            condutorId.value = conductor.id
            busId.value = conductor.BusId
            return true
        }
    }
    return false
}

fun decodeJwt(token: String): Conductor? {
    try {
        // The secret key used to sign the JWT (replace with your actual secret key)
        val secretKey = "your-secret-keygrbrgbgbrgbbtbtbewwfetghuyjiiop345643gfb".toByteArray(StandardCharsets.UTF_8)

        // Parse the JWT HS256 token
        val claims = Jwts.parserBuilder()
            .setSigningKey(Keys.hmacShaKeyFor(secretKey))
            .build()
            .parseClaimsJws(token)
            .body
        val conductor = Conductor(
            id = claims.get("conductorId", String::class.java),
            BusId = claims.get("busId", String::class.java)
        )
        return conductor


    } catch (e: Exception) {
        Log.e("JWT", "Error decoding JWT", e)
        return null
    }
}