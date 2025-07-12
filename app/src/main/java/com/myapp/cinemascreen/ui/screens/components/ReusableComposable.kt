package com.myapp.cinemascreen.ui.screens.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.myapp.cinemascreen.R
import com.myapp.cinemascreen.data.models.MovieTVListItem
import com.myapp.cinemascreen.utils.getMovieYear

@Composable
fun shimmerBrush(showShimmer: Boolean = true,targetValue:Float = 1000f): Brush {
    return if (showShimmer) {
        val shimmerColors = listOf(
            Color.LightGray.copy(alpha = 0.6f),
            Color.LightGray.copy(alpha = 0.2f),
            Color.LightGray.copy(alpha = 0.6f),
        )

        val transition = rememberInfiniteTransition(label = "")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(800), repeatMode = RepeatMode.Reverse
            ), label = ""
        )
        Brush.linearGradient(
            colors = shimmerColors,
            start = Offset.Zero,
            end = Offset(x = translateAnimation.value, y = translateAnimation.value)
        )
    } else {
        Brush.linearGradient(
            colors = listOf(Color.Transparent,Color.Transparent),
            start = Offset.Zero,
            end = Offset.Zero
        )
    }
}

@Composable
fun DisposableEffectWithLifecycle(
    onCreate: () -> Unit = {},
    onStart: () -> Unit = {},
    onStop: () -> Unit = {},
    onResume: () -> Unit = {},
    onPause: () -> Unit = {},
    onDestroy: () -> Unit = {},
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val currentOnCreate by rememberUpdatedState(onCreate)
    val currentOnStart by rememberUpdatedState(onStart)
    val currentOnStop by rememberUpdatedState(onStop)
    val currentOnResume by rememberUpdatedState(onResume)
    val currentOnPause by rememberUpdatedState(onPause)
    val currentOnDestroy by rememberUpdatedState(onDestroy)

    DisposableEffect(lifecycleOwner) {
        val lifecycleEventObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> currentOnCreate()
                Lifecycle.Event.ON_START -> currentOnStart()
                Lifecycle.Event.ON_PAUSE -> currentOnPause()
                Lifecycle.Event.ON_RESUME -> currentOnResume()
                Lifecycle.Event.ON_STOP -> currentOnStop()
                Lifecycle.Event.ON_DESTROY -> currentOnDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleEventObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleEventObserver)
        }
    }
}

@Composable
fun FavoritesCategoryButtons(
    modifier: Modifier = Modifier,
    categoryChoosen: Int,
    onChangeCategory: (Int) -> Unit
) {
    Row(
        modifier.padding(start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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
            Text(text = "All")
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
            Text(text = "Movies")
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
            Text(text = "TV Shows")
        }
    }
}

fun <T> LazyListScope.GridList(
    columnCount : Int,
    verticalSpace: Dp,
    horizontalSpace: Dp,
    horizontalPadding: Dp,
    list: List<T>,
    content: @Composable (T) -> Unit,
) {
    for (i in list.indices step columnCount) {
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(horizontalSpace),
                modifier = Modifier
                    .padding(
                        bottom = verticalSpace,
                        start = horizontalPadding,
                        end = horizontalPadding
                    )
                    .fillMaxWidth()
            ) {
                for (j in 0 until columnCount) {

                    if ((i + j) < list.size) {
                        Box(
                            modifier = Modifier.weight(1f),
                        ) {
                            content(list[i + j])
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f, fill = true))
                    }

                }
            }
        }
    }
}

fun <T> LazyListScope.GridListIntrinsic(
    columnCount : Int,
    verticalSpace: Dp,
    horizontalSpace: Dp,
    horizontalPadding: Dp,
    list: List<T>,
    content: @Composable (T) -> Unit,
) {
    for (i in list.indices step columnCount) {
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(horizontalSpace),
                modifier = Modifier
                    .padding(
                        bottom = verticalSpace,
                        start = horizontalPadding,
                        end = horizontalPadding
                    )
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
            ) {
                for (j in 0 until columnCount) {

                    if ((i + j) < list.size) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        ) {
                            content(list[i + j])
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f, fill = true))
                    }

                }
            }
        }
    }
}

fun LazyListScope.GridListIntrinsicPaging(
    columnCount : Int,
    verticalSpace: Dp,
    horizontalSpace: Dp,
    horizontalPadding: Dp,
    count: Int,
    content: @Composable (Int) -> Unit,
) {
    for (i in 0 until count step columnCount) {
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(horizontalSpace),
                modifier = Modifier
                    .padding(
                        bottom = verticalSpace,
                        start = horizontalPadding,
                        end = horizontalPadding
                    )
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
            ) {
                for (j in 0 until columnCount) {

                    if ((i + j) < count) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                        ) {
                            content((i + j))
                        }
                    } else {
                        Spacer(modifier = Modifier.weight(1f, fill = true))
                    }

                }
            }
        }
    }
}

@Composable
fun GridMovieTVItem (item: MovieTVListItem, onClickDetail: () -> Unit) {
//    Log.d("see PosterCard item", "${item.title} and ${item.poster_path}")
//    Card(
//    ){
    var showItemShimmer by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClickDetail()
                }
        ) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w300/${item.poster_path}",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.background(
                    shimmerBrush(
                        targetValue = 1300f,
                        showShimmer = showItemShimmer
                    )
                ).aspectRatio(2f/3f).clip(RoundedCornerShape(8.dp)),
                onLoading = { showItemShimmer = true },
                onSuccess = { showItemShimmer = false },
                error = painterResource(id = R.drawable.placeholder)
            )
            Spacer(
                Modifier.height(4.dp)
            )
            Text(
                item.title
            )
            Row {
                Box(
                    modifier = Modifier
                        .padding(end = 4.dp)//margin (padding outside)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.tertiary)
                        .fillMaxWidth(0.4f)
                        .padding(4.dp)
                ) {
                    Text(
                        text = item.media_type.replaceFirstChar { it.uppercase() },
                        modifier = Modifier.align(Alignment.Center),
                        style = TextStyle(fontSize = 8.sp)
                    )
                }
                Text(
                    text = if (item.release_date.isNotEmpty()) getMovieYear(item.release_date) else "",
                    style = TextStyle(fontWeight = FontWeight.Bold)
                )
            }
        }

//        Box {
//            AsyncImage(
//                model = "https://image.tmdb.org/t/p/original/${item.poster_path}",
//                contentDescription = null,
//                contentScale = ContentScale.FillWidth,
//            )
//        }
//    }
}