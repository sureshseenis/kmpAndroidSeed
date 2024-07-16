package data.service

import data.response.SampleDataItem

interface ISampleService {
    suspend fun fetchSampleData(): ArrayList<SampleDataItem>
}