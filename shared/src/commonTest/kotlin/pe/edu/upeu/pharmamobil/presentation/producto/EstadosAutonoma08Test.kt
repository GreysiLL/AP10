package pe.edu.upeu.pharmamobil.presentation.producto
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import pe.edu.upeu.pharmamobil.data.repository.FakeProductoRepository
import pe.edu.upeu.pharmamobil.domain.repository.ProductoRepository
import pe.edu.upeu.pharmamobil.domain.model.Producto
import pe.edu.upeu.pharmamobil.domain.error.*
import pe.edu.upeu.pharmamobil.domain.usecase.*
import kotlin.test.*
@OptIn(ExperimentalCoroutinesApi::class)
class EstadosAutonoma08Test {
 @AfterTest fun limpiar() { Dispatchers.resetMain() }
 private fun vm(r:ProductoRepository)=ProductoViewModel(RegistrarProductoUseCase(r),ListarProductosUseCase(r),ObtenerProductoUseCase(r),ActualizarProductoUseCase(r),EliminarProductoUseCase(r))
 private val producto=Producto(1,"Prueba",2.5,5)
 @Test fun cargandoPasaAConProductos()=runTest {
  Dispatchers.setMain(StandardTestDispatcher(testScheduler));val v=vm(FakeProductoRepository(mutableListOf(producto)))
  assertEquals(ProductoUiState.Fase.Cargando,v.uiState.value.fase)
  advanceUntilIdle();assertEquals(listOf(1L),assertIs<ProductoUiState.Fase.ConProductos>(v.uiState.value.fase).productos.map { it.id })
 }
 @Test fun cargandoPasaASinProductos()=runTest {
  Dispatchers.setMain(StandardTestDispatcher(testScheduler));val v=vm(FakeProductoRepository())
  assertEquals(ProductoUiState.Fase.Cargando,v.uiState.value.fase)
  advanceUntilIdle();assertEquals(ProductoUiState.Fase.SinProductos,v.uiState.value.fase)
 }
 @Test fun validacionDeServidorActualizaCamposYNoFaseError()=runTest {
  Dispatchers.setMain(StandardTestDispatcher(testScheduler))
  val fake=FakeProductoRepository(mutableListOf(producto)).apply { fallaAlRegistrar=ErrorApiException(ErrorApi.Validacion(mapOf("nombre" to "Nombre invalido","precio" to "Precio invalido","stock" to "Stock invalido")),400) }
  val v=vm(fake);advanceUntilIdle();val fase=v.uiState.value.fase
  v.onNombreChange("ab");v.onPrecioChange("2");v.onStockChange("3");v.registrar()
  assertIs<ProductoUiState.Operacion.EnCurso>(v.uiState.value.operacion)
  advanceUntilIdle();assertEquals(fase,v.uiState.value.fase)
  assertEquals("Nombre invalido",v.uiState.value.formulario.nombreError)
  assertEquals("Precio invalido",v.uiState.value.formulario.precioError)
  assertEquals("Stock invalido",v.uiState.value.formulario.stockError)
  assertEquals(ProductoUiState.Operacion.Inactiva,v.uiState.value.operacion)
 }
 @Test fun eliminarPasaDeEnCursoAInactivaYRecarga()=runTest {
  Dispatchers.setMain(StandardTestDispatcher(testScheduler))
  val fake=FakeProductoRepository(mutableListOf(producto))
  val repo=object:ProductoRepository by fake { override suspend fun eliminar(id:Long) { delay(100);fake.eliminar(id) } }
  val v=vm(repo);advanceUntilIdle();v.eliminar(1);runCurrent()
  assertIs<ProductoUiState.Operacion.EnCurso>(v.uiState.value.operacion)
  assertIs<ProductoUiState.Fase.ConProductos>(v.uiState.value.fase)
  advanceUntilIdle();assertEquals(ProductoUiState.Operacion.Inactiva,v.uiState.value.operacion)
  assertEquals(ProductoUiState.Fase.SinProductos,v.uiState.value.fase)
 }
 @Test fun salirCancelaLaOperacionSinPublicarError()=runTest {
  Dispatchers.setMain(StandardTestDispatcher(testScheduler))
  val fake=FakeProductoRepository(mutableListOf(producto));var cancelada=false
  val repo=object:ProductoRepository by fake {
   override suspend fun eliminar(id:Long) {
    try { delay(5000);fake.eliminar(id) } catch(e:CancellationException) { cancelada=true;throw e }
   }
  }
  val v=vm(repo);advanceUntilIdle();v.eliminar(1);runCurrent();v.cancelarOperaciones();advanceUntilIdle()
  assertTrue(cancelada);assertEquals(ProductoUiState.Operacion.Inactiva,v.uiState.value.operacion)
  assertIs<ProductoUiState.Fase.ConProductos>(v.uiState.value.fase)
  assertNull(v.uiState.value.mensajeExito);assertTrue(fake.obtener(1).estado)
 }
}
