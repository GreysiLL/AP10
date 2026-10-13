package pe.edu.upeu.pharmamobil.data.repository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.*
import kotlinx.serialization.Serializable
import pe.edu.upeu.pharmamobil.data.remote.ejecutarLlamada
import pe.edu.upeu.pharmamobil.data.remote.dto.PaginaResponseDto
import pe.edu.upeu.pharmamobil.domain.model.Cliente
import pe.edu.upeu.pharmamobil.domain.repository.ClienteRepository
@Serializable private data class ClienteRespuesta(val id: Long, val dni: String, val nombres: String, val apellidos: String, val email: String, val telefono: String? = null, val estado: Boolean)
@Serializable private data class ClientePeticion(val dni: String, val nombres: String, val apellidos: String, val email: String, val telefono: String?, val direccion: String? = null, val estado: Boolean = true)
private fun ClienteRespuesta.dominio() = Cliente(id, "$nombres $apellidos", email, telefono, dni, apellidos)
class ClienteRepositorioRest(private val cliente: HttpClient) : ClienteRepository {
    override suspend fun registrar(cliente: Cliente): Cliente = ejecutarLlamada {
        require(cliente.dni.matches(Regex("[0-9]{8}"))) { "El DNI debe tener 8 dígitos." }
        require(cliente.apellidos.length >= 2) { "Ingresa los apellidos del cliente." }
        this.cliente.post("clientes") { setBody(ClientePeticion(cliente.dni, cliente.nombre, cliente.apellidos, cliente.correo, cliente.telefono)) }.body<ClienteRespuesta>().dominio()
    }.getOrThrow()
    override suspend fun listar(): List<Cliente> = ejecutarLlamada {
        val resultado = mutableListOf<Cliente>();var pagina = 0
        do {
            val respuesta = cliente.get("clientes") { parameter("pagina", pagina);parameter("tamanio",100) }.body<PaginaResponseDto<ClienteRespuesta>>()
            resultado += respuesta.contenido.filter { it.estado }.map { it.dominio() }
            pagina++
        } while (!respuesta.ultima)
        resultado
    }.getOrThrow()
}
