package pe.edu.upeu.pharmamobil.data.remote
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import pe.edu.upeu.pharmamobil.data.remote.dto.*
class ProductoApi(private val client: HttpClient) {
 suspend fun listar(pagina: Int = 0, tamanio: Int = 20): PaginaResponseDto<ProductoResponseDto> = ejecutarLlamada {
  client.get("productos") { parameter("pagina", pagina); parameter("tamanio", tamanio) }.body<PaginaResponseDto<ProductoResponseDto>>()
 }.getOrThrow()
 suspend fun obtener(id: Long): ProductoResponseDto = ejecutarLlamada { client.get("productos/$id").body<ProductoResponseDto>() }.getOrThrow()
 suspend fun buscar(id: Long): ProductoResponseDto = obtener(id)
 suspend fun crear(request: ProductoRequestDto): ProductoResponseDto = ejecutarLlamada { client.post("productos") { setBody(request) }.body<ProductoResponseDto>() }.getOrThrow()
 suspend fun actualizar(id: Long, request: ProductoRequestDto): ProductoResponseDto = ejecutarLlamada { client.put("productos/$id") { setBody(request) }.body<ProductoResponseDto>() }.getOrThrow()
 suspend fun eliminar(id: Long) { ejecutarLlamada { client.delete("productos/$id"); Unit }.getOrThrow() }
}
