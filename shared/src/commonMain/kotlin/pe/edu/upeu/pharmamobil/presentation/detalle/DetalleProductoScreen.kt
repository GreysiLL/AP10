package pe.edu.upeu.pharmamobil.presentation.detalle
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
@Composable
fun DetalleProductoScreen(estado: DetalleProductoUiState, onVolver: () -> Unit, onReintentar: () -> Unit, onCompartir: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        item { TextButton(onClick = onVolver) { Icon(Icons.Outlined.ArrowBack, null, Modifier.size(18.dp)); Spacer(Modifier.width(8.dp)); Text("Volver a productos") } }
        when {
            estado.cargando -> item { CircularProgressIndicator(); Text("Cargando producto…") }
            estado.error != null -> item { Text(estado.error, color = MaterialTheme.colorScheme.error); Button(onClick = onReintentar) { Text("Reintentar") } }
            estado.producto == null -> item { Text("No se encontró el producto.") }
            else -> item {
                val producto = estado.producto
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)) {
                    Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(22.dp)) {
                        Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.primary) { Icon(Icons.Outlined.Medication, null, Modifier.padding(14.dp).size(28.dp)) }
                        Text(producto.nombre, style = MaterialTheme.typography.headlineSmall)
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) { Text("Precio", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(producto.precio, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary) }
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) { Text("Stock disponible", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(producto.stock, style = MaterialTheme.typography.headlineSmall) }
                        }
                        OutlinedButton(onClick = onCompartir, shape = MaterialTheme.shapes.small, modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) { Icon(Icons.Outlined.Share, null, Modifier.size(18.dp)); Spacer(Modifier.width(10.dp)); Text("Compartir") }
                        estado.errorCompartir?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                    }
                }
            }
        }
    }
}
