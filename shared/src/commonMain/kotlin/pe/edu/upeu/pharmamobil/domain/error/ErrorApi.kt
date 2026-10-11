package pe.edu.upeu.pharmamobil.domain.error
sealed interface ErrorApi {
 data class Validacion(val porCampo: Map<String, String>) : ErrorApi
 data object NoEncontrado : ErrorApi
 data class Conflicto(val mensaje: String) : ErrorApi
 data object Servidor : ErrorApi
 data object SinConexion : ErrorApi
 data object TiempoAgotado : ErrorApi
 data object RespuestaInvalida : ErrorApi
}
class ErrorApiException(val error: ErrorApi) : Exception(mensajeDe(error))
fun mensajeDe(error: ErrorApi): String = when(error) {
 is ErrorApi.Validacion -> "Revisa los campos indicados."
 ErrorApi.NoEncontrado -> "Producto no encontrado."
 is ErrorApi.Conflicto -> error.mensaje
 ErrorApi.Servidor -> "El servidor no pudo completar la operación. Intenta nuevamente."
 ErrorApi.SinConexion -> "Sin conexión. Comprueba tu conexión y que PharmaSoft esté funcionando."
 ErrorApi.TiempoAgotado -> "La solicitud tardó demasiado. Intenta nuevamente."
 ErrorApi.RespuestaInvalida -> "La respuesta del servidor no tiene el formato esperado."
}
