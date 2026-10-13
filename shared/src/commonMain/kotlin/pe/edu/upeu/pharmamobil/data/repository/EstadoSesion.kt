package pe.edu.upeu.pharmamobil.data.repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pe.edu.upeu.pharmamobil.data.local.AlmacenSeguro
import pe.edu.upeu.pharmamobil.domain.model.Sesion
class EstadoSesion(private val almacen: AlmacenSeguro) : pe.edu.upeu.pharmamobil.domain.repository.ObservadorSesion {
    private val actual = MutableStateFlow<Sesion?>(null)
    override val sesion = actual.asStateFlow()
    private val mensaje = MutableStateFlow<String?>(null)
    override val aviso = mensaje.asStateFlow()
    fun activar(sesion: Sesion) { actual.value = sesion; mensaje.value = null }
    fun limpiar(expirada: Boolean = false) {
        try { almacen.borrarToken() } finally {
            actual.value = null
            mensaje.value = if(expirada) "Tu sesión expiró o ya no es válida. Inicia sesión nuevamente." else null
        }
    }
}
