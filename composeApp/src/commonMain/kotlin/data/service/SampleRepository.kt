package data.service

import data.response.SampleDataItem


interface SampleRepository {
    suspend fun fetchSampleData(): ArrayList<SampleDataItem>
}