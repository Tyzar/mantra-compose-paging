package com.nokotogi.mantra.compose.paging.testpagin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nokotogi.mantra.compose.paging.routeColumnPagination
import com.nokotogi.mantra.compose.paging.routeVerticalGridPagination

val navItems = listOf(
    NavItem(
        route = routeColumnPagination,
        label = "Column",
        inactiveIcon = Icons.AutoMirrored.Outlined.List,
        activeIcon = Icons.AutoMirrored.Filled.List,
    ),
    NavItem(
        route = routeVerticalGridPagination,
        label = "VGrid",
        inactiveIcon = Icons.Outlined.DateRange,
        activeIcon = Icons.Filled.DateRange,
    )
)

@Composable
fun PaginExampleScreen(
    modifier: Modifier = Modifier
) {
    val paginNavController = rememberNavController()
    Scaffold(bottomBar = {
        PaginExampleBottomNav(
            modifier = Modifier.fillMaxWidth(),
            navItems = navItems,
            onNavItemClicked = { navRoute ->
                paginNavController.navigate(navRoute)
            })
    }) { padding ->
        NavHost(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            navController = paginNavController,
            startDestination = routeColumnPagination
        ) {
            composable(route = routeColumnPagination) {
                ListPaginScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    paginVM = viewModel()
                )
            }

            composable(route = routeVerticalGridPagination) {
                GridPaginScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    paginVM = viewModel()
                )
            }
        }
    }
}