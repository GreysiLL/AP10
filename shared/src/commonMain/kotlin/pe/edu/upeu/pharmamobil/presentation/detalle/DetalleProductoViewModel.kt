package pe.edu.upeu.pharmamobil.presentation.detalle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository
import pe.edu.upeu.pharmamobil.domain.usecase.comoTextoParaCompartir
import pe.edu.upeu.pharmamobil.presentation.producto.ProductoUi
import pe.edu.upeu.pharmamobil.presentation.producto.aUi

data class DetalleProductoUiState(
    val cargando: Boolean = true, val producto: ProductoUi? = null,
    val error: String? = null, val errorCompartir: String? = null
)
class DetalleProductoViewModel(
    private val repository: ProductoRepository, private val compartidor: Compartidor
) : ViewModel() {
    private val mutable = MutableStateFlow(DetalleProductoUiState())
    val uiState = mutable.asStateFlow()
    private var producto: Producto? = null
    private var carga: kotlinx.coroutines.Job? = null
    fun cargar(id: Long) {
        carga?.cancel()
        mutable.value = DetalleProductoUiState()
        producto = null
        carga = viewModelScope.launch {
            try {
                producto = repository.listar().find { it.id == id }
                mutable.value = DetalleProductoUiState(cargando = false, producto = producto?.aUi())
            } catch (e: CancellationException) { throw e }
            catch (e: Exception) {
                mutable.value = DetalleProductoUiState(cargando = false, error = e.message ?: "No se pudo cargar el producto.")
            }
        }
    }
    fun compartir() {
        val seleccionado = producto ?: return
        try {
            compartidor.compartir(seleccionado.comoTextoParaCompartir())
            mutable.value = mutable.value.copy(errorCompartir = null)
        } catch (e: Exception) {
            mutable.value = mutable.value.copy(errorCompartir = e.message ?: "No se pudo abrir el selector.")
        }
    }
}
