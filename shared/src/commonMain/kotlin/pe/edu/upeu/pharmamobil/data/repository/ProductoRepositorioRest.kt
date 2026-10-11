package pe.edu.upeu.pharmamobil.data.repository
import pe.edu.upeu.pharmamobil.data.mapper.*
import pe.edu.upeu.pharmamobil.data.remote.ProductoApi
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository
class ProductoRepositorioRest(private val api: ProductoApi, private val categoriaPorDefecto: Long) : ProductoRepository {
 override suspend fun listar(): List<Producto> {
  val productos = mutableListOf<Producto>()
  var numero = 0
  do {
   val pagina = api.listar(numero, 100)
   productos.addAll(pagina.contenido.filter { it.estado }.map { it.toDomain() })
   numero++
  } while (!pagina.ultima)
  return productos
 }
 override suspend fun obtener(id: Long): Producto = api.obtener(id).toDomain()
 override suspend fun registrar(producto: Producto): Producto = api.crear(producto.toRequest(categoriaPorDefecto)).toDomain()
 override suspend fun actualizar(producto: Producto): Producto = api.actualizar(producto.id, producto.toRequest(categoriaPorDefecto)).toDomain()
 override suspend fun eliminar(id: Long) = api.eliminar(id)
}
