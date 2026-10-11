package pe.edu.upeu.pharmamobil.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun crearHttpClient(
    engine: HttpClientEngine,
    urlBase: String,
    requestTimeoutMillis: Long = 15_000,
    ignoreUnknownKeys: Boolean = true,
    nivelLog: LogLevel = LogLevel.HEADERS
): HttpClient = HttpClient(engine) {
    expectSuccess = true
    install(ContentNegotiation) {
        json(Json { this.ignoreUnknownKeys = ignoreUnknownKeys; encodeDefaults = true; isLenient = true })
    }
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) { println("Ktor: $message") }
        }
        level = nivelLog
        sanitizeHeader { it == HttpHeaders.Authorization }
    }
    install(HttpTimeout) {
        this.requestTimeoutMillis = requestTimeoutMillis
        connectTimeoutMillis = 10_000
    }
    defaultRequest {
        url(urlBase)
        contentType(ContentType.Application.Json)
    }
}
