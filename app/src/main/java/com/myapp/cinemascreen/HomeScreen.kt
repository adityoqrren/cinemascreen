package com.myapp.cinemascreen

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.myapp.cinemascreen.data.MovieListItem
import com.myapp.cinemascreen.data.MovieTVListItem
import com.myapp.cinemascreen.data.TVListItem
import com.myapp.cinemascreen.ui.theme.CinemaScreenTheme

val AppBarHeight = 56.dp

@Composable
fun HomeScreen(
    toDetailScreen: (Int, String) -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    toProfileScreen: () -> Unit
) {
//    val generateTrendingNow = generateMovieTVList("datajson/trending_today_list.json", LocalContext.current)
//    val trendingNow by remember {
//        mutableStateOf(generateTrendingNow)
//    }
    val trendingNow by viewModel.trendingNow.collectAsStateWithLifecycle()

//    val generateNowInCinemas = generateMovieList(LocalContext.current)
//    val nowInCinemas by remember { mutableStateOf(generateNowInCinemas) }
    val nowInCinemas by viewModel.nowInCinemas.collectAsStateWithLifecycle()

//    val generateAiringNow = generateTVList("datajson/tvlist_airing_now.json", LocalContext.current)
//    val airingNow by remember { mutableStateOf(generateAiringNow) }
    val airingNow by viewModel.airingNow.collectAsStateWithLifecycle()

    var popularCategoryChoosen by remember { mutableStateOf(1) }
    val popularMovieTV by viewModel.popularMovieTV.collectAsStateWithLifecycle()
    val popularListState = rememberLazyListState()

    LaunchedEffect(key1 = popularCategoryChoosen) {
        viewModel.getPopular(popularCategoryChoosen)
        popularListState.scrollToItem(0)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn {
            item {
                BigBanner(listTrendingNow = trendingNow, toDetailScreen = toDetailScreen)
                CategoryButtons()
                PopularRightNow(
                    popularMovieTVList = popularMovieTV,
                    categoryChoosen = popularCategoryChoosen,
                    onChangeCategory = { categoryCode: Int ->
                        popularCategoryChoosen = categoryCode
                    },
                    listState = popularListState
                )
                NowInCinemas(nowInCinemasList = nowInCinemas)
                AiringNow(listAiringNow = airingNow)
            }
        }
        TopToolbar(toProfileScreen = toProfileScreen)
//        Text("This is HomeScreen", modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun AiringNow(modifier: Modifier = Modifier, listAiringNow: List<TVListItem>) {
    Column {
        Column(modifier = Modifier.padding(top = 20.dp, start = 16.dp)) {
            Text(
                text = "Airing Now",
                modifier = Modifier.padding(bottom = 6.dp),
                style = TextStyle(
                    fontFamily = fontFamily, fontSize = 14.sp
                )
            )
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(3.dp)
                    .clip(shape = RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        //List of tv shows airing now
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(top = 12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            items(listAiringNow) { tvItem ->
                PosterCardTV(tvItem)
            }
        }
    }
}

@Composable
fun NowInCinemas(modifier: Modifier = Modifier, nowInCinemasList: List<MovieListItem>) {
    Column {
        Column(modifier = Modifier.padding(top = 20.dp, start = 16.dp)) {
            Text(
                text = "Now In Cinemas",
                modifier = Modifier.padding(bottom = 6.dp),
                style = TextStyle(
                    fontFamily = fontFamily, fontSize = 14.sp
                )
            )
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(3.dp)
                    .clip(shape = RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        //List of movies now in cinemas
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(top = 12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp)
        ) {
            items(nowInCinemasList) { movieItem ->
                PosterCard(item = movieItem)
            }
        }
    }
}

@Composable
fun CategoryButtons(modifier: Modifier = Modifier) {
    Row(
        modifier.padding(top = 24.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(26.dp)
    ) {
        Button(
            onClick = {}, shape = RoundedCornerShape(8.dp), modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
        ) {
            Text("Movies")
        }
        Button(
            onClick = {}, shape = RoundedCornerShape(8.dp), modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary)
        ) {
            Text("TV Shows")
        }
    }
}


@Composable
fun BigBanner(
    modifier: Modifier = Modifier,
    listTrendingNow: List<MovieListItem>,
    toDetailScreen: (Int, String) -> Unit
) {
    Box(
        modifier = modifier
            .height(440.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomEnd = 8.dp, bottomStart = 8.dp))
            .background(MaterialTheme.colorScheme.primary)
            .statusBarsPadding()
            .padding(top = AppBarHeight)
    ) {
        Column(
            Modifier
                .fillMaxSize()
//                .background(MaterialTheme.colorScheme.primary)
                .padding(top = 16.dp)
        ) {
            Text(
                text = "Welcome",
                style = textSemiBold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 16.dp),
                color = Color.White
            )
            Text(
                text = "Explore Movies and TV",
                style = textSemiBold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 16.dp),
                color = Color.White
            )
            Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp)) {
                Text(
                    text = "Trending Now",
                    modifier = Modifier.padding(bottom = 6.dp),
                    style = TextStyle(
                        fontFamily = fontFamily, fontSize = 14.sp
                    ),
                    color = Color.White
                )
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(3.dp)
                        .clip(shape = RoundedCornerShape(4.dp))
                        .background(Color.White)
                )
            }

            //list of PosterCard
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(top = 12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                items(listTrendingNow) { trendingItem ->
                    PosterCardWithBadge(trendingItem, toDetailScreen = toDetailScreen)
                }
            }
        }
    }
}

