package pe.edu.upeu.pharmamobil.data.remote
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.logging.LogLevel
import kotlinx.coroutines.CancellationException
import pe.edu.upeu.pharmamobil.data.remote.ProductoApi
import pe.edu.upeu.pharmamobil.data.remote.crearHttpClient
import pe.edu.upeu.pharmamobil.domain.repository.DiagnosticoConexion
class DiagnosticoConexionRest(private val engine: HttpClientEngine, override val urlBase: String) : DiagnosticoConexion {
override suspend fun ejecutar(indice: Int): String {
    val client = crearHttpClient(engine, urlBase,
        requestTimeoutMillis = if (indice == 3) 1 else 15_000,
        ignoreUnknownKeys = indice != 4, nivelLog = LogLevel.ALL)
    try {
        val api = ProductoApi(client)
        if (indice == 1) {
            api.buscar(Long.MAX_VALUE)
            return "NO CUMPLE: el servidor devolvió un recurso que debía ser inexistente."
        }
        if (indice == 4) {
            var estricto = ""
            try {
                api.listar()
                return "NO CUMPLE: no hubo campos desconocidos en el JSON recibido."
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) {
                if (!esErrorJson(e) && (e as? pe.edu.upeu.pharmamobil.domain.error.ErrorApiException)?.error != pe.edu.upeu.pharmamobil.domain.error.ErrorApi.RespuestaInvalida) throw e
                estricto = describir(e)
            }
            val tolerante = crearHttpClient(engine, urlBase, nivelLog = LogLevel.ALL)
            try {
                val pagina = ProductoApi(tolerante).listar()
                return "CUMPLE: ignoreUnknownKeys=false produjo $estricto.\nCon true: HTTP 200, ${pagina.contenido.size} productos.\nMensaje: los campos adicionales no interrumpen la carga."
            } finally { tolerante.close() }
        }
        val pagina = api.listar()
        if (indice == 2 || indice == 3) return "NO CUMPLE: la petición devolvió HTTP 200; revisa la preparación del escenario."
        return "CUMPLE: HTTP 200 · ${pagina.contenido.size} productos\n" + pagina.contenido.joinToString("\n") { "${it.nombre} · S/ ${it.precio} · stock ${it.stock}" }
    } catch (e: CancellationException) { throw e }
    catch (e: pe.edu.upeu.pharmamobil.domain.error.ErrorApiException) {
        return if (indice == 1 && e.error == pe.edu.upeu.pharmamobil.domain.error.ErrorApi.NoEncontrado)
            "CUMPLE: ClientRequestException · HTTP 404\nMensaje: Producto no encontrado. La interfaz sigue operativa."
        else if (indice == 3 && e.error == pe.edu.upeu.pharmamobil.domain.error.ErrorApi.TiempoAgotado) "CUMPLE: tiempo de espera agotado y traducido a ErrorApi.TiempoAgotado."
        else if (indice == 2 && e.error == pe.edu.upeu.pharmamobil.domain.error.ErrorApi.SinConexion) "CUMPLE: error de red traducido a ErrorApi.SinConexion."
        else "NO CUMPLE: ${e.message}."
    } catch (e: HttpRequestTimeoutException) {
        return if (indice == 3) "CUMPLE: HttpRequestTimeoutException · límite 1 ms\nMensaje: La solicitud tardó demasiado. Intenta nuevamente. La interfaz sigue operativa."
        else "NO CUMPLE: tiempo de espera agotado fuera del escenario previsto."
    } catch (e: Exception) {
        return if (indice == 2 && esErrorRed(e))
            "CUMPLE: ${describir(e)}\nMensaje: Sin conexión. Comprueba tu conexión e intenta nuevamente. La interfaz sigue operativa."
        else "NO CUMPLE: ${describir(e)}\nMensaje: No se pudo completar la prueba."
    } finally { client.close() }
}

private fun esErrorJson(e: Throwable): Boolean = generateSequence(e) { it.cause }
    .any { it::class.simpleName.orEmpty().let { n -> n.contains("Serialization") || n.contains("Json") } }
private fun esErrorRed(e: Throwable): Boolean = generateSequence(e) { it.cause }
    .any { it::class.simpleName.orEmpty().let { n -> n.contains("Connect") || n.contains("Host") || n.contains("Socket") || n.contains("IOException") || n.contains("Address") } }
private fun describir(e: Throwable): String = generateSequence(e) { it.cause }
    .joinToString(" → ") { it::class.simpleName ?: "Excepción" }

}
