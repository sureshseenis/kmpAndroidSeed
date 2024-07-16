package screens.home

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import data.response.SampleDataItem
import data.service.ISampleService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class HomeViewModel(private val sampleService: ISampleService) : ScreenModel {

    private val job = SupervisorJob()
    private val coroutineContext: CoroutineContext = job + Dispatchers.IO
    private val viewModelScope = CoroutineScope(coroutineContext)
    val sampleData = mutableListOf<SampleDataItem>()
    val homeViewState = mutableStateOf<HomeViewState>(HomeViewState.Loading)

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