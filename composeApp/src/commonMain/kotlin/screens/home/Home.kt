package screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import data.response.SampleDataItem
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
        LazyColumn {
            items(dataItem.size) { index ->
                Text(
                    text = dataItem[index].title.toString(),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}