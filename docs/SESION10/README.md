# Práctica 10: autenticación JWT

Backend actualizado desde dreyna/pharmaSoft, commit fff44f99c06d4ca0e71809b591c64435f25978d2 (seguridad). Se conservó Oracle Docker puerto 1522 y la migración V1. Nuevas migraciones V2/V3 de Flyway aplicadas al arrancar.

matriz-seguridad.json registra 23 comprobaciones reales del backend. Las escrituras usan {} para verificar permisos mediante 400 de validación sin crear datos; no sustituyen la evidencia de un CRUD válido. Tokens omitidos de los registros.

Usuarios de prueba: admin/Admin123, vendedor/Vendedor123, almacen/Almacen123.

Estado Android: APK compilado, 77 pruebas aprobadas y ejecución real completada. Login con los tres usuarios, cierre/recuperación de sesión, permisos, 401/403 y CRUD autenticado verificados. iOS implementado, pendiente de compilar y ejecutar en una Mac.

El almacén Android usa EncryptedSharedPreferences y MasterKey según la guía; esa API está deprecada en AndroidX Security 1.1.0, por lo que su uso queda limitado a este ejercicio. iOS usa Keychain con servicio/cuenta de la aplicación y acceso WhenUnlockedThisDeviceOnly. Se deshabilitan las copias de seguridad Android para evitar restaurar preferencias cifradas sin su clave.

La Activity PruebasSeguridadActivity existe solamente en debug. Permite provocar GET clientes con almacén (403) y reemplazar voluntariamente el token por uno inválido para comprobar el 401; esta última acción cierra la sesión. No se incluye en release.

## Capturas Android

- [01-login](capturas/01-login.png): Pantalla de login nueva, sin sesión.
- [02-credenciales-invalidas](capturas/02-credenciales-invalidas.png): Login 401 se informa como credenciales incorrectas; contraseña limpiada.
- [03-admin-autenticado](capturas/03-admin-autenticado.png): Admin puede leer y registrar productos reales.
- [04-sesion-recuperada](capturas/04-sesion-recuperada.png): Se cerró completamente la app; al reabrir consulta auth/me con token persistido y recupera inventario sin pedir clave.
- [05-cerrar-sesion](capturas/05-cerrar-sesion.png): Cerrar sesión vuelve al login.
- [06-vendedor-lectura](capturas/06-vendedor-lectura.png): Vendedor ve productos; formulario, Editar y Eliminar ocultos.
- [07-menu-vendedor](capturas/07-menu-vendedor.png): Vendedor tiene sección Clientes disponible.
- [08-almacen-catalogo](capturas/08-almacen-catalogo.png): Almacén puede leer y escribir catálogo.
- [09-menu-almacen](capturas/09-menu-almacen.png): Clientes oculto para almacén, sesión activa.
- [10-error403](capturas/10-error403.png): GET clientes devuelve 403: mensaje de falta de permiso y sesión conservada.
- [11-error401](capturas/11-error401.png): Token deliberadamente inválido produce 401 y borra sesión/almacén.
- [12-login-tras401](capturas/12-login-tras401.png): 401 protegido lleva al login con aviso distinto al de credenciales incorrectas.
- [13-crud-crear](capturas/13-crud-crear.png): POST autenticado desde Android: PruebaS10-144402, precio 2.50, stock 2.
- [14-crud-actualizar](capturas/14-crud-actualizar.png): GET por ID y PUT autenticados; nuevo precio 3.75.
- [15-crud-eliminar](capturas/15-crud-eliminar.png): DELETE autenticado; producto de prueba eliminado lógicamente, inventario activo restaurado.

POST 201, GET 200, PUT 200 y DELETE 204 están en ktor-android.log. El producto exclusivo de prueba quedó inactivo con precio 3.75 y stock 2 (producto-prueba-final.json); el inventario activo vuelve a 3 productos. El 401 se provocó con un token inválido, no esperando una hora real de expiración.

La matriz de permisos usa POST {}: cuando existe permiso obtiene 400 de validación. Esas comprobaciones prueban autorización, no creación de ventas. No se registraron ventas reales.

Repositorio de entrega: https://github.com/GreysiLL/AP10/tree/feature/auth-jwt-duran. Rama: feature/auth-jwt-duran. La subida se verifica comparando el commit local con la referencia remota.

## Comprobación adicional en Swagger

- [401 sin token](capturas/16-swagger401.png): GET /api/v1/productos, sin autorización, responde 401.
- [200 autorizado](capturas/17-swagger200.png): mismo GET con admin autorizado, responde 200 y contiene los productos reales.
- [403 por permiso](capturas/18-swagger403.png): GET /api/v1/clientes con el token de almacén, responde 403.
- [Logcat saneado](capturas/19-logcat-authorization.png): captura proporcionada por el usuario del registro de esta ejecución, con Authorization: ***.

Las capturas de Swagger enfocan URL y respuesta; no incluyen el token del ejemplo curl. El token se obtuvo del login real del backend y se introdujo en Authorize sin el prefijo Bearer. Los archivos efímeros de credenciales se eliminaron y la pestaña de comprobación se cerró.
