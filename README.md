# Coal — Backend E-commerce de Videojuegos 🎮

API RESTful para un e-commerce de videojuegos. Gestiona usuarios, autenticación JWT, catálogo de videojuegos, carritos, pedidos, métodos de pago, favoritos, estadísticas, subida de archivos e integración con MySQL.

## Índice
- Introducción
- Quick Start
- Arquitectura y tecnologías
- Alcance y limitaciones
- Estructura del proyecto
- Configuración (MySQL, JWT, uploads)
- Ejecución (dev y producción)
- Endpoints principales
- Seguridad (JWT)
- Subida de archivos
- Tests
- Despliegue
- Contribuir
- Autores

## Introducción
Este backend expone endpoints para operar un e-commerce: autenticación y gestión de usuarios, administración de videojuegos (incluyendo imágenes), carritos y pedidos, estadísticas de ventas, favoritos y métodos de pago. Está desarrollado con Spring Boot y persiste datos en MySQL.

## 🚀 Quick Start

1. Crear la base de datos MySQL `coal`
2. Configurar credenciales en `application.properties`
3. Ejecutar:
   ./mvnw spring-boot:run
4. API disponible en http://localhost:4002


## Arquitectura y tecnologías
- Lenguaje: Java 17
- Framework: Spring Boot
- Seguridad: Spring Security + JWT
- Persistencia: Spring Data JPA (MySQL)
- Build: Maven (Wrappers `mvnw`/`mvnw.cmd`)
- Carga de archivos: Multipart + almacenamiento local (`uploads/`)
 - Carga de archivos: Multipart + almacenamiento en base de datos (BLOB)
- Logs y configuración: `application.properties`

## Alcance y limitaciones
- No incluye pasarela de pago real (simulación).
- Autenticación basada en JWT sin refresh token.
- Uploads almacenados localmente (no S3/Cloud).


## Estructura del proyecto
```
src/
	main/
		java/com/uade/tpo/demo/
			DemoApplication.java
			config/WebConfig.java
			controllers/
				auth/ AuthenticationController.java
				CarritoController.java
				EstadisticasController.java
				FavoritosController.java
				MetodoPagoController.java
				PedidoController.java
				UsuarioController.java
				VideojuegoController.java
			dao/ (DAOs auxiliares)
			dto/ (DTOs de request/response)
			entity/ (Entidades JPA: Usuario, Videojuego, Pedido, etc.)
			exception/ (Excepciones de dominio)
			repository/ (Spring Data Repositories)
			service/ (Servicios de negocio)
		resources/
			application.properties
```

## Configuración
La configuración principal se realiza en `src/main/resources/application.properties`:

```properties
spring.application.name=demo
server.port=4002
spring.datasource.url=jdbc:mysql://localhost:3306/coal
spring.datasource.driverClassName=com.mysql.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.database=mysql
spring.jpa.database-platform= org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
application.security.jwt.secretKey=... (clave hex)
application.security.jwt.expiration=86400000
spring.web.resources.static-locations=file:./uploads/
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB
upload.dir=uploads
```

### Base de datos (MySQL)
1. Crear la base:
	 ```sql
	 CREATE DATABASE coal CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
	 ```
2. Ajustar usuario/clave en `application.properties` si no usas `root/root`.
3. Al iniciar, JPA generará/actualizará el esquema (`ddl-auto=update`).

### JWT
- Clave y expiración configuradas en `application.security.jwt.*`.
- Los endpoints protegidos requieren `Authorization: Bearer <token>`.

### Almacenamiento de archivos
- Las imágenes y archivos se almacenan en la base de datos (p. ej. BLOB).
- Los endpoints devuelven los datos desde la BD; la configuración `spring.web.resources.static-locations` puede existir para otros recursos estáticos, pero no se usa para servir imágenes subidas.
- Límites de subida configurados: 100MB por archivo y por request.

## Ejecución

### Desarrollo (Windows)
```bash
./mvnw.cmd spring-boot:run
```
Servidor en: `http://localhost:4002`

