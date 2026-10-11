package pe.edu.upeu.pharmamobil.data.mapper
import pe.edu.upeu.pharmamobil.data.remote.dto.*
import pe.edu.upeu.pharmamobil.domain.model.Producto
fun ProductoResponseDto.toDomain(): Producto = Producto(id, nombre, precio, stock, estado, categoriaId)
fun Producto.toRequest(categoriaPorDefecto: Long): ProductoRequestDto = ProductoRequestDto(nombre, precio, stock, estado, categoriaId ?: categoriaPorDefecto)
