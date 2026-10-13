package pe.edu.upeu.pharmamobil.domain.repository
import kotlinx.coroutines.flow.StateFlow
import pe.edu.upeu.pharmamobil.domain.model.Sesion
interface ObservadorSesion {
    val sesion: StateFlow<Sesion?>
    val aviso: StateFlow<String?>
}
