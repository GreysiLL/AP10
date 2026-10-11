package pe.edu.upeu.pharmamobil.data.remote
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import pe.edu.upeu.pharmamobil.data.remote.dto.*
import pe.edu.upeu.pharmamobil.data.repository.ProductoRepositorioRest
import pe.edu.upeu.pharmamobil.domain.error.*
import pe.edu.upeu.pharmamobil.domain.model.Producto
import kotlin.test.*
class CrudProductoTest {
 private val headers = headersOf(HttpHeaders.ContentType,"application/json")
 private val producto = """{"id":8,"nombre":"Prueba","precio":2.5,"stock":3,"estado":true,"categoriaId":7}"""
 @Test fun postYPutEnvianCategoriaEstadoYDatosYDelete204NoLeeCuerpo() = runTest {
  val metodos=mutableListOf<HttpMethod>()
  val engine=MockEngine { req ->
   metodos.add(req.method)
   if(req.method==HttpMethod.Post || req.method==HttpMethod.Put) {
    val json=Json.parseToJsonElement((req.body as TextContent).text).jsonObject
    assertEquals(7L,json.getValue("categoriaId").jsonPrimitive.long)
    assertEquals(false,json.getValue("estado").jsonPrimitive.boolean)
    assertEquals("Prueba",json.getValue("nombre").jsonPrimitive.content)
   }
   if(req.method==HttpMethod.Delete) respond("",HttpStatusCode.NoContent)
   else respond(producto,if(req.method==HttpMethod.Post) HttpStatusCode.Created else HttpStatusCode.OK,headers)
  }
  val client=crearHttpClient(engine,"http://localhost/api/v1/")
  try {
   val repo=ProductoRepositorioRest(ProductoApi(client),1L)
   val p=Producto(8,"Prueba",2.5,3,false,7)
   assertEquals(8L,repo.registrar(p).id)
   repo.obtener(8);repo.actualizar(p);repo.eliminar(8)
   assertEquals(listOf(HttpMethod.Post,HttpMethod.Get,HttpMethod.Put,HttpMethod.Delete),metodos)
  } finally { client.close();engine.close() }
 }
 @Test fun postIncluyeEstadoTruePredeterminadoQueExigeElServidor() = runTest {
  val engine=MockEngine { req ->
   val json=Json.parseToJsonElement((req.body as TextContent).text).jsonObject
   assertEquals(true,json.getValue("estado").jsonPrimitive.boolean)
   assertEquals(1L,json.getValue("categoriaId").jsonPrimitive.long)
   respond(producto,HttpStatusCode.Created,headers)
  }
  val client=crearHttpClient(engine,"http://localhost/api/v1/")
  try { ProductoApi(client).crear(ProductoRequestDto("Prueba",2.5,3,categoriaId=1)) }
  finally { client.close();engine.close() }
 }
 @Test fun interpreta400404409Y500EnDominio() = runTest {
  val casos=listOf(400 to ErrorApi.Validacion(mapOf("nombre" to "Entre 3 y 150 caracteres")),404 to ErrorApi.NoEncontrado,409 to ErrorApi.Conflicto("Duplicado"),500 to ErrorApi.Servidor)
  for((status,esperado) in casos) {
   val engine=MockEngine { respond("""{"message":"Duplicado","validationErrors":{"nombre":"Entre 3 y 150 caracteres"}}""",HttpStatusCode.fromValue(status),headers) }
   val client=crearHttpClient(engine,"http://localhost/api/v1/")
   try { assertEquals(esperado,assertFailsWith<ErrorApiException> { ProductoApi(client).obtener(99) }.error) }
   finally { client.close();engine.close() }
  }
 }
 @Test fun conflictoConValidationErrorsNullConservaMensajeReal() = runTest {
  val engine=MockEngine { respond("""{"timestamp":"2026-10-11T17:38:04","status":409,"error":"Conflict","message":"Ya existe un producto con el nombre Prueba","path":"/api/v1/productos","validationErrors":null}""",HttpStatusCode.Conflict,headers) }
  val client=crearHttpClient(engine,"http://localhost/api/v1/")
  try {
   val e=assertFailsWith<ErrorApiException> { ProductoApi(client).crear(ProductoRequestDto("Prueba",2.5,5,categoriaId=1)) }
   assertEquals(ErrorApi.Conflicto("Ya existe un producto con el nombre Prueba"),e.error)
   assertEquals(409,e.codigoHttp)
  } finally { client.close();engine.close() }
 }
 @Test fun listaTodasLasPaginasYOcultaLaBajaLogica() = runTest {
  val engine=MockEngine { req ->
   val pagina=req.url.parameters["pagina"]!!.toInt()
   val contenido=if(pagina==0) producto else producto.replace("\"id\":8","\"id\":9").replace("\"estado\":true","\"estado\":false")
   respond("""{"contenido":[$contenido],"pagina":$pagina,"tamanio":100,"totalElementos":2,"totalPaginas":2,"ultima":${pagina==1}}""",headers=headers)
  }
  val client=crearHttpClient(engine,"http://localhost/api/v1/")
  try { assertEquals(listOf(8L),ProductoRepositorioRest(ProductoApi(client),1).listar().map { it.id }) }
  finally { client.close();engine.close() }
 }
 @Test fun noConvierteLaCancelacionEnErrorDeInterfaz() = runTest {
  assertFailsWith<CancellationException> { ejecutarLlamada<Unit> { throw CancellationException("cerrada") } }
 }
}
