package pe.edu.upeu.pharmamobil
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.request.get
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext
import pe.edu.upeu.pharmamobil.data.local.AlmacenSeguro
import pe.edu.upeu.pharmamobil.data.repository.EstadoSesion
import pe.edu.upeu.pharmamobil.data.remote.ejecutarLlamada
class PruebasSeguridadActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val koin = GlobalContext.get()
        val cliente = koin.get<HttpClient>()
        val estado = koin.get<EstadoSesion>()
        setContent { MaterialTheme {
            val sesion by estado.sesion.collectAsState()
            var resultado by remember { mutableStateOf("Elige una comprobación.") }
            var trabajando by remember { mutableStateOf(false) }
            val scope = rememberCoroutineScope()
            Column(Modifier.fillMaxSize().safeDrawingPadding().padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Pruebas de seguridad S10", style = MaterialTheme.typography.titleLarge)
                Text("Usuario: ${sesion?.usuario ?: "Sin sesión"}")
                Button(enabled = !trabajando, onClick = { scope.launch {
                    trabajando = true
                    val r = ejecutarLlamada { cliente.get("clientes").status.value }
                    resultado = r.fold({ "HTTP $it: acceso permitido." }, { "${it.message} HTTP ${(it as? pe.edu.upeu.pharmamobil.domain.error.ErrorApiException)?.codigoHttp}" })
                    trabajando = false
                } }) { Text("Consultar clientes (403 con almacén)") }
                Button(enabled = !trabajando, onClick = { scope.launch {
                    trabajando = true
                    koin.get<AlmacenSeguro>().guardarToken("token-invalido-prueba-S10")
                    cliente.authProvider<BearerAuthProvider>()?.clearToken()
                    val r = ejecutarLlamada { cliente.get("productos").status.value }
                    resultado = r.fold({ "HTTP $it" }, { "${it.message} HTTP ${(it as? pe.edu.upeu.pharmamobil.domain.error.ErrorApiException)?.codigoHttp}" })
                    trabajando = false
                } }) { Text("Comprobar cierre por 401") }
                Text(resultado)
                Text("Sesión activa: ${sesion != null}")
                TextButton(onClick = { finish() }) { Text("Volver a PharmaMobil") }
            }
        } }
    }
}
