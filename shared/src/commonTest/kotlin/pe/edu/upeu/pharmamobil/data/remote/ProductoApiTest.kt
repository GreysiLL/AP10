package pe.edu.upeu.pharmamobil.data.remote

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import pe.edu.upeu.pharmamobil.data.repository.ProductoRepositorioEnMemoria
import pe.edu.upeu.pharmamobil.data.repository.ProductoRepositorioRest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ProductoApiTest {
    @Test
    fun consumeContratoRealYMapeaLosTresProductos() = runTest {
        val engine = MockEngine { request ->
            assertEquals("/api/v1/productos", request.url.encodedPath)
            assertEquals("0", request.url.parameters["pagina"])
            assertEquals("20", request.url.parameters["tamanio"])
            respond(contratoReal, headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val client = crearHttpClient(engine, "http://localhost:8080/api/v1/")
        try {
            val repository = ProductoRepositorioRest(ProductoApi(client), ProductoRepositorioEnMemoria())
            val productos = repository.listar()
            assertEquals(3, productos.size)
            assertEquals("Paracetamol 500 mg", productos.first().nombre)
            assertEquals(4.5, productos.first().precio)
            assertEquals(120, productos.first().stock)
        } finally { client.close(); engine.close() }
    }

    @Test
    fun errorDelServidorSePropagaConMensajeParaLaInterfaz() = runTest {
        val engine = MockEngine {
            respond("{}", HttpStatusCode.ServiceUnavailable, headersOf(HttpHeaders.ContentType, "application/json"))
        }
        val client = crearHttpClient(engine, "http://localhost:8080/api/v1/")
        try {
            val repository = ProductoRepositorioRest(ProductoApi(client), ProductoRepositorioEnMemoria())
            val error = assertFailsWith<IllegalStateException> { repository.listar() }
            assertTrue(error.message.orEmpty().contains("503"))
        } finally { client.close(); engine.close() }
    }

    @Test
    fun falloDeConexionSePropagaConMensajeComprensible() = runTest {
        val engine = MockEngine { throw IllegalStateException("Sin conexión") }
        val client = crearHttpClient(engine, "http://localhost:8080/api/v1/")
        try {
            val repository = ProductoRepositorioRest(ProductoApi(client), ProductoRepositorioEnMemoria())
            val error = assertFailsWith<IllegalStateException> { repository.listar() }
            assertTrue(error.message.orEmpty().contains("Comprueba la conexión"))
        } finally { client.close(); engine.close() }
    }

    private val contratoReal = """{
  "contenido": [
    {
      "id": 1,
      "nombre": "Paracetamol 500 mg",
      "precio": 4.5,
      "stock": 120,
      "estado": true,
      "categoriaId": 1,
      "categoriaNombre": "Analgésicos",
      "fechaCreacion": "2026-10-09T16:45:39.588859",
      "fechaModificacion": null
    },
    {
      "id": 2,
      "nombre": "Ibuprofeno 400 mg",
      "precio": 6.8,
      "stock": 80,
      "estado": true,
      "categoriaId": 1,
      "categoriaNombre": "Analgésicos",
      "fechaCreacion": "2026-10-09T16:45:39.672464",
      "fechaModificacion": null
    },
    {
      "id": 3,
      "nombre": "Naproxeno 550 mg",
      "precio": 8.5,
      "stock": 60,
      "estado": true,
      "categoriaId": 1,
      "categoriaNombre": "Analgésicos",
      "fechaCreacion": "2026-10-09T16:45:39.73848",
      "fechaModificacion": null
    }
  ],
  "pagina": 0,
  "tamanio": 20,
  "totalElementos": 3,
  "totalPaginas": 1,
  "ultima": true
}
"""
}
