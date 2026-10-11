package pe.edu.upeu.pharmamobil.domain.repository
import pe.edu.upeu.pharmamobil.domain.error.ErrorApi
data class ResultadoError(val peticion: String, val codigoHttp: Int?, val error: ErrorApi?, val mensaje: String)
interface PruebasErrores { suspend fun ejecutar(numero: Int): ResultadoError }
