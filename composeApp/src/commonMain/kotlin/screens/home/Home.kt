package screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import coil3.compose.rememberAsyncImagePainter
import data.response.SampleDataItem
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI
import kotlin.math.absoluteValue

@OptIn(KoinExperimentalAPI::class)
@Composable
fun Home() {
    val viewModel = koinViewModel<HomeViewModel>()
    val state = viewModel.homeViewState
    LaunchedEffect(Unit) {
        viewModel.getSampleData()
    }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val resultedState = state.value) {
            is HomeViewState.Failure -> Failure(resultedState.error)
            HomeViewState.Loading -> Loading()
            is HomeViewState.Success -> Success(resultedState.dataItem)
        }
    }
}

@Composable
fun Failure(message: String) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text(text = message, modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun Loading() {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = Color.Black,
        )
    }
}

@Composable
fun Success(dataItem: ArrayList<SampleDataItem>) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxWidth()) {
            TopNewsPager(dataItem, onItemClick = {
            })
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopNewsPager(sampleData: List<SampleDataItem>, onItemClick: (String) -> Unit) {
    val pagerState = rememberPagerState() { sampleData.take(7).size }
    Column {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "Breaking News",
            modifier = Modifier.padding(10.dp),
            style = TextStyle(fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 21.sp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalPager(
            state = pagerState, modifier = Modifier.fillMaxWidth(),
            pageSpacing = 20.dp, contentPadding = PaddingValues(horizontal = 30.dp)
        ) { page ->
            val item = sampleData[page]
            Box(modifier = Modifier.clickable { onItemClick(item.url.orEmpty()) }) {
                Image(
                    painter = rememberAsyncImagePainter(item.url ?: ""),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.height(180.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .graphicsLayer {
                            val pageOffset = (
                                    (pagerState.currentPage - page) + pagerState
                                        .currentPageOffsetFraction
                                    ).absoluteValue
                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        }
                        .drawWithCache {
                            val gradient = Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black),
                                startY = size.height / 3,
                                endY = size.height
                            )
                            onDrawWithContent {
                                drawContent()
                                drawRect(gradient, blendMode = BlendMode.Multiply)
                            }
                        }
                )
                Text(
                    item.title.orEmpty(),
                    style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                    color = Color.White,
                    modifier = Modifier.align(Alignment.BottomStart)
                        .padding(horizontal = 20.dp, vertical = 15.dp)
                )
            }
        }
        Row(
            Modifier
                .height(50.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(sampleData.take(7).size) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) Color.Blue.copy(alpha = 0.5f) else Color.Black.copy(
                        alpha = 0.1f
                    )
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)

                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "Recommendation",
            modifier = Modifier.padding(10.dp),
            style = TextStyle(fontWeight = FontWeight.Bold, color = Color.Black)
        )
        Spacer(modifier = Modifier.height(6.dp))
    }
}
