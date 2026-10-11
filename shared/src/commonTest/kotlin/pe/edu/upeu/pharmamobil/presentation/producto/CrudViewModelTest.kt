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
class CrudViewModelTest {
 @AfterTest fun limpiar() { Dispatchers.resetMain() }
 private fun vm(r: ProductoRepository) = ProductoViewModel(RegistrarProductoUseCase(r),ListarProductosUseCase(r),ObtenerProductoUseCase(r),ActualizarProductoUseCase(r),EliminarProductoUseCase(r))
 @Test fun validacion400VaAlCampoYConservaElInventario() = runTest {
  Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
  val repo=FakeProductoRepository(mutableListOf(Producto(1,"Existente",2.0,5))).apply { fallaAlRegistrar=ErrorApiException(ErrorApi.Validacion(mapOf("nombre" to "Entre 3 y 150 caracteres"))) }
  val v=vm(repo);v.onNombreChange("ab");v.onPrecioChange("2");v.onStockChange("3");v.registrar()
  assertEquals("Entre 3 y 150 caracteres",v.uiState.value.formulario.nombreError)
  assertIs<ProductoUiState.Fase.ConProductos>(v.uiState.value.fase)
  assertEquals(ProductoUiState.Operacion.Inactiva,v.uiState.value.operacion)
 }
 @Test fun listadoVisibleYNoDuplicaPostMientrasGuarda() = runTest {
  Dispatchers.setMain(StandardTestDispatcher(testScheduler))
  val fake=FakeProductoRepository(mutableListOf(Producto(1,"Existente",2.0,5)))
  var posts=0
  val repo=object: ProductoRepository by fake {
   override suspend fun registrar(producto: Producto): Producto { posts++;delay(100);return fake.registrar(producto) }
  }
  val v=vm(repo);advanceUntilIdle();v.onNombreChange("Nuevo");v.onPrecioChange("2");v.onStockChange("3");v.registrar();v.registrar();runCurrent()
  assertIs<ProductoUiState.Fase.ConProductos>(v.uiState.value.fase)
  assertIs<ProductoUiState.Operacion.EnCurso>(v.uiState.value.operacion)
  advanceUntilIdle();assertEquals(1,posts)
  assertEquals(2,assertIs<ProductoUiState.Fase.ConProductos>(v.uiState.value.fase).productos.size)
 }
 @Test fun editaCategoriaOriginalYEliminarRecargaActivos() = runTest {
  Dispatchers.setMain(UnconfinedTestDispatcher(testScheduler))
  val repo=FakeProductoRepository(mutableListOf(Producto(3,"Existente",2.0,5,true,7)))
  val v=vm(repo);v.editar(3);v.onPrecioChange("4");v.registrar()
  assertEquals(7L,repo.obtener(3).categoriaId);assertEquals(4.0,repo.obtener(3).precio)
  v.eliminar(3)
  assertEquals(ProductoUiState.Fase.SinProductos,v.uiState.value.fase)
  assertEquals("Producto eliminado",v.uiState.value.mensajeExito)
 }
}
