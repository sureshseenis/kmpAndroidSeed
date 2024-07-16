package di

import data.service.ISampleRepository
import data.service.ISampleService
import data.service.SampleRepositoryImpl
import data.service.SampleService
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module
import screens.home.HomeViewModel

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(ktorModule, repositoryModule, viewModelModule)
    }

val ktorModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                    }
                )
            }
            install(Logging) {
                level = LogLevel.ALL
            }
        }
    }

    single { "https://jsonplaceholder.typicode.com/" }
}

val repositoryModule = module {
    single<ISampleRepository> { SampleRepositoryImpl(get()) }
    single<ISampleService> { SampleService(get(), get()) }
}

val viewModelModule = module {
    factory { HomeViewModel(get()) }
}

//using in iOS
fun initKoin() = initKoin {}