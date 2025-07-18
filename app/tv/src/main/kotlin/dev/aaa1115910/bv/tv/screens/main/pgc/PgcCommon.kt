package dev.aaa1115910.bv.tv.screens.main.pgc

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import dev.aaa1115910.biliapi.entity.pgc.PgcFeedData
import dev.aaa1115910.biliapi.entity.pgc.PgcItem
import dev.aaa1115910.biliapi.entity.pgc.PgcType
import dev.aaa1115910.biliapi.http.SeasonIndexType
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.tv.component.PgcCarousel
import dev.aaa1115910.bv.tv.component.videocard.SeasonCard
import dev.aaa1115910.bv.entity.carddata.SeasonCardData
import dev.aaa1115910.bv.entity.proxy.ProxyArea
import dev.aaa1115910.bv.tv.activities.video.SeasonInfoActivity
import dev.aaa1115910.bv.ui.theme.BVTheme
import dev.aaa1115910.bv.util.ImageSize
import dev.aaa1115910.bv.util.resizedImageUrl
import dev.aaa1115910.bv.util.toast
import dev.aaa1115910.bv.viewmodel.pgc.FeedListType
import dev.aaa1115910.bv.viewmodel.pgc.PgcViewModel

@Composable
fun PgcScaffold(
    modifier: Modifier = Modifier,
    lazyListState: LazyListState,
    pgcViewModel: PgcViewModel,
    pgcType: PgcType,
    featureButtons: (@Composable () -> Unit)? = null
) {
    val context = LocalContext.current
    val carouselFocusRequester = remember { FocusRequester() }

    val carouselItems = pgcViewModel.carouselItems
    val pgcFeeds = pgcViewModel.feedItems

    LazyColumn(
        modifier = modifier,
        state = lazyListState
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.Center
            ) {
                PgcCarousel(
                    modifier = Modifier
                        .width(880.dp)
                        .padding(32.dp, 0.dp)
                        .focusRequester(carouselFocusRequester),
                    data = carouselItems,
                    onClick = { item ->
                        SeasonInfoActivity.actionStart(
                            context = context,
                            epId = item.episodeId,
                            seasonId = item.seasonId,
                            proxyArea = ProxyArea.checkProxyArea(item.title)
                        )
                    }
                )
            }
        }
        if (featureButtons != null) {
            item {
                featureButtons()
            }
        } else {
            item {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                )
            }
        }
        itemsIndexed(items = pgcFeeds) { index, feedListItem ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .onFocusChanged {
                        if (it.hasFocus) {
                            if (index + 10 > pgcFeeds.size) {
                                pgcViewModel.loadMore()
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                when (feedListItem.type) {
                    FeedListType.Ep -> PgcFeedVideoRow(
                        data = feedListItem.items!!
                    )

                    FeedListType.Rank -> PgcFeedRankRow(
                        data = feedListItem.rank!!
                    )
                }
            }
        }
    }
}

