package com.nokotogi.mantra.compose.paging.testpagin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun PaginExampleBottomNav(
    modifier: Modifier = Modifier,
    navItems: List<NavItem>,
    onNavItemClicked: (navRoute: String) -> Unit
) {
    var activeRoute by rememberSaveable {
        mutableStateOf(navItems[0].route)
    }

    BottomAppBar(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    ) {
        LazyRow(modifier = modifier, horizontalArrangement = Arrangement.SpaceAround) {
            items(count = navItems.size, key = { navItems[it].route }) {
                NavIcon(
                    navItem = navItems[it],
                    isActive = activeRoute == navItems[it].route,
                    onClick = {
                        activeRoute = navItems[it].route
                        onNavItemClicked(navItems[it].route)
                    }
                )
            }
        }
    }
}

data class NavItem(
    val route: String,
    val label: String,
    val activeIcon: ImageVector,
    val inactiveIcon: ImageVector
)

@Composable
fun NavIcon(
    modifier: Modifier = Modifier,
    isActive: Boolean = false,
    navItem: NavItem,
    onClick: () -> Unit
) {
    Column(modifier = modifier
        .padding(8.dp)
        .clickable {
            onClick()
        }, horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            tint = if (isActive) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface,
            imageVector = if (isActive) navItem.activeIcon else navItem.inactiveIcon,
            contentDescription = navItem.label
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = navItem.label, style = MaterialTheme.typography.labelMedium.copy(
                color = if (isActive) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface
            )
        )
    }
}