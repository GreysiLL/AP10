package pe.edu.upeu.pharmamobil.presentation.acerca

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upeu.pharmamobil.platform.InfoDispositivo

@Composable
fun AcercaScreen() {
    val dispositivo = remember { InfoDispositivo() }
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Acerca de PharmaMobil", style = MaterialTheme.typography.headlineSmall)
        Text("Gestión farmacéutica multiplataforma")
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Información del dispositivo", style = MaterialTheme.typography.titleMedium)
                Text("Sistema operativo: ${dispositivo.sistema}")
                Text("Versión: ${dispositivo.version}")
            }
        }
    }
}