@Composable
fun PgcFeedVideoRow(
    modifier: Modifier = Modifier,
    data: List<PgcItem>
) {
    val context = LocalContext.current
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        data.forEachIndexed { index, feedItem ->
            val cardModifier = if (index == data.lastIndex) {
                Modifier.onPreviewKeyEvent {
                    when (it.key) {
                        Key.DirectionRight -> return@onPreviewKeyEvent true
                    }
                    false
                }
            } else {
                Modifier
            }

            item {
                SeasonCard(
                    modifier = cardModifier,
                    coverHeight = 180.dp,
                    data = SeasonCardData(
                        seasonId = feedItem.seasonId,
                        title = feedItem.title,
                        subTitle = feedItem.subTitle,
                        cover = feedItem.cover.resizedImageUrl(ImageSize.SeasonCoverThumbnail),
                        rating = feedItem.rating
                    ),
                    onClick = {
                        SeasonInfoActivity.actionStart(
                            context = context,
                            seasonId = feedItem.seasonId,
                            proxyArea = ProxyArea.checkProxyArea(feedItem.title)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun PgcFeedRankRow(
    modifier: Modifier = Modifier,
    data: PgcFeedData.FeedRank
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .height(300.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            // light theme color: Color(250, 222, 214)
                            Color(20, 18, 17),
                            Color(20, 18, 17).copy(alpha = 0.298f)
                        )
                    )
                )
        ) {}
        BoxWithConstraints {
            AsyncImage(
                modifier = Modifier
                    .fillMaxHeight()
                    .offset(x = (-1 * (0.25 * 1.6 * this.maxHeight.value)).dp)
                    .graphicsLayer { alpha = 0.99f }
                    .drawWithContent {
                        val colors = listOf(
                            Color.Black,
                            Color.Transparent
                        )
                        drawContent()
                        drawRect(
                            brush = Brush.horizontalGradient(colors),
                            blendMode = BlendMode.DstIn
                        )
                        drawRect(
                            brush = Brush.verticalGradient(colors),
                            blendMode = BlendMode.DstIn
                        )
                    },
                model = data.cover,
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                alpha = 1f
            )
        }
        Row(
            modifier = Modifier
                .fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(240.dp)
                    .padding(32.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Text(
                    text = data.subTitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            LazyRow(
                modifier = modifier,
                contentPadding = PaddingValues(horizontal = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                data.items.forEachIndexed { index, feedItem ->
                    val cardModifier = if (index == data.items.lastIndex) {
                        Modifier.onPreviewKeyEvent {
                            when (it.nativeKeyEvent.keyCode) {
                                KeyEvent.KEYCODE_DPAD_RIGHT -> return@onPreviewKeyEvent true
                            }
                            false
                        }
                    } else {
                        Modifier
                    }

                    item {
                        SeasonCard(
                            modifier = cardModifier,
                            coverHeight = 180.dp,
                            data = SeasonCardData(
                                seasonId = feedItem.seasonId,
                                title = feedItem.title,
                                subTitle = feedItem.subTitle,
                                cover = feedItem.cover.resizedImageUrl(ImageSize.SeasonCoverThumbnail),
                                rating = feedItem.rating
                            ),
                            onClick = {
                                SeasonInfoActivity.actionStart(
                                    context = context,
                                    seasonId = feedItem.seasonId,
                                    proxyArea = ProxyArea.checkProxyArea(feedItem.title)
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(device = "id:tv_1080p")
@Composable
fun PgcFeedRankRowPreview() {
    val data = PgcFeedData.FeedRank(
        cover = "http://i0.hdslb.com/bfs/archive/aae451dabf64ead2e983f92be76039a8ba233ade.png",
        title = "热门热血番剧榜",
        subTitle = "每小时更新",
        items = List(8) {
            PgcItem(
                cover = "https://i0.hdslb.com/bfs/bangumi/image/f610305ad3922bee9d51748ab38da0c54e785b44.png",
                title = "解雇后走上人生巅峰",
                subTitle = "被解雇的暗黑士兵慢生活的第二人生",
                episodeId = 0,
                seasonId = 0,
                seasonType = SeasonIndexType.Anime,
                rating = "9.8"
            )
        }
    )
    BVTheme {
        PgcFeedRankRow(data = data)
    }
}

@Composable
fun PgcFeatureButtons(
    modifier: Modifier = Modifier,
    buttons: List<Triple<String, Any, () -> Unit>>
) {
    val buttonWidth = 185.dp

    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
        contentPadding = PaddingValues(horizontal = 32.dp)
    ) {
        items(items = buttons) { (title, icon, onClick) ->
            when (icon) {
                is ImageVector -> PgcFeatureButton(
                    modifier = Modifier.width(buttonWidth),
                    title = title,
                    icon = icon,
                    onClick = { onClick.invoke() }
                )

                is Painter -> PgcFeatureButton(
                    modifier = Modifier.width(buttonWidth),
                    title = title,
                    icon = icon,
                    onClick = { onClick.invoke() }
                )

                else -> {}
            }
        }
    }
}


@Composable
fun PgcFeatureButton(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
            pressedContainerColor = MaterialTheme.colorScheme.inverseSurface
        ),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.large),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = icon, contentDescription = null)
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
fun PgcFeatureButton(
    modifier: Modifier = Modifier,
    title: String,
    icon: Painter,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier,
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.inverseSurface,
            pressedContainerColor = MaterialTheme.colorScheme.inverseSurface
        ),
        shape = ClickableSurfaceDefaults.shape(shape = MaterialTheme.shapes.large),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    modifier = Modifier.size(24.dp),
                    painter = icon,
                    contentDescription = null
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

val showPlaceholderToast: () -> Unit = {
    "都说了介个是占位按钮了".toast(BVApp.context)
}