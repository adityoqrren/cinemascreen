package com.myapp.cinemascreen.ui.screens.components

import android.util.Log
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.myapp.cinemascreen.R
import com.myapp.cinemascreen.data.models.Cast
import com.myapp.cinemascreen.data.models.ProductionCompany
import com.myapp.cinemascreen.fontFamily
import com.myapp.cinemascreen.ui.screens.AppBarHeight
import com.myapp.cinemascreen.ui.theme.YellowDarker
import com.myapp.cinemascreen.ui.theme.YellowMain
import com.myapp.cinemascreen.ui.theme.YellowOrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.max

val AppBarCollapsedHeight = 56.dp
val AppBarExpandedHeight = 400.dp
val imageHeight = AppBarExpandedHeight - AppBarCollapsedHeight

@Composable
fun BottomLikeShare(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onFavoriteAction: () -> Unit
) {
    var isClicked by remember { mutableStateOf(false) }
    //code above using var because we want to change the value. it uses delegated property so it directly forward to mutablestate.value

    val interactionSource = remember { MutableInteractionSource() }

    val coroutineScope = rememberCoroutineScope()

    val loveScaleFactor by animateFloatAsState(
        targetValue = if (isClicked) 1.2f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ), label = ""
    )

    Surface(
        shadowElevation = 2.dp,
        modifier = modifier.padding(bottom = 20.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.secondary,
                )
                .padding(7.dp)
                .fillMaxWidth(0.5f)
                .wrapContentHeight()
        ) {
            Box(
                modifier = Modifier
                    .padding(7.dp)
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.mdi_share),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .height(32.dp)
                        .width(32.dp)
                )
            }
            Spacer(
                modifier = Modifier
                    .background(Color.White)
                    .width(1.dp)
                    .height(46.dp)
            )
            Box(
                modifier = Modifier
                    .padding(7.dp)
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isFavorite) Color.Red else Color.White,
                    modifier = Modifier
//                            .background(Color.Red)
                        .height(32.dp)
                        .width(32.dp)
                        .scale(loveScaleFactor)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            //isLoved = !isLoved
                            onFavoriteAction()
                            if (isFavorite) {
                                isClicked = !isClicked
                                coroutineScope.launch {
                                    delay(500)
                                    isClicked = false
                                }
                            }
                        }
                )
            }
        }
    }
}

@Composable
fun SetStatusBarColor(color: Color, useDarkIcons: Boolean) {
    val systemUiController = rememberSystemUiController()

    DisposableEffect(systemUiController, useDarkIcons) {
        systemUiController.setStatusBarColor(
            color = color,
            darkIcons = useDarkIcons
        )

        onDispose { }
    }
}


@Composable
fun ParalaxToolbar(
    offset: Int,
    maxOffset: Int,
    backToBefore: () -> Unit,
    movieTitle: String,
    movieImageUrl: String?
) {
    ExpandedAppBar(
        offset = offset,
        maxOffset = maxOffset,
        movieTitle = movieTitle,
        movieImageUrl = movieImageUrl
    )
    StaticButtonAppbar(offset = offset, maxOffset = maxOffset, backToBefore = backToBefore)
}

@Composable
fun StaticButtonAppbar(offset: Int, maxOffset: Int, backToBefore: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(AppBarHeight)
            .zIndex(5f)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Icon(
            imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier
                .height(24.dp)
                .width(24.dp)
                .clickable { backToBefore() },
            tint = if (offset > maxOffset / 2) MaterialTheme.colorScheme.primary else Color.White
        )
    }
}

