package pe.edu.upeu.pharmamobil.domain.usecase
import pe.edu.upeu.pharmamobil.domain.repository.AuthRepository
import pe.edu.upeu.pharmamobil.domain.model.Credenciales
class IniciarSesionUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke(usuario: String, clave: String) = repo.iniciarSesion(Credenciales(usuario.trim(), clave))
}
class CerrarSesionUseCase(private val repo: AuthRepository) {
    suspend operator fun invoke() = repo.cerrarSesion()
}
