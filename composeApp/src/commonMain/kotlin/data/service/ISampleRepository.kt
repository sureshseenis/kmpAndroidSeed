package data.service

import data.response.SampleDataItem

interface ISampleRepository {
    suspend fun fetchSampleData(): ArrayList<SampleDataItem>
}