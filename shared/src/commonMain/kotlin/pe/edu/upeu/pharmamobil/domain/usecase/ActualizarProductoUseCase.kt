package pe.edu.upeu.pharmamobil.domain.usecase
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository
class ActualizarProductoUseCase(repository: ProductoRepository) {
 private val guardar = RegistrarProductoUseCase(repository)
 suspend operator fun invoke(producto: Producto, nombre: String, precio: String, stock: String): Result<Producto> = guardar(nombre, precio, stock, producto)
}
