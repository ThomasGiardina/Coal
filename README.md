# 🎮 Coal Backend - API de E-commerce de Videojuegos

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Java](https://img.shields.io/badge/Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=json-web-tokens&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

*API RESTful robusta para una plataforma moderna de compra y venta de videojuegos*

</div>

---

## 📖 Descripción del Proyecto

**Coal Backend** es la API RESTful que potencia la plataforma de e-commerce de videojuegos Coal. Este proyecto nació como un trabajo universitario y fue completamente mejorado y rediseñado, implementando arquitectura en capas, patrones de diseño modernos, seguridad robusta con JWT y buenas prácticas de desarrollo.

La API gestiona todas las operaciones del negocio: autenticación de usuarios, catálogo de videojuegos, carrito de compras, procesamiento de pedidos, sistema de favoritos, métodos de pago, gift cards, y estadísticas para administradores.

---

## 👨‍💻 Desarrolladores

| Desarrollador | Rol |
|---------------|-----|
| **Thomas Agustín Giardina** | Fullstack Developer |
| **Juan Ignacio Domínguez** | Fullstack Developer |

> 🎓 Este proyecto comenzó como un trabajo práctico en la facultad y fue posteriormente mejorado por completo, implementando buenas prácticas, patrones de diseño modernos y una arquitectura escalable.

---

## 🏗️ Arquitectura del Proyecto

El proyecto está dividido en dos repositorios:

| Repositorio | Descripción | Puerto |
|-------------|-------------|--------|
| **Coal** (este repo) | Backend - Spring Boot | `localhost:4002` |
| **[CoalFrontt](https://github.com/ThomasGiardina/CoalFrontt)** | Frontend - React + Vite | `localhost:5173` |

### Conexión con el Frontend

El backend expone una API RESTful en el puerto `4002` que es consumida por el frontend mediante peticiones HTTP. La autenticación se maneja mediante **tokens JWT** y la configuración de CORS permite las conexiones desde el frontend.

---

## 🚀 Tecnologías Utilizadas

### Backend (Este Repositorio)

| Tecnología | Versión | Uso |
|------------|---------|-----|
| **Java** | 17 | Lenguaje de programación |
| **Spring Boot** | 3.1.11 | Framework principal |
| **Spring Security** | 6.3.0 | Autenticación y autorización |
| **Spring Data JPA** | - | Persistencia de datos |
| **Spring Mail** | - | Envío de emails |
| **MySQL Connector** | 8.0.33 | Driver de base de datos |
| **JWT (jjwt)** | 0.12.5 | Tokens de autenticación |
| **Lombok** | 1.18.28 | Reducción de boilerplate |
| **Maven** | - | Gestión de dependencias |
| **Spring Actuator** | - | Monitoreo de la aplicación |
| **Spring DevTools** | - | Desarrollo y hot reload |

---

## 📁 Estructura del Proyecto

```
src/main/java/com/uade/tpo/demo/
├── DemoApplication.java      # Clase principal de la aplicación
├── config/                   # Configuraciones generales
├── controllers/              # Controladores REST
│   ├── auth/                 # Autenticación (login, registro)
│   │   ├── AuthenticationController.java
│   │   ├── AuthenticationRequest.java
│   │   ├── AuthenticationResponse.java
│   │   └── RegisterRequest.java
│   ├── config/               # Configuración de seguridad
│   │   ├── SecurityConfig.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── JwtService.java
│   ├── CarritoController.java
│   ├── EstadisticasController.java
│   ├── FavoritosController.java
│   ├── MetodoPagoController.java
│   ├── PedidoController.java
│   ├── UsuarioController.java
│   └── VideojuegoController.java
├── dto/                      # Data Transfer Objects
│   ├── CarritoDTO.java
│   ├── ItemCarritoDTO.java
│   ├── ItemPedidoDTO.java
│   ├── MetodoPagoDTO.java
│   ├── PedidoDTO.java
│   ├── ProductoMasVendidoDTO.java
│   ├── UltimasVentasDTO.java
│   ├── UsuarioDTO.java
│   ├── VentasPorCategoriaDTO.java
│   └── VideojuegoDTO.java
├── entity/                   # Entidades JPA
│   ├── Carrito.java
│   ├── Favoritos.java
│   ├── GiftCardCode.java
│   ├── ItemCarrito.java
│   ├── ItemPedido.java
│   ├── MetodoPago.java
│   ├── Pedido.java
│   ├── Rol.java
│   ├── Usuario.java
│   └── Videojuego.java
├── exception/                # Excepciones personalizadas
├── repository/               # Repositorios JPA
│   ├── CarritoRepository.java
│   ├── FavoritosRepository.java
│   ├── GiftCardCodeRepository.java
│   ├── ItemCarritoRepository.java
│   ├── ItemPedidoRepository.java
│   ├── MetodoPagoRepository.java
│   ├── PedidoRepository.java
│   ├── UserRepository.java
│   └── VideojuegoRepository.java
├── service/                  # Lógica de negocio
│   ├── AuthenticationService.java
│   ├── CarritoService.java
│   ├── EmailService.java
│   ├── EstadisticasService.java
│   ├── FavoritosService.java
│   ├── FileUploadService.java
│   ├── GiftCardService.java
│   ├── MetodoPagoService.java
│   ├── MetodoPagoServiceImpl.java
│   ├── PedidoService.java
│   ├── UsuarioService.java
│   ├── VideojuegoService.java
│   └── VideojuegoServiceImpl.java
└── dao/                      # Data Access Objects
```

---

## 🔒 Seguridad y Autenticación

### JWT (JSON Web Tokens)

El sistema utiliza **JWT** para manejar la autenticación de manera stateless:

- **Generación de tokens** al iniciar sesión
- **Validación automática** en cada petición protegida
- **Roles de usuario**: `USER` y `ADMIN`
- **Filtro de autenticación** personalizado

### Configuración de Endpoints

```java
// Endpoints públicos
.requestMatchers("/api/v1/auth/**").permitAll()
.requestMatchers("GET", "/videojuegos/**").permitAll()
.requestMatchers("/api/estadisticas/productos-mas-vendidos").permitAll()

// Endpoints protegidos por rol
.requestMatchers("/carritos/**").hasAnyRole("ADMIN", "USER")
.requestMatchers("/api/pedidos/**").hasAnyRole("ADMIN", "USER")
.requestMatchers("/metodosPago/**").hasRole("USER")
.requestMatchers("/api/estadisticas/**").hasRole("ADMIN")
.requestMatchers("POST", "/videojuegos/**").hasRole("ADMIN")
```

---

## 🔌 API Endpoints

### Autenticación (`/api/v1/auth`)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/register` | Registro de nuevo usuario |
| `POST` | `/authenticate` | Inicio de sesión |

### Videojuegos (`/videojuegos`)

| Método | Endpoint | Descripción | Rol |
|--------|----------|-------------|-----|
| `GET` | `/` | Listar todos los videojuegos | Público |
| `GET` | `/{id}` | Obtener videojuego por ID | Público |
| `POST` | `/` | Crear nuevo videojuego | Admin |
| `PUT` | `/{id}` | Actualizar videojuego | Admin |
| `DELETE` | `/{id}` | Eliminar videojuego | Admin |

### Carrito (`/carritos`)

| Método | Endpoint | Descripción | Rol |
|--------|----------|-------------|-----|
| `GET` | `/` | Obtener carrito del usuario | User/Admin |
| `POST` | `/agregar` | Agregar item al carrito | User/Admin |
| `PUT` | `/actualizar` | Actualizar cantidad | User/Admin |
| `DELETE` | `/eliminar/{id}` | Eliminar item | User/Admin |

### Pedidos (`/api/pedidos`)

| Método | Endpoint | Descripción | Rol |
|--------|----------|-------------|-----|
| `GET` | `/` | Obtener pedidos del usuario | User/Admin |
| `POST` | `/crear` | Crear nuevo pedido | User/Admin |
| `GET` | `/{id}` | Obtener detalle de pedido | User/Admin |

### Favoritos (`/favoritos`)

| Método | Endpoint | Descripción | Rol |
|--------|----------|-------------|-----|
| `GET` | `/` | Obtener favoritos | User/Admin |
| `POST` | `/agregar` | Agregar a favoritos | User/Admin |
| `DELETE` | `/eliminar/{id}` | Quitar de favoritos | User/Admin |

### Estadísticas (`/api/estadisticas`)

| Método | Endpoint | Descripción | Rol |
|--------|----------|-------------|-----|
| `GET` | `/productos-mas-vendidos` | Top productos | Público |
| `GET` | `/ventas-por-categoria` | Ventas por categoría | Admin |
| `GET` | `/ultimas-ventas` | Últimas ventas | Admin |

---

## ✨ Funcionalidades Principales

### Para Usuarios

- 🔐 **Registro e Inicio de Sesión**: Autenticación segura con JWT
- 🛒 **Carrito de Compras**: CRUD completo de items
- ❤️ **Lista de Favoritos**: Guardar juegos para después
- 📦 **Gestión de Pedidos**: Crear y consultar órdenes
- 💳 **Métodos de Pago**: Gestión de tarjetas y pagos
- 🎁 **Gift Cards**: Canje de tarjetas de regalo
- ⚙️ **Perfil de Usuario**: Actualización de datos personales
- 📧 **Recuperación de Contraseña**: Por email

### Para Administradores

- 🎮 **CRUD de Videojuegos**: Gestión completa del catálogo
- 📊 **Dashboard de Estadísticas**: Ventas, productos más vendidos
- 📋 **Gestión de Pedidos**: Ver todos los pedidos del sistema
- 🎁 **Gestión de Gift Cards**: Crear y administrar tarjetas

---

## 🛡️ Buenas Prácticas Implementadas

### Arquitectura y Código

- ✅ **Arquitectura en Capas**: Controller → Service → Repository → Entity
- ✅ **Principio de Responsabilidad Única**: Cada clase tiene un propósito específico
- ✅ **DTOs**: Separación entre entidades de negocio y datos expuestos
- ✅ **Inyección de Dependencias**: Uso de `@Autowired` y constructores
- ✅ **Lombok**: Reducción de código boilerplate

### Seguridad

- ✅ **JWT Stateless**: Sin sesiones en servidor
- ✅ **BCrypt**: Encriptación de contraseñas
- ✅ **Roles y Permisos**: Control de acceso granular
- ✅ **CORS Configurado**: Solo orígenes permitidos
- ✅ **Validación de Datos**: En DTOs y entidades

### Base de Datos

- ✅ **JPA/Hibernate**: ORM para persistencia
- ✅ **Relaciones Correctas**: OneToMany, ManyToOne, ManyToMany
- ✅ **Repositorios Spring Data**: Queries derivadas automáticas
- ✅ **Transacciones**: Manejo correcto de operaciones

### API RESTful

- ✅ **Verbos HTTP Correctos**: GET, POST, PUT, DELETE
- ✅ **Códigos de Estado**: 200, 201, 400, 401, 403, 404, 500
- ✅ **Respuestas JSON**: Formato consistente
- ✅ **Manejo de Errores**: Excepciones personalizadas

---

## 📦 Instalación y Ejecución

### Requisitos Previos

- Java 17 o superior
- Maven 3.6+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, VS Code)

### Configuración de Base de Datos

1. Crear una base de datos MySQL:
```sql
CREATE DATABASE coal_db;
```

2. Configurar `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/coal_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
```

### Pasos de Instalación

```bash
# 1. Clonar el repositorio
git clone https://github.com/ThomasGiardina/Coal.git

# 2. Entrar al directorio
cd Coal

# 3. Compilar el proyecto
mvn clean install

# 4. Ejecutar la aplicación
mvn spring-boot:run
```

### Scripts Disponibles

| Comando | Descripción |
|---------|-------------|
| `mvn spring-boot:run` | Inicia el servidor en `localhost:4002` |
| `mvn clean install` | Compila y empaqueta la aplicación |
| `mvn test` | Ejecuta los tests unitarios |
| `mvn package` | Genera el JAR ejecutable |

---

## 🖥️ Frontend - Coal Frontend

El frontend del proyecto se encuentra en un repositorio separado:

📦 **Repositorio:** [Coal Frontend](https://github.com/ThomasGiardina/CoalFrontt)

### Características del Frontend

- **Framework:** React 18 + Vite
- **Puerto:** `localhost:5173`
- **Estado Global:** Redux Toolkit
- **Estilos:** TailwindCSS + DaisyUI
- **Animaciones:** Framer Motion

### Conexión

El frontend se conecta al backend mediante la URL base:
```
http://localhost:4002
```

---

## 📄 Licencia

Este proyecto fue desarrollado con fines educativos como parte de un trabajo universitario.

---

<div align="center">

**Hecho con ❤️ por Thomas Giardina & Juan Ignacio Domínguez**

*Proyecto universitario mejorado y llevado a producción*

</div>
