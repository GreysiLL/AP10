# Verificación de la práctica

- Error inicial sin actual: registrado en 01-error-sin-actual.log.
- Código Android y común: compilado al ejecutar las pruebas.
- Pruebas: 52 ejecutadas, cero fallos (03-pruebas.log).
- Selector nativo: implementado; pendiente prueba manual y captura en emulador.
- iOS: código preparado; pendiente compilación, ejecución y capturas en Mac.
- REST PharmaSoft: pendiente. La base recibida usa ProductoRepositorioEnMemoria.
- Rama y tres commits: scripts preparados. La sesión no pudo escribir en .git por permisos del sistema; no se afirma que ya estén creados.

Prueba manual: registrar un producto, abrir su tarjeta y pulsar Compartir. Verificar nombre, importe y stock en el selector del sistema. Cancelar el selector y comprobar que se conserva el detalle.

## Servidor localizado

Carpeta: C:/Users/HECTOR/StudioProjects/pharmaSoft.
Contrato revisado: ProductoController, ProductoRequestDTO, ProductoResponseDTO y PaginaResponseDTO.
GET /api/v1/productos retorna un objeto paginado con contenido. POST y PUT requieren nombre, precio, stock, estado y categoriaId.
La comprobación HTTP del 6 de octubre de 2026 no obtuvo conexión en localhost:8080; no se verificó HTTP 200. No se cambió el repositorio en memoria por un repositorio REST.

## APK

assembleDebug se detuvo en validateSigningDebug por AccessDeniedException al archivo debug.keystore.lock. Es un bloqueo de acceso de la sesión, posterior a la compilación Kotlin. Ejecutar Run desde la sesión del usuario para verificar instalación y selector de compartir.
