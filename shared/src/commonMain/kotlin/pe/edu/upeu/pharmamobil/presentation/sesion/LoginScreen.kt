package pe.edu.upeu.pharmamobil.presentation.sesion
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
@Composable
fun LoginScreen(estado: SesionUiState, aviso: String?, usuario: (String) -> Unit, clave: (String) -> Unit, login: () -> Unit, restaurar: () -> Unit) {
    Column(Modifier.fillMaxSize().safeDrawingPadding().imePadding().verticalScroll(rememberScrollState()).padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("PharmaMobil", style = MaterialTheme.typography.headlineLarge)
        Text("Iniciar sesión", style = MaterialTheme.typography.titleLarge)
        Text("Ingresa con tu cuenta para acceder al inventario.")
        if(estado.iniciando) { CircularProgressIndicator(); Text("Comprobando sesión…") }
        else {
            aviso?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            OutlinedTextField(estado.usuario, usuario, label = { Text("Usuario") }, singleLine = true, enabled = !estado.cargando, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(estado.clave, clave, label = { Text("Contraseña") }, singleLine = true, enabled = !estado.cargando, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth())
            estado.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            Button(login, enabled = !estado.cargando, modifier = Modifier.fillMaxWidth()) { Text(if(estado.cargando) "Ingresando…" else "Ingresar") }
            if(estado.error != null) TextButton(restaurar) { Text("Reintentar sesión guardada") }
        }
    }
}
