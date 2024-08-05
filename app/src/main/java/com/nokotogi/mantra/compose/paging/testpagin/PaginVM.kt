package com.nokotogi.mantra.compose.paging.testpagin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nokotogi.mantra.compose.paging.controller.MtrPageController
import com.nokotogi.mantra.compose.paging.states.MtrPageState
import com.nokotogi.mantra.compose.paging.states.PageResult
import com.nokotogi.mantra.either.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


data class PaginItem(val id: Int, val name: String, val desc: String)

class PaginVM : ViewModel() {
    companion object {
        const val TAG = "TestPaging"
    }

    private val pageController =
        MtrPageController(
            MtrPageState(),
            setNextPageFunc = { currKey, pageSize, _ ->
                (currKey ?: 1) + pageSize
            }) { pageKey, pageSize ->
            withContext(Dispatchers.IO) {
                delay(3000)
                return@withContext getPaginatedData((pageKey ?: 1), pageSize).fold(
                    onLeft = {
                        PageResult.Error("Failed to get pagin data")
                    }, onRight = {
                        PageResult.Loaded(it)
                    })
            }
        }

    val pagingState = pageController.state

    init {
        loadPaging()
    }

    fun loadPaging() {
        viewModelScope.launch {
            pageController.loadPage()
        }
    }

    fun refresh() {
        viewModelScope.launch {
            pageController.refreshPage()
        }
    }
}

fun getPaginatedData(pageKey: Int, resultSize: Int): Either<Exception, List<PaginItem>> {
    if (pageKey > 60) {
        //test set pagin error
        return getPaginatedDataError(pageKey, resultSize)
    }

    val result = mutableListOf<PaginItem>()
    for (i in pageKey..<pageKey + resultSize) {
        result.add(PaginItem(i, "Name $i", "This is description of item with name $i"))
    }

    return Either.Right(result)
}

fun getPaginatedDataError(pageKey: Int, resultSize: Int): Either<Exception, List<PaginItem>> {
    return Either.Left(Exception("Failed to get paging data"))
}