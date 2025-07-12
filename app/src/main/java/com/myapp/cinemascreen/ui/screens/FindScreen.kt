package com.myapp.cinemascreen.ui.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.myapp.cinemascreen.R
import com.myapp.cinemascreen.data.models.MovieListItem
import com.myapp.cinemascreen.fontFamily
import com.myapp.cinemascreen.ui.screens.components.EasyGrid
import com.myapp.cinemascreen.ui.screens.components.FavoritesCategoryButtons
import com.myapp.cinemascreen.ui.screens.components.GridListIntrinsicPaging
import com.myapp.cinemascreen.ui.screens.components.GridMovieTVItem

@Composable
fun FindScreen(
    viewModel: FindViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
    toDetailScreen: (Int, String) -> Unit,
    toProfileScreen: () -> Unit
) {

    val genres = viewModel.genres

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val listRecentSearchResults by viewModel.recentSearch.collectAsStateWithLifecycle()
    val searchResult = viewModel.filteredSearchResults.collectAsLazyPagingItems()

    var isFirstBackPress by remember { mutableStateOf(true) }

    var buttonVisibility by remember {
        mutableStateOf(true)
    }

    val focusManager = LocalFocusManager.current

    val isInSearch by viewModel.isInSearch

    val idCatSelected by viewModel.idCatSelected.collectAsStateWithLifecycle()

    val listState = rememberLazyListState()

    LaunchedEffect(idCatSelected) {
        listState.scrollToItem(0)
    }

    BackHandler {
        if (isInSearch) {
            if (isFirstBackPress) {
                Log.d("check backpress", "first backpressed")
                isFirstBackPress = false
                focusManager.clearFocus(true)
                viewModel.clearSearchValue()
            } else {
                viewModel.setIsInSearch(false)
                //reset first back press indicator
                isFirstBackPress = true
            }
        } else {
            onNavigateUp()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(contentPadding = PaddingValues(top = 180.dp), state = listState) {

            if (isInSearch) {
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
                GridListIntrinsicPaging(
                    columnCount = 2,
                    verticalSpace = 8.dp,
                    horizontalSpace = 16.dp,
                    horizontalPadding = 16.dp,
                    count = searchResult.itemCount
                ) { item ->
                    GridMovieTVItem(
                        item = searchResult[item]!!,
                        onClickDetail = {
                            viewModel.saveToRecentSearch(searchResult[item]!!)
                            toDetailScreen(
                                searchResult[item]!!.id,
                                searchResult[item]!!.media_type
                            )
                        })
                }

                searchResult.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
//                                item{
//                                    Column(
//                                        modifier = Modifier.fillParentMaxSize(),
//                                        verticalArrangement = Arrangement.Center,
//                                        horizontalAlignment = Alignment.CenterHorizontally
//                                    ) {
//                                        CircularProgressIndicator()
//                                    }
//                                }
                        }

                        loadState.refresh is LoadState.Error -> {
                            val error = searchResult.loadState.refresh as LoadState.Error
                            Log.d(
                                "check error",
                                "errornya di akhir: ${error.endOfPaginationReached} || ${error.error} || $error"
                            )
                            item {
                                Column(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Oops, something went wrong",
                                        color = MaterialTheme.colorScheme.error,
                                        maxLines = 2
                                    )
                                    OutlinedButton(onClick = { searchResult.refresh() }) {
                                        Text(text = "retry")
                                    }
                                }
                            }
                        }

                        loadState.append is LoadState.Loading -> {
                            item {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                        .wrapContentWidth(Alignment.CenterHorizontally)
                                )
                            }
                        }

                        loadState.append is LoadState.Error -> {
                            item {
                                Column(
                                    Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Oops, something went wrong",
                                        color = MaterialTheme.colorScheme.error,
                                        maxLines = 2
                                    )
                                    OutlinedButton(onClick = { retry() }) {
                                        Text(text = "retry")
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    GenresBox(genreData = genres)
                    //button to expand genres
                    AnimatedVisibility(
                        visible = buttonVisibility, exit = slideOutVertically() + fadeOut()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    viewModel.expandingGenres()
                                    buttonVisibility = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.White,
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ),
                                modifier = Modifier.defaultMinSize(
                                    minWidth = ButtonDefaults.MinWidth, minHeight = 1.dp
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(text = "See More")
                            }
                        }
                    }
                    //recent search results
                    RecentSearchResults(listRecentSearchResults = listRecentSearchResults, toDetailScreen = toDetailScreen)
                }
            }

        }
        FindScreenToolbar(
            searchValue = searchQuery,
            onChangeSearchValue = { newSearchValue -> viewModel.toSetSearchValue(newSearchValue) },
            onClearSearch = { viewModel.clearSearchValue() },
            focusManager = focusManager,
            isInSearch = isInSearch,
            onChangeInSearch = { bool -> viewModel.setIsInSearch(bool) },
            idCatSelected = idCatSelected,
            onChangeCatSelected = { int -> viewModel.toSetCatSelected(int) },
            toProfileScreen = toProfileScreen
        )
    }
}

@Composable
fun RecentSearchResults(
    modifier: Modifier = Modifier,
    listRecentSearchResults: List<MovieListItem>,
    toDetailScreen: (Int, String) -> Unit
) {
    Column(
        modifier.padding(bottom = 16.dp)
    ) {
        Row(Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "Recent Search Results",
                modifier = Modifier.padding(bottom = 12.dp),
                style = TextStyle(
                    fontFamily = fontFamily, fontSize = 16.sp
                )
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
        ) {
            items(listRecentSearchResults) { item ->
                PosterCardWithBadge(item = item, toDetailScreen = toDetailScreen )
            }
        }
    }
}

