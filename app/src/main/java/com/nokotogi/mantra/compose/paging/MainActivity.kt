package com.nokotogi.mantra.compose.paging

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.nokotogi.mantra.compose.paging.ui.theme.Mantra_compose_pagingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Mantra_compose_pagingTheme {
                App()
            }
        }
    }
}