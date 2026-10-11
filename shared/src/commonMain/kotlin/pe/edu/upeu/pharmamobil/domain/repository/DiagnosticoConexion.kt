package pe.edu.upeu.pharmamobil.domain.repository
interface DiagnosticoConexion { val urlBase: String; suspend fun ejecutar(indice: Int): String }
