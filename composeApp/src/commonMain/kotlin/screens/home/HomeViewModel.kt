package screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import data.response.SampleDataItem
import data.service.ISampleService
import kotlinx.coroutines.launch

class HomeViewModel(private val sampleService: ISampleService) : ViewModel() {

    val sampleData = mutableListOf<SampleDataItem>()
    val homeViewState = mutableStateOf<HomeViewState>(HomeViewState.Loading)

    init {
        getSampleData()
    }

    fun getSampleData() {
        viewModelScope.launch {
            try {
                val sampleDataResonse = sampleService.fetchSampleData()
                homeViewState.value = HomeViewState.Success(dataItem = sampleDataResonse)
            } catch (e: Exception) {
                e.printStackTrace()
                homeViewState.value = HomeViewState.Failure(e.message.toString())
            }
        }
    }
}