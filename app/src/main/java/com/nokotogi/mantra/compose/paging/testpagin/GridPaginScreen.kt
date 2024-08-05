package com.nokotogi.mantra.compose.paging.testpagin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nokotogi.mantra.compose.paging.states.PageResult
import com.nokotogi.mantra.compose.paging.verticalgrid.MtrVerticalGridPagin

@Composable
fun GridPaginScreen(modifier: Modifier = Modifier, paginVM: PaginVM) {
    val paginState by paginVM.pagingState.collectAsStateWithLifecycle()

    MtrVerticalGridPagin(modifier = modifier, columns = GridCells.Fixed(2), onRequestLoadNewPage = {
        paginVM.loadPaging()
    }) {
        val dataset = paginState.dataset
        if (dataset.isNotEmpty()) {
            items(count = dataset.size, key = { dataset[it].id }) {
                GridPaginItem(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    paginItem = dataset[it]
                )
            }
        }

        if (paginState.pageResult is PageResult.Loading) {
            item(span = { GridItemSpan(2) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else if (paginState.pageResult is PageResult.Error) {
            item(span = { GridItemSpan(2) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = paginState.pageResult.error ?: "An error is occurred")
                    Spacer(modifier = Modifier.height(16.dp))
                    ElevatedButton(onClick = { paginVM.refresh() }) {
                        Text(text = "Refresh")
                    }
                }
            }
        }
    }
}

@Composable
fun GridPaginItem(modifier: Modifier = Modifier, paginItem: PaginItem) {
    Card(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(8.dp),
                text = paginItem.name,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}