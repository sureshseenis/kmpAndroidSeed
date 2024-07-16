package screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import data.service.ISampleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class HomeViewModel(private val iSampleRepository: ISampleRepository) : ViewModel() {
    private val job = SupervisorJob()
    private val coroutineContext: CoroutineContext = job + Dispatchers.IO
    private val coroutineScope = CoroutineScope(coroutineContext)
    val homeViewState = mutableStateOf<HomeViewState>(HomeViewState.Loading)

    fun getSampleData() {
        coroutineScope.launch {
            try {
                val sampleDataResponse = iSampleRepository.fetchSampleData()
                homeViewState.value = HomeViewState.Success(dataItem = sampleDataResponse)
            } catch (e: Exception) {
                e.printStackTrace()
                homeViewState.value = HomeViewState.Failure(e.message.toString())
            }
        }
    }
}