@Composable
fun ExpandedAppBar(offset: Int, maxOffset: Int, movieTitle: String, movieImageUrl: String?) {

    val offsetProgress = max(0f, offset * 3f - 2f * maxOffset) / maxOffset

    if (offset > (maxOffset / 2)) {
        SetStatusBarColor(color = Color.Transparent, useDarkIcons = true)
    } else {
        SetStatusBarColor(color = Color.Transparent, useDarkIcons = false)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(AppBarExpandedHeight)
            .offset({ IntOffset(x = 0, y = -offset) }),
        color = Color.White,
        shadowElevation = if (offset == maxOffset) 4.dp else 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .height(imageHeight)
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = 1f - offsetProgress
                        }
                ) {
                    //image
                    if (movieImageUrl != null) {
                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/w1280/$movieImageUrl",
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.tertiary)
                                .fillMaxSize(),
                            error = painterResource(id = R.drawable.placeholder)
                        )
                    } else {
                        //TODO: change with NO IMAGE
                        Box(
                            Modifier
                                .background(MaterialTheme.colorScheme.tertiary)
                                .fillMaxSize()
                        ) {

                        }
                    }
                    //layer blurry white in front of image
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colorStops = arrayOf(
                                        Pair(0.02f, Color.Transparent),
                                        Pair(1f, Color.White)
                                    )
                                )
                            )
                    )
                }
                Row(
                    modifier = Modifier
//                        .background(Color.Blue)
                        .fillMaxWidth()
                        .height(AppBarCollapsedHeight),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = movieTitle,
                        maxLines = if (offset > maxOffset / 2) 1 else 2,
                        style = TextStyle(
                            fontFamily = fontFamily,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                        ),
                        modifier = Modifier
                            .scale(1f - 0.2f * offsetProgress)
                            .padding(
                                start = 16.dp,
                                end = if (offset > maxOffset) 30.dp else 16.dp
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun TopCasts(modifier: Modifier = Modifier, topActors: List<Cast>) {
    Log.d("see topActors", "$topActors")

    Column(modifier = modifier.padding(top = 16.dp)) {
        Text("Top Cast", Modifier.padding(horizontal = 16.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.padding(top = 12.dp)
        ) {
            items(topActors) { actor ->
                TopCastItem(
                    actorImg = actor.profile_path ?: "",
                    actorName = actor.name,
                    actorCharacter = actor.character
                )
            }
        }
    }
}

@Composable
fun Studio(modifier: Modifier = Modifier, movieStudios: List<ProductionCompany>) {
    Column(
        modifier = modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Text("Studio", Modifier.padding(horizontal = 16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(top = 16.dp)
        ) {
            LazyHorizontalGrid(
                rows = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(movieStudios) { item ->
                    StudioItem(
                        nameOfStudio = item.name,
                        imgStudio = item.logo_path ?: ""
                    )
                }
            }
        }
    }
}

@Composable
fun Overview(modifier: Modifier = Modifier, movieOverview: String) {
    Column(modifier = modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)) {
        Text(text = "Overview")
        Text(
            text = movieOverview,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            fontFamily = fontFamily,
            fontSize = 12.sp
        )
    }
}

@Composable
fun CardWithAnimatedBorder(
    modifier: Modifier = Modifier,
    onCardClick: () -> Unit = {},
    borderColors: List<Color> = emptyList(),
    startAnimation: Boolean,
    content: @Composable () -> Unit
) {
    val angle by animateFloatAsState(
        targetValue = if (startAnimation) 360f else 0f,
        animationSpec = repeatable(
            iterations = 3,
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "repeatable spec"
    )

    val brush =
        if (borderColors.isNotEmpty()) Brush.sweepGradient(borderColors)
        else Brush.sweepGradient(listOf(Color.Gray, Color.White))

    Surface(modifier = modifier.clickable { onCardClick() }, shape = RoundedCornerShape(20.dp)) {
        Surface(
            modifier =
            Modifier
                .clipToBounds()
                .fillMaxWidth()
                .padding(2.dp)
                .drawWithContent {
                    rotate(angle) {
                        drawCircle(
                            brush = brush,
                            radius = size.width,
                            blendMode = BlendMode.SrcIn,
                        )
                    }
                    drawContent()
                },
            shape = RoundedCornerShape(18.dp)
        ) {
            Box(modifier = Modifier.background(Color.White)) { content() }
        }
    }
}