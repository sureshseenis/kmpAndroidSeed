package screens.home

import data.response.SampleDataItem

sealed interface HomeViewState {
    data object Loading : HomeViewState
    data class Success(
        val dataItem: ArrayList<SampleDataItem>,
    ) : HomeViewState

    data class Failure(val error: String) : HomeViewState
}