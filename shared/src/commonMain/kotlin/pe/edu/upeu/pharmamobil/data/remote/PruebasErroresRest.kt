package pe.edu.upeu.pharmamobil.data.remote
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.plugin
import io.ktor.client.plugins.logging.LogLevel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlin.time.Clock
import pe.edu.upeu.pharmamobil.domain.error.*
import pe.edu.upeu.pharmamobil.domain.repository.*
import pe.edu.upeu.pharmamobil.data.remote.dto.ProductoRequestDto
class PruebasErroresRest(private val engine: HttpClientEngine, private val urlBase: String) : PruebasErrores {
 override suspend fun ejecutar(numero: Int): ResultadoError {
  val client=crearHttpClient(engine,urlBase,requestTimeoutMillis=if(numero==7) 1 else 15000,nivelLog=LogLevel.ALL)
  if(numero==8) client.plugin(HttpSend).intercept { request ->
   println("AA08: EnCurso; envio suspendido 5 segundos para comprobar cancelacion al salir")
   delay(5000)
   execute(request)
  }
  val api=ProductoApi(client)
  val nombre="AA08-${numero}-${Clock.System.now().toEpochMilliseconds()}"
  val valido=ProductoRequestDto(nombre,2.5,5,categoriaId=1)
  var limpiar: Long?=null
  var peticion=""
  try {
   when(numero) {
    1 -> { peticion="POST productos; nombre=ab, precio=2.5, stock=5, estado=true, categoriaId=1";api.crear(valido.copy(nombre="ab")) }
    2 -> { peticion="POST productos; nombre=$nombre, precio=0, stock=5, estado=true, categoriaId=1";api.crear(valido.copy(precio=0.0)) }
    3 -> { peticion="PUT productos/9223372036854775807 con cuerpo valido";api.actualizar(Long.MAX_VALUE,valido) }
    4 -> {
     val p=api.crear(valido);limpiar=p.id
     api.eliminar(p.id);limpiar=null
     peticion="DELETE productos/${p.id}; primera llamada HTTP 204; repetir DELETE"
     api.eliminar(p.id)
    }
    5 -> {
     val p=api.crear(valido);limpiar=p.id
     peticion="POST productos; repetir nombre $nombre del producto ${p.id}"
     api.crear(valido)
    }
    6 -> { peticion="GET productos con PharmaSoft detenido";api.listar() }
    7 -> { peticion="GET productos; requestTimeoutMillis=1 ms";api.listar() }
    8 -> { peticion="GET productos; retraso controlado 5 s antes del envio; salir de Activity";api.listar() }
    else -> error("Escenario no reconocido")
   }
   return ResultadoError(peticion,200,null,"No se produjo el fallo previsto; revisar la preparacion.")
  } catch(e: CancellationException) {
   println("AA08: CancellationException relanzada; no se convierte en ErrorApi ni se publica mensaje de error")
   throw e
  } catch(e: ErrorApiException) {
   println("AA08: escenario=$numero HTTP=${e.codigoHttp ?: "sin respuesta"} ErrorApi=${e.error} mensaje=${e.message}")
   return ResultadoError(peticion,e.codigoHttp,e.error,e.message ?: mensajeDe(e.error))
  } finally {
   if(limpiar!=null) {
    // Solo el producto exclusivo del escenario de conflicto requiere limpieza.
    // La cancelacion se relanza; nunca se encubre como un fallo de limpieza.
    try { api.eliminar(limpiar) }
    catch(e: CancellationException) { throw e }
    catch(e: Exception) { println("AA08: limpieza pendiente para ID=$limpiar: ${e.message}") }
   }
   client.close()
  }
 }
}

