package com.nokotogi.mantra.compose.paging.states

data class MtrPageState<Error, Data>(
    val pageSize: Int = 20,
    val pageKey: Int? = null,
    val isPageEnded: Boolean = false,
    val pageResult: PageResult<Error, List<Data>> = PageResult.Initial(),
    val dataset: List<Data> = emptyList()
)

sealed class PageResult<Error, Data>(
    open val error: Error? = null,
    open val pageData: Data? = null
) {
    class Initial<Error, Data> : PageResult<Error, Data>()
    class Loading<Error, Data> : PageResult<Error, Data>()

    data class Error<Error, Data>(override val error: Error) :
        PageResult<Error, Data>(error = error)

    class Loaded<Error, Data>(override val pageData: Data) :
        PageResult<Error, Data>(error = null, pageData = pageData)
}