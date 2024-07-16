package screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import data.response.SampleDataItem

class Home : Screen {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<HomeViewModel>()
        HomeScreenContent(viewModel)
    }

    @Composable
    fun HomeScreenContent(viewModel: HomeViewModel) {
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
}