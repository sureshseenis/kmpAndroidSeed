package screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import data.response.SampleDataItem
import kotlinx.coroutines.runBlocking
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

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
        LazyColumn(Modifier.fillMaxWidth().padding(4.dp)) {
            items(dataItem.size) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    elevation = 4.dp,
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Column(Modifier.fillMaxWidth()) {
                        Image(
                            painter = rememberAsyncImagePainter(dataItem[index].url),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth().height(150.dp)
                        )
                        Text(
                            text = dataItem[index].title.toString(),
                            modifier = Modifier.fillMaxWidth().padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}