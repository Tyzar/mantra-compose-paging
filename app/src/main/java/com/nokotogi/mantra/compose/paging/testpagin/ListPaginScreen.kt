package com.nokotogi.mantra.compose.paging.testpagin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nokotogi.mantra.compose.paging.column.MtrColumnPagin
import com.nokotogi.mantra.compose.paging.states.PageResult

@Composable
fun ListPaginScreen(modifier: Modifier = Modifier, paginVM: PaginVM) {
    val pagingState by paginVM.pagingState.collectAsStateWithLifecycle()

    Scaffold { padding ->
        MtrColumnPagin(
            modifier.padding(
                padding
            ),
            verticalArrangement = Arrangement.spacedBy(
                16.dp
            ),
            onRequestLoadNewPage = {
                paginVM.loadPaging()
            }
        ) {
            item {
                ElevatedButton(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    onClick = { paginVM.refresh() }) {
                    Text(text = "Refresh")
                }
            }

            if (pagingState.dataset.isNotEmpty()) {
                items(
                    count = pagingState.dataset.size,
                    key = { idx -> pagingState.dataset[idx].id }) {
                    PageItem(modifier = Modifier.fillMaxWidth(), item = pagingState.dataset[it])
                }
            }

            if (pagingState.pageResult is PageResult.Loading) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (pagingState.pageResult is PageResult.Error) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "An error occurred")
                        Spacer(modifier = Modifier.height(16.dp))
                        ElevatedButton(
                            onClick = { paginVM.loadPaging() },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(text = "Reload")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PageItem(modifier: Modifier = Modifier, item: PaginItem) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            item.name,
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(
            modifier = Modifier.height(
                16.dp
            )
        )
        Text(
            item.desc,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}