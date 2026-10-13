package pe.edu.upeu.pharmamobil.domain.model
sealed interface ErrorSesion {
    data object CredencialesInvalidas : ErrorSesion
    data object SesionExpirada : ErrorSesion
    data object SinPermiso : ErrorSesion
    data object SinConexion : ErrorSesion
    data object Servidor : ErrorSesion
    data object Almacenamiento : ErrorSesion
}
class ErrorSesionException(val error: ErrorSesion) : Exception(when(error) {
    ErrorSesion.CredencialesInvalidas -> "Usuario o contraseña incorrectos."
    ErrorSesion.SesionExpirada -> "Tu sesión expiró o ya no es válida. Inicia sesión nuevamente."
    ErrorSesion.SinPermiso -> "No tienes permiso para realizar esta operación."
    ErrorSesion.SinConexion -> "No pudimos conectar con PharmaSoft. Intenta nuevamente."
    ErrorSesion.Servidor -> "PharmaSoft no pudo completar el inicio de sesión."
    ErrorSesion.Almacenamiento -> "No pudimos acceder al almacén seguro."
})
