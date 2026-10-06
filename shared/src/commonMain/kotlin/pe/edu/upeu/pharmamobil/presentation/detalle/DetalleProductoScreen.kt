package pe.edu.upeu.pharmamobil.presentation.detalle
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable
fun DetalleProductoScreen(estado: DetalleProductoUiState, onVolver: () -> Unit, onReintentar: () -> Unit, onCompartir: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        item { TextButton(onClick = onVolver) { Text("Volver a productos") } }
        when {
            estado.cargando -> item { CircularProgressIndicator(); Text("Cargando producto…") }
            estado.error != null -> item {
                Text(estado.error, color = MaterialTheme.colorScheme.error)
                Button(onClick = onReintentar) { Text("Reintentar") }
            }
            estado.producto == null -> item { Text("No se encontró el producto.") }
            else -> item {
                val producto = estado.producto
                Text(producto.nombre, style = MaterialTheme.typography.headlineSmall)
                Text("Precio: ${producto.precio}")
                Text("Stock: ${producto.stock}")
                Button(onClick = onCompartir) { Text("Compartir") }
                estado.errorCompartir?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}
