package data.service

import data.response.SampleDataItem

class SampleRepositoryImpl(private val sampleApi: ISampleService): ISampleRepository {

    override suspend fun fetchSampleData(): ArrayList<SampleDataItem> {
      return sampleApi.fetchSampleData()
    }
}