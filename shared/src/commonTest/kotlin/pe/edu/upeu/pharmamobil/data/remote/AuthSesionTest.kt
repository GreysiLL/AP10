package pe.edu.upeu.pharmamobil.data.remote
import io.ktor.client.engine.mock.*
import io.ktor.client.request.*
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import pe.edu.upeu.pharmamobil.data.local.AlmacenSeguro
import pe.edu.upeu.pharmamobil.data.repository.*
import pe.edu.upeu.pharmamobil.domain.model.*
import kotlin.test.*
class AuthSesionTest {
    private class Almacen : AlmacenSeguro {
        var token: String? = null
        override fun guardarToken(token: String) { this.token = token }
        override fun leerToken() = token
        override fun borrarToken() { token = null }
    }
    private val json = headersOf(HttpHeaders.ContentType, "application/json")
    private fun login(token: String, username: String = "admin") = """{"token":"$token","tipo":"Bearer","expiraEnSegundos":3600,"username":"$username","nombreCompleto":"Usuario prueba","roles":["ADMIN"],"permisos":["CATALOGO_LEER","CATALOGO_ESCRIBIR"]}"""
    @Test fun bearerViajaEnPrimeraPeticionSinReintento() = runTest {
        val store = Almacen().also { it.token = "token-prueba" };var calls = 0
        val client = crearHttpClient(MockEngine { req -> calls++;assertEquals("Bearer token-prueba",req.headers[HttpHeaders.Authorization]);respond("{}",headers=json) }, "http://localhost/api/v1/", almacen=store)
        try { client.get("productos");assertEquals(1,calls) } finally { client.close() }
    }
    @Test fun loginNuncaEnviaTokenAnteriorNiLoRegistra() = runTest {
        val store = Almacen().also { it.token = "anterior" };val estado = EstadoSesion(store)
        val client = crearHttpClient(MockEngine { req -> assertNull(req.headers[HttpHeaders.Authorization]);respond(login("nuevo"),headers=json) },"http://localhost/api/v1/",almacen=store,estadoSesion=estado,esDepuracion=false)
        try { val sesion=AuthRepositorioRest(client,store,estado).iniciarSesion(Credenciales("admin","clave"));assertEquals("nuevo",store.token);assertTrue(sesion.puede("CATALOGO_ESCRIBIR")) } finally { client.close() }
    }
    @Test fun logoutYLoginOtroUsuarioInvalidanCacheBearer() = runTest {
        val store=Almacen();val estado=EstadoSesion(store);val enviados=mutableListOf<String?>();var cuenta=0
        val client=crearHttpClient(MockEngine { req ->
            if(req.url.encodedPath.endsWith("auth/login")) { cuenta++;respond(login("token-$cuenta"),headers=json) }
            else { enviados+=req.headers[HttpHeaders.Authorization];respond("{}",headers=json) }
        },"http://localhost/api/v1/",almacen=store,estadoSesion=estado,esDepuracion=false)
        try {
            val repo=AuthRepositorioRest(client,store,estado)
            repo.iniciarSesion(Credenciales("admin","clave"));client.get("productos")
            repo.cerrarSesion();assertNull(store.token);assertNull(estado.sesion.value)
            repo.iniciarSesion(Credenciales("vendedor","clave"));client.get("productos")
            assertEquals(listOf<String?>("Bearer token-1","Bearer token-2"),enviados.toList())
        } finally { client.close() }
    }
    @Test fun respuesta401ProtegidaBorraSesionYToken() = runTest {
        val store=Almacen().also { it.token="viejo" };val estado=EstadoSesion(store)
        estado.activar(Sesion("viejo","admin","Admin",setOf("CATALOGO_LEER")))
        val client=crearHttpClient(MockEngine { respond("{}",HttpStatusCode.Unauthorized,json) },"http://localhost/api/v1/",almacen=store,estadoSesion=estado,esDepuracion=false)
        try { assertFailsWith<ClientRequestException> { client.get("productos") };assertNull(store.token);assertNull(estado.sesion.value);assertNotNull(estado.aviso.value) } finally { client.close() }
    }
    @Test fun respuesta403ConservaLaSesion() = runTest {
        val store=Almacen().also { it.token="activo" };val estado=EstadoSesion(store)
        val sesion=Sesion("activo","almacen","Almacén",setOf("CATALOGO_LEER"));estado.activar(sesion)
        val client=crearHttpClient(MockEngine { respond("{}",HttpStatusCode.Forbidden,json) },"http://localhost/api/v1/",almacen=store,estadoSesion=estado,esDepuracion=false)
        try { assertFailsWith<ClientRequestException> { client.get("clientes") };assertEquals("activo",store.token);assertEquals(sesion,estado.sesion.value);assertNull(estado.aviso.value) } finally { client.close() }
    }
    @Test fun restauracionConsultaMeConTokenPersistido() = runTest {
        val store=Almacen().also { it.token="guardado" };val estado=EstadoSesion(store)
        val client=crearHttpClient(MockEngine { req -> assertEquals("Bearer guardado",req.headers[HttpHeaders.Authorization]);respond("""{"username":"almacen","empleadoNombre":"Jorge","permisos":["CATALOGO_LEER"]}""",headers=json) },"http://localhost/api/v1/",almacen=store,estadoSesion=estado,esDepuracion=false)
        try { val s=AuthRepositorioRest(client,store,estado).sesionGuardada();assertEquals("almacen",s?.usuario);assertFalse(s!!.puede("CLIENTES_LEER")) } finally { client.close() }
    }
    @Test fun tokenNoSeEnviaFueraDelOrigenDePharmaSoft() = runTest {
        val store=Almacen().also { it.token="guardado" }
        val client=crearHttpClient(MockEngine { req -> assertNull(req.headers[HttpHeaders.Authorization]);respond("{}",headers=json) },"http://localhost/api/v1/",almacen=store,esDepuracion=false)
        try { client.get("http://otro-host/api/v1/productos") } finally { client.close() }
    }
    @Test fun login401EsCredencialesInvalidasSinAvisoDeExpiracion() = runTest {
        val store=Almacen();val estado=EstadoSesion(store)
        val client=crearHttpClient(MockEngine { respond("{}",HttpStatusCode.Unauthorized,json) },"http://localhost/api/v1/",almacen=store,estadoSesion=estado,esDepuracion=false)
        try { val e=assertFailsWith<ErrorSesionException> { AuthRepositorioRest(client,store,estado).iniciarSesion(Credenciales("admin","mal")) };assertEquals(ErrorSesion.CredencialesInvalidas,e.error);assertNull(estado.aviso.value) } finally { client.close() }
    }
}
