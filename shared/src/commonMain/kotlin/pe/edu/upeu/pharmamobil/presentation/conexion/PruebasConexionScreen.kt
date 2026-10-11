package pe.edu.upeu.pharmamobil.presentation.conexion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.logging.LogLevel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import pe.edu.upeu.pharmamobil.data.remote.ProductoApi
import pe.edu.upeu.pharmamobil.data.remote.crearHttpClient
import kotlin.time.Clock

/** Pantalla de laboratorio, accesible solamente desde la Activity debug de Android. */
@Composable
fun PruebasConexionScreen() {
    val engine: HttpClientEngine = koinInject()
    val urlBase: String = koinInject(qualifier = named("urlBase"))
    val scope = rememberCoroutineScope()
    var ocupado by remember { mutableStateOf(false) }
    var resultado by remember { mutableStateOf("Selecciona una prueba. Para Sin conexión activa modo avión y desactiva Wi-Fi antes de ejecutar.") }
    var fecha by remember { mutableStateOf("") }
    val opciones = listOf("1. GET exitoso", "2. Recurso inexistente", "3. Sin conexión", "4. Tiempo de espera", "5. Campo desconocido")
    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("S07 · Pruebas de conexión", style = MaterialTheme.typography.titleLarge)
        Text(urlBase, style = MaterialTheme.typography.bodySmall)
        opciones.forEachIndexed { indice, titulo ->
            Button(enabled = !ocupado, onClick = {
                ocupado = true
                fecha = Clock.System.now().toString()
                resultado = "Ejecutando: $titulo"
                scope.launch {
                    try { resultado = titulo + "\n" + ejecutarPrueba(indice, engine, urlBase) }
                    finally { ocupado = false }
                }
            }, modifier = Modifier.fillMaxWidth()) { Text(titulo) }
        }
        if (ocupado) CircularProgressIndicator()
        Text("Fecha UTC: $fecha", style = MaterialTheme.typography.bodySmall)
        HorizontalDivider()
        Text(resultado, style = MaterialTheme.typography.bodyMedium)
    }
}

private suspend fun ejecutarPrueba(indice: Int, engine: HttpClientEngine, urlBase: String): String {
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
                if (!esErrorJson(e)) throw e
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
    catch (e: ClientRequestException) {
        return if (indice == 1 && e.response.status.value == 404)
            "CUMPLE: ClientRequestException · HTTP 404\nMensaje: Producto no encontrado. La interfaz sigue operativa."
        else "NO CUMPLE: error HTTP ${e.response.status.value}."
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
