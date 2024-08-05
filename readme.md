# Mantra Compose Paging

Reduces boilerplate when writing pagination code on Jetpack Compose components.
Currently, this library supports pagination on LazyColumn and LazyVerticalGrid.

## Installing

In `setting.gradle.kts`, add repository at `dependencyResolutionManagement.repositories`

```
  maven {
            url = uri("https://jitpack.io")            
        }

```

or

```
maven {
            url = uri("https://jitpack.io")
            content {
                includeGroup("com.github.Tyzar")
            }
        }

```

Makesure that kotlin jvmTarget set with JDK version 8 or higher

```
compileOptions {
        ...
        targetCompatibility =
            JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

```

In module level `build.gradle.kts`, add dependency as below

```
implementation(com.github.Tyzar.mantra-compose-paging:1.0.0-alpha)

```

## Usage

This paging library consists of paging components, paging controller, and paging state.

### Paging Components

Paging components can be used inside composable function and triggers request new page event that
can be handled by
`onRequestLoadNewPage` callback. New page event triggered when paging component scroll reaches
bottom and last data position.

### MtrColumnPagin

This component enables pagination on LazyColumn.

```
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
            //describe your item components here...
        }

```

### MtrVerticalGridPagin

This component enables pagination on LazyVerticalGrid.

```
MtrVerticalGridPagin(modifier = modifier, columns = GridCells.Fixed(2), onRequestLoadNewPage = {
        paginVM.loadPaging()
    }) {
          //describe your item components here...
       }

```

### Paging State

Paging data like current key, max data per page, or result of last page fetch are represented
by `MtrPageState`.
This state class only can be changed by using page controller. `MtrPageState` has two generic type
for `Error` and `Data`.

For example `MtrPageState<String,Book>` will return fetching error result as String and dataset are
stored as `List<Book>`.
For another properties at `MtrPageState`, explained as below.

`pageSize`: how many data can be fetched at one page request.

`pageKey`: Current key of the page.

`isPageEnded`: Indicates if last page has been loaded.

`pageResult`: The result status of currently or last fetching page process. Represented
by `PageResult<Error,Data>` The result can
be `Initial`, `Loading`, `Error`, and `Loaded`.

`dataset`: The dataset of paging. Use this dataset to describe item components in paging component.

### Paging Controller

To gluing paging component and paging state, a paging controller is needed. Controller expose some
functionality to modify `MtrPageState`.
Paging controller emits the `MtrPageState`'value changed using `StateFlow`.

#### Creating Paging Controller

Create paging controller by instantiating class `MtrPageController`.

```
MtrPageController(
            //initial paging state
            MtrPageState(), 
            
            //set how next key is provided
            setNextPageFunc = { currKey, pageSize, _ ->
                (currKey ?: 1) + pageSize
            }) {
                //set how fetch page data 
                pageKey, pageSize ->
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

```

Below are the explanations of each constructor parameter of `MtrPageController`

`state`: initial `MtrPageState` for this controller.

`setNextPageFunc`: This function required by controller in order to set current page key to the next
page key. Each pagination model has its own quirks on how to decide next page key, sometimes we only
append it by 1, sometimes we use the offset that obtained by current size of loaded dataset.

`loadPageFunc`: This function must be provided by result of fetching or loading a page data. Mostly,
you must transform the fetch result type to `PageResult<Error, List<Data>>` type.

#### Modifying Page State

To modify page state, `MtrPageController` provides these function.

`loadPage`  : Suspend function to load page data with current `pageKey`. This function calls
function `loadPageFunc`
that
provided at controller constructor.

`refreshPage`: Suspend function to refresh pagination and set `pageKey` to its initial
value (`null`). It will
removes all
loaded pages and reloads dataset from initial `pageKey`

Then all MtrPageState changes will be emitted by `StateFlow`.

Below is example of calling those
function in `ViewModels`

```
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

```

#### Reading Page State

Paging state can be obtained from property `state` of `MtrPageController`. Collect from it to be
able notified about `MtrPageState` changes.
On composable function, it is recommended to convert `StateFlow` to `State` using extension
function `collectAsStateWithLifecycle` from `androidx.lifecycle.lifecycle-runtime-compose` library.

```
val pagingState by controller.state.collectAsStateWithLifecycle()

```

##### Using `PageResult` Info

`PageResult` from `MtrPageState` can be used to display fetch process status in paging component.

Example read paging state in `MtrColumnPagin`:

```
MtrColumnPagin(
            modifier.padding(
                padding
            ),
            verticalArrangement = Arrangement.spacedBy(
                16.dp
            ),
            onRequestLoadNewPage = {
                paginVM.loadPage()
            }
        ) {
            //display refresh button on very top of LazyColumn  
            item {
                ElevatedButton(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    onClick = { paginVM.refreshPage() }) {
                    Text(text = "Refresh")
                }
            }

            //if dataset not empty, display items
            if (pagingState.dataset.isNotEmpty()) {
                items(
                    count = pagingState.dataset.size,
                    key = { idx -> pagingState.dataset[idx].id }) {
                    PageItem(modifier = Modifier.fillMaxWidth(), item = pagingState.dataset[it])
                }
            }

            //if pageResult Loading, show progress indicator below last item in dataset
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
            } 
            //if pageResult Error, show error info below last item in dataset
            else if (pagingState.pageResult is PageResult.Error) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "An error occurred")
                        Spacer(modifier = Modifier.height(16.dp))
                        ElevatedButton(
                            onClick = { paginVM.loadPage() },
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(text = "Reload")
                        }
                    }
                }
            }
        }

```

Example of read paging state in `MtrVerticalGridPagin`:

```
MtrVerticalGridPagin(modifier = modifier, columns = GridCells.Fixed(2), onRequestLoadNewPage = {
        paginVM.loadPage()
    }) {      
        val dataset = paginState.dataset
        
        //if dataset is not empty, display items
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
        
        //if pageResult Loading, show progress indicator below last item.
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
        } 
        //if pageResult Error, show error info below last item
        else if (paginState.pageResult is PageResult.Error) {
            item(span = { GridItemSpan(2) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = paginState.pageResult.error ?: "An error is occurred")
                    Spacer(modifier = Modifier.height(16.dp))
                    ElevatedButton(onClick = { paginVM.refreshPage() }) {
                        Text(text = "Refresh")
                    }
                }
            }
        }
    }

```

#### Where to Put Paging Controller?

Paging controller can be hoisted at ViewModels and expose its state from there. This approach will
ensure the value of paging state will not be reset after configuration changes happened on device.
More over, it follows best practice to modular architecture. The other reason is because
pagination strongly relates to how repository or datasource layer fetch the data.

Another approach by using `remember` function and `state` inside composable, currently not
supported. Maybe in the next version, we can make a proper way to make paging controller and paging
state.

### Full Example Code

In the example code, we hoist page controller in `ViewModels`. Full code can be read at `app` module
of this project. 
