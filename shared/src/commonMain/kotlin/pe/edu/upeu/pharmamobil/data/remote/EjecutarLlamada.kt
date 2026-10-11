package pe.edu.upeu.pharmamobil.data.remote
import io.ktor.client.call.body
import io.ktor.client.plugins.*
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import pe.edu.upeu.pharmamobil.data.remote.dto.ErrorResponseDto
import pe.edu.upeu.pharmamobil.domain.error.*
suspend fun <T> ejecutarLlamada(bloque: suspend () -> T): Result<T> = try {
 Result.success(bloque())
} catch(e: CancellationException) { throw e
} catch(e: ClientRequestException) {
 val cuerpo = try { e.response.body<ErrorResponseDto>() }
 catch(c: CancellationException) { throw c }
 catch(_: Exception) { null }
 val error = when(e.response.status.value) {
  400 -> if (cuerpo?.validationErrors.isNullOrEmpty()) ErrorApi.Conflicto(cuerpo?.message ?: "Solicitud no válida.") else ErrorApi.Validacion(cuerpo!!.validationErrors)
  404 -> ErrorApi.NoEncontrado
  409 -> ErrorApi.Conflicto(cuerpo?.message ?: "Operación no permitida.")
  else -> ErrorApi.Servidor
 }
 Result.failure(ErrorApiException(error))
} catch(e: ServerResponseException) { Result.failure(ErrorApiException(ErrorApi.Servidor))
} catch(e: HttpRequestTimeoutException) { Result.failure(ErrorApiException(ErrorApi.TiempoAgotado))
} catch(e: ConnectTimeoutException) { Result.failure(ErrorApiException(ErrorApi.TiempoAgotado))
} catch(e: SocketTimeoutException) { Result.failure(ErrorApiException(ErrorApi.TiempoAgotado))
} catch(e: IOException) { Result.failure(ErrorApiException(ErrorApi.SinConexion))
} catch(e: JsonConvertException) { Result.failure(ErrorApiException(ErrorApi.RespuestaInvalida))
} catch(e: SerializationException) { Result.failure(ErrorApiException(ErrorApi.RespuestaInvalida))
}
