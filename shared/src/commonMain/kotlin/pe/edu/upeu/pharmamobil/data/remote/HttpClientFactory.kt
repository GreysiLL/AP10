package pe.edu.upeu.pharmamobil.data.remote

import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.URLBuilder
import io.ktor.http.encodedPath
import pe.edu.upeu.pharmamobil.data.local.AlmacenSeguro
import pe.edu.upeu.pharmamobil.data.repository.EstadoSesion
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
    nivelLog: LogLevel = LogLevel.HEADERS,
    almacen: AlmacenSeguro? = null,
    estadoSesion: EstadoSesion? = null,
    esDepuracion: Boolean = true
): HttpClient = HttpClient(engine) {
    expectSuccess = true
    install(ContentNegotiation) {
        json(Json { this.ignoreUnknownKeys = ignoreUnknownKeys; encodeDefaults = true; isLenient = true })
    }
    if (almacen != null) {
        val origen = URLBuilder(urlBase).build()
        install(Auth) {
            bearer {
                loadTokens { almacen.leerToken()?.let { BearerTokens(it, "") } }
                sendWithoutRequest { request ->
                    request.url.host == origen.host && request.url.port == origen.port && request.url.protocol == origen.protocol &&
                    request.url.encodedPath.startsWith(origen.encodedPath) && !request.url.encodedPath.endsWith("/auth/login")
                }
            }
        }
        HttpResponseValidator {
            handleResponseExceptionWithRequest { cause, request ->
                if (cause is ClientRequestException && cause.response.status.value == 401 && !request.url.encodedPath.endsWith("/auth/login")) {
                    estadoSesion?.limpiar(expirada = true)
                }
            }
        }
    }
    install(Logging) {
        logger = object : Logger {
            override fun log(message: String) { println("Ktor: $message") }
        }
        level = if (esDepuracion) nivelLog else LogLevel.NONE
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
