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
import pe.edu.upeu.pharmamobil.domain.error.ErrorApi
import pe.edu.upeu.pharmamobil.domain.repository.PruebasErrores
import pe.edu.upeu.pharmamobil.domain.repository.ResultadoError
@Composable
fun PruebasErroresScreen() {
 val pruebas: PruebasErrores=koinInject()
 val scope=rememberCoroutineScope()
 var ocupado by remember { mutableStateOf(false) }
 var resultado by remember { mutableStateOf<ResultadoError?>(null) }
 var fecha by remember { mutableStateOf("") }
 val nombres=listOf("1. Nombre de dos caracteres","2. Precio cero en servidor","3. Actualizar ID inexistente","4. Doble eliminacion","5. Nombre duplicado","6. Servidor detenido","7. Timeout de 1 ms","8. Cancelacion al salir")
 Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).padding(16.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(5.dp)) {
  Text("S08 - Errores controlados",style=MaterialTheme.typography.titleLarge)
  Text("Laboratorio debug. Para 6 detener PharmaSoft. Para 8 iniciar y salir con Atras antes de 5 segundos.",style=MaterialTheme.typography.bodySmall)
  nombres.forEachIndexed { i,n ->
   Button(enabled=!ocupado,onClick={ocupado=true;resultado=null;fecha=Clock.System.now().toString();scope.launch {
    try { resultado=pruebas.ejecutar(i+1) } finally { ocupado=false }
   }},modifier=Modifier.fillMaxWidth()) { Text(n) }
  }
  Text("Fecha UTC: $fecha",style=MaterialTheme.typography.bodySmall)
  if(ocupado) Text("EnCurso: esperando operacion; salir cancela la corrutina.")
  resultado?.let { r ->
   HorizontalDivider()
   Text(r.peticion,style=MaterialTheme.typography.bodySmall)
   Text("HTTP: ${r.codigoHttp ?: "sin respuesta"}")
   Text("ErrorApi: ${r.error?.let { it::class.simpleName } ?: "ninguno"}")
   if(r.error is ErrorApi.Validacion) {
    val campos=r.error.porCampo
    Text("Nombre",style=MaterialTheme.typography.labelLarge)
    campos["nombre"]?.let { Text(it,color=MaterialTheme.colorScheme.error) }
    Text("Precio",style=MaterialTheme.typography.labelLarge)
    campos["precio"]?.let { Text(it,color=MaterialTheme.colorScheme.error) }
    campos.filterKeys { it !in setOf("nombre","precio") }.forEach { (campo,mensaje) -> Text("$campo: $mensaje",color=MaterialTheme.colorScheme.error) }
   } else Text(r.mensaje,color=MaterialTheme.colorScheme.error)
  }
 }
}
