package pe.edu.upeu.pharmamobil.data.repository
import io.ktor.client.plugins.ResponseException
import kotlinx.coroutines.CancellationException
import pe.edu.upeu.pharmamobil.data.mapper.toDomain
import pe.edu.upeu.pharmamobil.data.remote.ProductoApi
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository

class ProductoRepositorioRest(
    private val api: ProductoApi,
    private val respaldoLocal: ProductoRepositorioEnMemoria
) : ProductoRepository {
    override suspend fun listar(): List<Producto> = try {
        api.listar().contenido.map { it.toDomain() }
    } catch (e: CancellationException) {
        throw e
    } catch (e: ResponseException) {
        throw IllegalStateException("El servidor respondió con error ${e.response.status.value}. Intenta nuevamente.", e)
    } catch (e: Exception) {
        throw IllegalStateException("No se pudo cargar el inventario. Comprueba la conexión y que PharmaSoft esté funcionando.", e)
    }

    // Sesión 7: POST remoto se implementa en la sesión 8.
    override suspend fun registrar(producto: Producto): Producto = respaldoLocal.registrar(producto)
}
