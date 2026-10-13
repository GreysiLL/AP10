# Práctica 10: comprobaciones en Postman

Esta colección corresponde al backend PharmaSoft con seguridad JWT. Incluye las **28 operaciones de la API**, organizadas en **53 solicitudes**, con login automático, pruebas de JWT y permisos y un recorrido CRUD autenticado.

## Requisitos

1. Oracle accesible para PharmaSoft. En el equipo del estudiante se usa Docker, puerto local 1522 y servicio FREEPDB1.
2. Backend actualizado del profesor: https://github.com/dreyna/pharmaSoft, commit de seguridad `fff44f99c06d4ca0e71809b591c64435f25978d2` o versión compatible. Deben haberse aplicado las migraciones Flyway V1, V2 y V3.
3. PharmaSoft funcionando en `http://localhost:8080`. Si está en otro equipo o puerto, cambiar `baseUrl` en el entorno.
4. Postman Desktop actualizado. Se usa `pm.execution.skipRequest()` para omitir las operaciones opcionales o aquellas sin un ID de prueba creado.

En el proyecto local preparado por el estudiante, el backend se inicia con `iniciar-pharmasoft.ps1`. El profesor puede utilizar su propia versión del backend; estos archivos no incluyen Oracle ni el servidor Java.

## Importación

En Postman, seleccionar **Import** e importar ambos archivos:

- `PharmaSoft_S10_Completa.postman_collection.json`: colección de solicitudes.
- `PharmaSoft_S10_Local.postman_environment.json`: configuración local.

Seleccionar el entorno **PharmaSoft S10 - Local**. Mantener el backend encendido. No es necesario ejecutar Android Studio para comprobar la API.

## Ejecución completa

Usar **Run collection / Collection Runner**, seleccionar todas las carpetas y ejecutar **una iteración en el orden del archivo**, desde 00 hasta 10. No ejecutar las carpetas en paralelo.

La carpeta 01 inicia sesión con los tres usuarios y guarda sus tokens en variables de colección. Las demás solicitudes usan esos tokens automáticamente. No se debe pegar un token ni escribir la cabecera Authorization manualmente.

| Usuario de prueba | Contraseña | Acceso |
|---|---|---|
| admin | Admin123 | Todos los permisos |
| vendedor | Vendedor123 | Catálogo de lectura; clientes y ventas |
| almacen | Almacen123 | Lectura y escritura de catálogo |

Un **401 o 403 esperado es un test aprobado**, no un fallo de la colección. Las comprobaciones de permisos con `POST {}` esperan 400 cuando la autorización está permitida: la validación del cuerpo evita crear registros en esa carpeta.

## Datos creados y limpieza

Por defecto, `ejecutarCRUD=true`. La colección crea una categoría, un producto y un cliente con nombres/DNI generados para la ejecución. Sus IDs se capturan de las respuestas del servidor; los PUT y DELETE solo se envían si existe el ID correspondiente. La carpeta 10 da de baja lógicamente el producto y el cliente y comprueba `estado=false`. La categoría temporal se elimina físicamente y su consulta posterior debe devolver 404.

No se editan ni eliminan registros preexistentes. El producto temporal utiliza una categoría existente sin modificarla, y la categoría temporal no se asocia a productos, para permitir su eliminación. El producto y cliente de prueba quedan inactivos en el historial. Si el Runner se interrumpe antes de terminar, ejecutar la carpeta 10 manteniendo las variables de esa ejecución. No repetir el login de admin antes de esa limpieza: ese login inicia una ejecución nueva y limpia los IDs anteriores.

Para una comprobación de lectura y seguridad sin crear registros, establecer `ejecutarCRUD=false`.

## Operaciones opcionales

- `ejecutarVentas=false`: no registra ventas por defecto. Si se cambia a true, el Runner debe crear primero su cliente y producto. La venta usa esos registros, consume una unidad de stock y permanece en el historial; la API no ofrece DELETE ventas.
- `crearUsuario=false`: no crea cuentas por defecto. Para activar la solicitud se debe configurar explícitamente `empleadoSinUsuarioId` y `rolIdUsuarioNuevo`, después de revisar los listados de empleados y roles. No reutilizar un empleado que ya tenga usuario. La cuenta creada permanece porque esta API no ofrece DELETE usuarios.

Las solicitudes omitidas aparecen como **skipped / sin pruebas ejecutadas**. Esto es intencional cuando una opción está desactivada.

## Alcance

Postman comprueba el **backend**: login, token, roles, errores y endpoints REST. No comprueba la interfaz de PharmaMobil, el cifrado en Android/Keychain, la recuperación de sesión al reabrir la app ni el comportamiento de iOS. Esas evidencias están en `docs/SESION10` del proyecto.

Los archivos exportados no contienen tokens reales ni una clave de firma JWT. Los tokens se obtienen al ejecutar los logins. No compartir una exportación posterior que incluya tokens de una sesión real.

## Verificación de la colección

Ejecutada el 13/10/2026 con Newman 6.2.3: 50 solicitudes enviadas, 68 comprobaciones aprobadas y cero fallos. Tres solicitudes opcionales omitidas. verificacion-postman.json contiene el resumen sin credenciales y ejecucion-newman.log conserva el resultado. PharmaSoft_API_openapi.json contiene la estructura OpenAPI real del servidor y los DTO.
