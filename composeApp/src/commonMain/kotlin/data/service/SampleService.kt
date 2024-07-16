package data.service

import data.response.SampleDataItem
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class SampleService(private val baseUrl: String, private val client: HttpClient) : ISampleService{
    override suspend fun fetchSampleData(): ArrayList<SampleDataItem> = client.get(baseUrl+"albums/1/photos").body()
}