package pe.edu.upeu.pharmamobil.presentation.conexion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import kotlin.time.Clock

/** Pantalla de laboratorio, accesible solamente desde la Activity debug de Android. */
@Composable
fun PruebasConexionScreen() {
    val diagnostico: pe.edu.upeu.pharmamobil.domain.repository.DiagnosticoConexion = koinInject()
    val urlBase = diagnostico.urlBase
    val scope = rememberCoroutineScope()
    var ocupado by remember { mutableStateOf(false) }
    var resultado by remember { mutableStateOf("Selecciona una prueba. Para Sin conexión activa modo avión y desactiva Wi-Fi antes de ejecutar.") }
    var fecha by remember { mutableStateOf("") }
    val opciones = listOf("1. GET exitoso", "2. Recurso inexistente", "3. Sin conexión", "4. Tiempo de espera", "5. Campo desconocido")
    Column(Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("S07 · Pruebas de conexión", style = MaterialTheme.typography.titleLarge)
        Text(urlBase, style = MaterialTheme.typography.bodySmall)
        opciones.forEachIndexed { indice, titulo ->
            Button(enabled = !ocupado, onClick = {
                ocupado = true
                fecha = Clock.System.now().toString()
                resultado = "Ejecutando: $titulo"
                scope.launch {
                    try { resultado = titulo + "\n" + diagnostico.ejecutar(indice) }
                    finally { ocupado = false }
                }
            }, modifier = Modifier.fillMaxWidth()) { Text(titulo) }
        }
        if (ocupado) CircularProgressIndicator()
        Text("Fecha UTC: $fecha", style = MaterialTheme.typography.bodySmall)
        HorizontalDivider()
        Text(resultado, style = MaterialTheme.typography.bodyMedium)
    }
}
