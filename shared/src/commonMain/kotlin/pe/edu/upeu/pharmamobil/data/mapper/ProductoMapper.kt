package pe.edu.upeu.pharmamobil.data.mapper
import pe.edu.upeu.pharmamobil.data.remote.dto.ProductoResponseDto
import pe.edu.upeu.pharmamobil.domain.model.Producto

fun ProductoResponseDto.toDomain(): Producto = Producto(id, nombre, precio, stock)
