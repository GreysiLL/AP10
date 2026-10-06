package pe.edu.upeu.pharmamobil.platform
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
@Composable actual fun AtrasDetalle(habilitado: Boolean, volver: () -> Unit) {
    BackHandler(enabled = habilitado, onBack = volver)
}
