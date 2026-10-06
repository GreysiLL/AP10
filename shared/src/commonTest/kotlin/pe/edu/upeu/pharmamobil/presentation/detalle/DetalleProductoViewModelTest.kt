package pe.edu.upeu.pharmamobil.presentation.detalle

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.platform.Compartidor
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository
import pe.edu.upeu.pharmamobil.domain.usecase.comoTextoParaCompartir
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class DetalleProductoViewModelTest {
    private val producto = Producto(1, "Paracetamol", 24.50, 10)
    private val repository = object : ProductoRepository {
        override suspend fun registrar(producto: Producto) = producto
        override suspend fun listar() = listOf(producto)
    }
    @AfterTest fun limpiar() { Dispatchers.resetMain() }
    @Test fun comparteElProductoSeleccionadoConSuPrecioYStock() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        var texto = ""
        val vm = DetalleProductoViewModel(repository, object : Compartidor {
            override fun compartir(valor: String) { texto = valor }
        })
        vm.cargar(1)
        assertTrue(vm.uiState.value.cargando)
        advanceUntilIdle()
        vm.compartir()
        assertEquals(producto.comoTextoParaCompartir(), texto)
        assertEquals("Paracetamol", vm.uiState.value.producto?.nombre)
        assertFalse(vm.uiState.value.cargando)
    }
    @Test fun unProductoInexistenteNoAbreElSelector() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        var llamadas = 0
        val vm = DetalleProductoViewModel(repository, object : Compartidor {
            override fun compartir(texto: String) { llamadas++ }
        })
        vm.cargar(99)
        advanceUntilIdle()
        vm.compartir()
        assertNull(vm.uiState.value.producto)
        assertEquals(0, llamadas)
    }
    @Test fun unErrorNativoSeMuestraSinPerderElDetalle() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val vm = DetalleProductoViewModel(repository, object : Compartidor {
            override fun compartir(texto: String) { error("Selector no disponible") }
        })
        vm.cargar(1)
        advanceUntilIdle()
        vm.compartir()
        assertEquals("Selector no disponible", vm.uiState.value.errorCompartir)
        assertNotNull(vm.uiState.value.producto)
    }
}
