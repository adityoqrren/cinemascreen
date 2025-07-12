package com.myapp.cinemascreen.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.myapp.cinemascreen.data.models.MovieTVFavorite
import com.myapp.cinemascreen.fontFamily
import com.myapp.cinemascreen.ui.screens.components.DisposableEffectWithLifecycle
import com.myapp.cinemascreen.ui.screens.components.FavoritesCategoryButtons
import com.myapp.cinemascreen.ui.screens.components.GridList
import com.myapp.cinemascreen.ui.states.UIstate

@Composable
fun FavoritesScreen(
    toDetailScreen: (Int, String) -> Unit,
    toProfileScreen: () -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val categoryChoosen by viewModel.idSelected.collectAsStateWithLifecycle()
    val favoritesMovieTV by viewModel.favoritesMovieTV.collectAsStateWithLifecycle()
    val totalMoviesTV by viewModel.totalMoviesTV.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffectWithLifecycle(
        onDestroy = {
            viewModel.detachListener()
        }
    )

    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is UIstate.Error -> {
                Text(
                    "There is a problem with our server or connection",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            UIstate.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is UIstate.Success -> {
                LazyColumn(contentPadding = PaddingValues(top = 140.dp)) {
                    item {
                        FavoritesCategoryButtons(
                            modifier = Modifier.padding(top = 20.dp),
                            categoryChoosen = categoryChoosen,
                            onChangeCategory = { categoryCode: Int ->
                                viewModel.setIdSelected(categoryCode)
                            },
                        )
                    }
                    item {
                        Spacer(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp)
                        )
                    }
                    if (favoritesMovieTV.isEmpty()) {
                        item {
                            val noItemText = when (categoryChoosen) {
                                1 -> "No Movie/TV you like"
                                2 -> "No Movie you like"
                                3 -> "No TV Show you like"
                                else -> "No Item"
                            }
                            Text(
                                text = noItemText,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {

                        GridList(
                            columnCount = 2,
                            verticalSpace = 8.dp,
                            horizontalSpace = 8.dp,
                            horizontalPadding = 16.dp,
                            favoritesMovieTV
                        ) { item ->
                            PosterCardExpanding(
                                item = item,
                                onClickDetail = { toDetailScreen(item.id, item.media_type) }
                            )
                        }

                    }
                }
            }
        }
        FavoriteTopToolbar(
            toProfileScreen = toProfileScreen,
            totalMoviesTV = totalMoviesTV
        )
    }
}

@Composable
fun FavoriteTopToolbar(
    modifier: Modifier = Modifier,
    totalMoviesTV: Int,
    toProfileScreen: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = 16.dp)
            .statusBarsPadding()
    ) {
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
        Row(
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Text(text = "$totalMoviesTV movies and tv shows", color = Color.White)
        }
    }

}

@Composable
fun PosterCardExpanding(item: MovieTVFavorite, onClickDetail: () -> Unit) {
    //Log.d("see PosterCard item", "${item.title} and ${item.poster_path}")
    Card(
        modifier = Modifier
            .aspectRatio(2 / 3f)
            .clickable {
                onClickDetail()
            }
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