@Composable
fun GenresBox(genreData: List<String>, modifier: Modifier = Modifier) {
    Column(modifier.padding(16.dp)) {
        Text(
            text = "Genres", modifier = Modifier.padding(bottom = 12.dp), style = TextStyle(
                fontFamily = fontFamily, fontSize = 16.sp
            )
        )
        Box(
            modifier = Modifier.animateContentSize()
        ) {
            EasyGrid(
                columnCount = 2,
                paddingValues = PaddingValues(0.dp),
                verticalSpace = 8.dp,
                horizontalSpace = Arrangement.spacedBy(8.dp),
                list = genreData,
            ) { genre ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.tertiary)
                        .clickable { }
                        .height(80.dp)
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        genre, textAlign = TextAlign.Center, style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun FindScreenToolbar(
    modifier: Modifier = Modifier,
    toProfileScreen: () -> Unit,
    isInSearch: Boolean,
    onChangeInSearch: (Boolean) -> Unit,
    searchValue: String,
    onChangeSearchValue: (String) -> Unit,
    onClearSearch: () -> Unit,
    idCatSelected: Int,
    onChangeCatSelected: (Int) -> Unit,
    focusManager: FocusManager
) {
//    val localContext = LocalContext.current
//    val onBackPressedDispatcherOwner = LocalOnBackPressedDispatcherOwner.current
    Log.d("check FindScreenToolbar", "isInSearch $isInSearch")

    val focusRequester = remember { FocusRequester() }

    val isSearchRecommendationShow by remember {
        mutableStateOf(false)
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .clip(shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp)
            .statusBarsPadding()
    ) {
        //top row (logo and button profile)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 6.dp)
                .height(AppBarHeight),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CinemaScreen",
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                style = TextStyle(
                    fontFamily = fontFamily, fontWeight = FontWeight.Bold, fontSize = 16.sp
                ),
                color = Color.White
            )
            IconButton(onClick = { toProfileScreen() }) {
                Icon(
                    imageVector = Icons.Outlined.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .height(32.dp)
                        .width(32.dp),
                    tint = Color.White
                )
            }
        }
        //search field
        Row(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxWidth()
        ) {
            TextField(value = searchValue,
                onValueChange = { onChangeSearchValue(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            onChangeInSearch(true)
                        }
                    },
                shape = RoundedCornerShape(28.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = { keyboardController?.hide() }
                ),
                placeholder = { Text("Find movie or TV show") },
                leadingIcon = {
                    if (isInSearch) {
                        IconButton(onClick = {
                            onClearSearch()
                            focusManager.clearFocus()
                            onChangeInSearch(false)
                        }) {
                            Icon(Icons.Outlined.ArrowBack, contentDescription = "icon back search")
                        }
                    } else {
//                        IconButton(onClick = {}) {
                        Icon(Icons.Outlined.Search, contentDescription = "icon search")
//                        }
                    }
                },
                trailingIcon = {
                    IconButton(onClick = {}) {
                        if (searchValue.isNotEmpty()) {
                            IconButton(onClick = onClearSearch) {
                                Icon(Icons.Outlined.Clear, contentDescription = "icon clear search")
                            }
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_mic_none_24),
                                contentDescription = "icon open mic"
                            )
                        }
                    }
                })
        }
        //search recommendation : snippet
//        AnimatedVisibility(visible = isSearchRecommendationShow) {
//            LazyColumn {
//                itemsIndexed(listOf("1","2","3")){ index, item ->
//                    Column(modifier = Modifier
//                        .fillMaxWidth()
//                        .clickable {}
//                    ) {
//                        Text(text = item, modifier = Modifier.padding(8.dp))
//                        if (index < 2) {
//                            HorizontalDivider(
//                                modifier = Modifier.fillMaxWidth(),
//                                color = Color.White
//                            )
//                        }
//                    }
//                }
//                item {
//                    Spacer(modifier = Modifier.height(20.dp))
//                }
//            }
//        }
        //category buttons
        if (!isSearchRecommendationShow && isInSearch) {
            FavoritesCategoryButtons(
                modifier = Modifier.padding(bottom = 16.dp),
                categoryChoosen = idCatSelected,
                onChangeCategory = { int -> onChangeCatSelected(int) })
        }
    }
}

//@Composable
//fun <T> EasyGridWithHeight(
//    columnCount: Int,
//    paddingValues: PaddingValues,
//    verticalSpace: Dp,
//    horizontalSpace: Arrangement.Horizontal,
//    list: List<T>,
//    content: @Composable (T) -> Unit,
//) {
//    Column(
//        modifier = Modifier.padding(paddingValues),
//        verticalArrangement = Arrangement.spacedBy(space = verticalSpace)
//    ) {
//        for (i in list.indices step columnCount) {
//            Row(
//                horizontalArrangement = horizontalSpace,
//                modifier = Modifier
//                    .fillMaxWidth()
//            ) {
//                for (j in 0 until columnCount) {
//
//                    if ((i + j) < list.size) {
//                        Box(
//                            modifier = Modifier
//                                .weight(1f)
////                                .height(),
//                        ) {
//                            content(list[i + j])
//                        }
//                    } else {
//                        Spacer(modifier = Modifier.weight(1f, fill = true))
//                    }
//
//                }
//            }
//        }
//    }
//}
