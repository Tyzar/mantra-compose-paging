package com.nokotogi.mantra.compose.paging.controller

import com.nokotogi.mantra.compose.paging.states.MtrPageState
import com.nokotogi.mantra.compose.paging.states.PageResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

typealias SetNextPageFunc = (pageKey: Int?, lastResultSize: Int) -> Int?

class MtrPageController<Error, Data>(
    state: MtrPageState<Error, Data>,
    private val setNextPageFunc: SetNextPageFunc,
    private val loadPageFunc: suspend (pageKey: Int?) -> PageResult<Error, List<Data>>
) {
    companion object {
        const val TAG = "TestPaging"
    }

    private val mState = MutableStateFlow(state)

    val state = mState.asStateFlow()

    private fun appendPage(pageData: List<Data>) {
        if (pageData.isEmpty()) {
            mState.value = mState.value.copy(
                pageResult = PageResult.Loaded(pageData),
                pageKey = setNextPageFunc(
                    mState.value.pageKey,
                    pageData.size
                )
            )
            return
        }

        val mDataset = mState.value.dataset.toMutableList()
        mDataset.addAll(pageData)

        mState.value = mState.value.copy(
            dataset = mDataset,
            pageResult = PageResult.Loaded(pageData),
            pageKey = setNextPageFunc(mState.value.pageKey, pageData.size)
        )
    }

    private fun notifyLoadError(error: Error) {
        mState.value = mState.value.copy(
            pageResult = PageResult.Error(error)
        )
    }

    suspend fun loadPage() {
        if (mState.value.pageResult is PageResult.Loading) {
            return
        }

        if (mState.value.isPageEnded) {
            return
        }

        mState.value = mState.value.copy(
            pageResult = PageResult.Loading()
        )
        when (val pageResult = loadPageFunc(mState.value.pageKey)) {
            is PageResult.Error -> notifyLoadError(pageResult.error)

            is PageResult.Loaded -> appendPage(pageResult.pageData)
            else -> {}
        }
    }

    suspend fun refreshPage() {
        mState.value = mState.value.copy(
            pageKey = null,
            isPageEnded = false,
            dataset = emptyList(),
            pageResult = PageResult.Initial()
        )

        loadPage()
    }

    fun setEndOfPage() {
        mState.value = mState.value.copy(
            isPageEnded = true
        )
    }
}