@Composable
fun TopToolbar(modifier: Modifier = Modifier, toProfileScreen: () -> Unit) {
    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .height(AppBarHeight),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "CinemaScreen",
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            style = TextStyle(
                fontFamily = fontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            color = Color.White
        )

        IconButton(onClick = { toProfileScreen() }) {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = null,
                modifier = Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .clip(CircleShape),
                tint = Color.White,
            )
        }
    }
}

@Composable
fun PopularRightNow(
    modifier: Modifier = Modifier,
    popularMovieTVList: List<MovieTVListItem>,
    categoryChoosen: Int,
    onChangeCategory: (Int) -> Unit,
    listState: LazyListState
) {
    Column {
        Column(modifier = Modifier.padding(top = 16.dp, start = 16.dp)) {
            Text(
                text = "Popular Right Now",
                modifier = Modifier.padding(bottom = 6.dp),
                style = TextStyle(
                    fontFamily = fontFamily, fontSize = 14.sp
                )
            )
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(3.dp)
                    .clip(shape = RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
        //Category to choose
        Row(
            Modifier.padding(top = 12.dp, start = 16.dp, end = 16.dp),
            Arrangement.spacedBy(12.dp)
        ) {
//            Button(
//                onClick = {onChangeCategory(1)},
//                modifier = Modifier.defaultMinSize(
//                    minWidth = ButtonDefaults.MinWidth,
//                    minHeight = 1.dp
//                ),
//                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.secondary),
//                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
//            ) {
//                Text(text = "Streaming")
//            }
            OutlinedButton(
                onClick = { onChangeCategory(1) },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                shape = ButtonDefaults.outlinedShape,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (categoryChoosen == 1) Color.White else MaterialTheme.colorScheme.secondary,
                    containerColor = if (categoryChoosen == 1) MaterialTheme.colorScheme.secondary else Color.White
                ),
                modifier = Modifier.defaultMinSize(
                    minWidth = ButtonDefaults.MinWidth,
                    minHeight = 1.dp
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(text = "Streaming")
            }
            OutlinedButton(
                onClick = { onChangeCategory(2) },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                shape = ButtonDefaults.outlinedShape,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (categoryChoosen == 2) Color.White else MaterialTheme.colorScheme.secondary,
                    containerColor = if (categoryChoosen == 2) MaterialTheme.colorScheme.secondary else Color.White
                ),
                modifier = Modifier.defaultMinSize(
                    minWidth = ButtonDefaults.MinWidth,
                    minHeight = 1.dp
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(text = "On Tv")
            }
            OutlinedButton(
                onClick = { onChangeCategory(3) },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                shape = ButtonDefaults.outlinedShape,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (categoryChoosen == 3) Color.White else MaterialTheme.colorScheme.secondary,
                    containerColor = if (categoryChoosen == 3) MaterialTheme.colorScheme.secondary else Color.White
                ),
                modifier = Modifier.defaultMinSize(
                    minWidth = ButtonDefaults.MinWidth,
                    minHeight = 1.dp
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(text = "For Rent")
            }
            OutlinedButton(
                onClick = { onChangeCategory(4) },
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                shape = ButtonDefaults.outlinedShape,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (categoryChoosen == 4) Color.White else MaterialTheme.colorScheme.secondary,
                    containerColor = if (categoryChoosen == 4) MaterialTheme.colorScheme.secondary else Color.White
                ),
                modifier = Modifier.defaultMinSize(
                    minWidth = ButtonDefaults.MinWidth,
                    minHeight = 1.dp
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(text = "On Cinema")
            }
        }
        //List of popular movies and TVs
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(top = 12.dp),
            contentPadding = PaddingValues(horizontal = 12.dp),
            state = listState
        ) {
            items(popularMovieTVList) { item: MovieTVListItem ->
                PosterCardPopular(item)
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    CinemaScreenTheme {
        HomeScreen(toDetailScreen = { idValue, mediatypeValue -> }, toProfileScreen = {})
    }
}

@Composable
fun PosterCardWithBadge(item: MovieListItem, toDetailScreen: (Int, String) -> Unit) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .aspectRatio(2 / 3f)
            .clickable {
                toDetailScreen(item.id, item.media_type)
            }
    ) {
        Box {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500/${item.poster_path}",
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
            )

            Box(
                modifier = Modifier
                    .padding(4.dp)//margin (padding outside)
                    .clip(shape = RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.tertiary)
                    .fillMaxWidth(0.4f)
                    .padding(4.dp)
                    .align(Alignment.TopEnd)
            ) {
                Text(
                    text = item.media_type.replaceFirstChar { it.uppercase() },
                    modifier = Modifier.align(Alignment.Center),
                    style = TextStyle(fontSize = 8.sp)
                )
            }
        }
    }
}

@Composable
fun PosterCard(item: MovieListItem) {
    Log.d("see PosterCard item", "${item.title} and ${item.poster_path}")
    Card(
        modifier = Modifier
            .width(120.dp)
            .aspectRatio(2 / 3f) //aspectRatio is important to give shape of card when item is still loading
    ) {
        Box {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/original/${item.poster_path}",
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
            )
        }
    }
}

@Composable
fun PosterCardTV(TVItem: TVListItem) {
//    Log.d("see PosterCard item","${TVItem.name} and ${item.poster_path}")
    Card(
        modifier = Modifier
            .width(120.dp)
            .aspectRatio(2 / 3f)
    ) {
        Box {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/original/${TVItem.poster_path}",
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
            )
        }
    }
}

@Composable
fun PosterCardPopular(item: MovieTVListItem) {
    //Log.d("see PosterCard item", "${item.title} and ${item.poster_path}")
    Card(
        modifier = Modifier
            .width(120.dp)
            .aspectRatio(2 / 3f)
    ) {
        Box {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/original/${item.poster_path}",
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
            )
        }
    }
}
