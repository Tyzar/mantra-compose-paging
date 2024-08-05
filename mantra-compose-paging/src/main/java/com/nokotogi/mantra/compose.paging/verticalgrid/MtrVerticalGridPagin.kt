package com.nokotogi.mantra.compose.paging.verticalgrid

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun MtrVerticalGridPagin(
    modifier: Modifier = Modifier,
    columns: GridCells,
    lazyGridState: LazyGridState = rememberLazyGridState(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    onRequestLoadNewPage: () -> Unit,
    content: LazyGridScope.() -> Unit
) {
    val isScrollEnd by rememberGridScrollEnd(lazyGridState)
    if (isScrollEnd) {
        onRequestLoadNewPage()
    }

    LazyVerticalGrid(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = verticalArrangement,
        state = lazyGridState,
        columns = columns,
        content = content
    )
}

@Composable
fun rememberGridScrollEnd(lazyGridState: LazyGridState): State<Boolean> {
    return remember {
        derivedStateOf {
            val layoutInfo =
                lazyGridState.layoutInfo
            when {
                layoutInfo.visibleItemsInfo.isEmpty() -> false
                else -> {
                    val lastVisibleItem =
                        layoutInfo.visibleItemsInfo.last()
                    val viewportHeight =
                        layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset

                    (lastVisibleItem.index + 1 == layoutInfo.totalItemsCount &&
                            lastVisibleItem.offset.y + lastVisibleItem.size.height >= viewportHeight)
                }
            }
        }
    }
}