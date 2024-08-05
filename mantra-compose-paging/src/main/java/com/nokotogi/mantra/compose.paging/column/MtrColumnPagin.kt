package com.nokotogi.mantra.compose.paging.column

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun MtrColumnPagin(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    onRequestLoadNewPage: () -> Unit,
    content: LazyListScope.() -> Unit
) {
    val isScrollEnd by rememberColumnScrollEnd(lazyListState)
    if (isScrollEnd) {
        onRequestLoadNewPage()
    }

    LazyColumn(
        modifier = modifier,
        state = lazyListState,
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}

@Composable
fun rememberColumnScrollEnd(lazyListState: LazyListState): State<Boolean> {
    return remember {
        derivedStateOf {
            val layoutInfo =
                lazyListState.layoutInfo
            when {
                layoutInfo.visibleItemsInfo.isEmpty() -> false
                else -> {
                    val lastVisibleItem =
                        layoutInfo.visibleItemsInfo.last()
                    val viewportHeight =
                        layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset

                    (lastVisibleItem.index + 1 == layoutInfo.totalItemsCount &&
                            lastVisibleItem.offset + lastVisibleItem.size >= viewportHeight)
                }
            }
        }
    }
}