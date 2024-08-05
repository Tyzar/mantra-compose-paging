package com.nokotogi.mantra.compose.paging

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nokotogi.mantra.compose.paging.testpagin.PaginExampleScreen

const val routePaginExamples = "/pagin-examples"
const val routeColumnPagination = "/list-pagination"
const val routeVerticalGridPagination = "/vertical-grid-pagination"

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = routePaginExamples) {
        composable(route = routePaginExamples) {
            PaginExampleScreen(modifier = Modifier.fillMaxSize())
        }
    }
}