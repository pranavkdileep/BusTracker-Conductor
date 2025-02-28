package com.pranavkd.bustracker_conductor.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier



@Composable
fun LoginScreen(modifier: Modifier = Modifier) {
    Scaffold (
        modifier = modifier
    ){ paddingValues ->
        Text(
            modifier = Modifier.padding(paddingValues),
            text = "Chat Screen"
        )
    }
}