### Construcción y ejecución del JAR
```bash
./mvnw.cmd clean package -DskipTests
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### Pruebas
```bash
./mvnw.cmd test
```

## Endpoints principales
Las rutas a continuación se basan en las anotaciones encontradas en los controladores.

### Autenticación (`/api/v1/auth`)
- POST `/register`: registro de usuario.
- POST `/authenticate`: login, devuelve JWT.

#### Ejemplo: Login

POST `/api/v1/auth/authenticate`

Request:

```json
{
	"email": "user@mail.com",
	"password": "1234"
}
```

Response:

```json
{
	"token": "eyJhbGciOiJIUzI1NiIs..."
}
```

### Usuario (`/api/usuario`)
- GET `/actual`: obtener usuario autenticado.
- PUT `/actualizar`: actualizar datos del usuario.
- POST `/actualizar-imagen`: actualizar imagen (multipart).
- GET `/imagen/{userId}`: obtener imagen por usuario.
- PUT `/cambiar-contrasena`: cambiar contraseña.
- PUT `/olvidar-contrasena`: flujo de recuperación.

### Videojuegos (`/videojuegos`)
- POST `/` (multipart/form-data): crear videojuego.
- POST `/{id}/foto`: subir/actualizar foto.
- GET `/{id}`: detalle.
- GET `/`: listado y filtros (según servicio).
- PUT `/{id}` (multipart/form-data): actualizar.
- DELETE `/{id}`: eliminar.

### Carritos (`/carritos`)
- GET `/{id}`: obtener carrito.
- POST `/{carritoId}/items`: agregar ítem al carrito.
- PUT `/items/{itemId}`: actualizar cantidad/detalle de ítem.
- DELETE `/{carritoId}/items/{itemId}`: eliminar ítem.
- POST `/confirmar/{carritoId}`: confirmar carrito → crea pedido.
- GET `/usuarios/carrito`: carrito del usuario autenticado.

### Pedidos (`/api/pedidos`)
- GET `/`: listado.
- GET `/usuario/{usuarioId}`: pedidos por usuario.
- PUT `/{pedidoId}/confirmar`: marcar confirmado.
- PUT `/{pedidoId}/pendiente`: marcar pendiente.
- PUT `/{pedidoId}/cancelar`: cancelar pedido.
- POST `/{pedidoId}/pagar`: pago (carrito completo).
- POST `/{pedidoId}/pagarUnico`: pago de ítem único.

### Favoritos (`/favoritos`)
- GET `/`: lista de favoritos del usuario.
- POST `/{videojuegoId}`: agregar a favoritos.
- DELETE `/{videojuegoId}`: quitar de favoritos.

### Métodos de Pago (`/metodosPago`)
- POST `/`: crear método de pago.
- GET `/{id}`: detalle.
- GET `/`: listado.
- GET `/usuario`: métodos del usuario.
- PUT `/{id}`: actualizar.
- DELETE `/{id}`: eliminar.

### Estadísticas (`/api/estadisticas`)
- GET `/recaudacion-mensual`
- GET `/recaudacion-diaria`
- GET `/ultimas-ventas`
- GET `/productos-mas-vendidos`
- GET `/ventas-por-categoria`
- GET `/recaudacion-mensual-confirmada`
- GET `/ganancias-diarias-confirmadas`
- GET `/recaudacion-total-confirmada`

> Nota: Los cuerpos de requests/responses utilizan los DTOs en `src/main/java/com/uade/tpo/demo/dto/` (p. ej., `UsuarioDTO`, `VideojuegoDTO`, `PedidoDTO`, etc.).

## Seguridad (JWT)
- Rutas públicas: endpoints bajo `/api/v1/auth` (registro/autenticación).
- Resto de rutas requieren JWT en el header `Authorization`.
- Roles y autorizaciones se gestionan en servicios/repositorios asociados a `Usuario`/`Rol`.

## Subida de archivos
- Endpoints de videojuegos aceptan `multipart/form-data` para imágenes.
- Los archivos se almacenan en la base de datos; los servicios gestionan la persistencia y la entrega (bytes/base64 según implementación).

## Despliegue
- Variables sensibles (credenciales DB, clave JWT) deben inyectarse por entorno o perfiles (`application-prod.properties`).
- Recomendado: actualizar `driverClassName` a `com.mysql.cj.jdbc.Driver` en entornos modernos.
- Configurar `server.port` según necesidad y exponer la carpeta `uploads/`.

## Contribuir
1. Crear rama desde `main`.
2. Asegurar formato y convenciones.
3. Agregar/actualizar pruebas cuando aplique.
4. Abrir PR con descripción clara de cambios.

## Autores
- Juan Ignacio Dominguez
- Thomas Agustin Giardina
- Marco Ambrosini
- Lucas Gibellini

