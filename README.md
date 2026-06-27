# 🌌 Cafetería Ambrosia — POS System (Deep Space Mars Aesthetic)

¡Bienvenido a **Cafetería Ambrosia**! Este es un sistema de punto de venta (POS) y visualización de cocina en tiempo real desarrollado para una cafetería premium con temática espacial ("Deep Space Mars"). El sistema combina un frontend SPA dinámico y moderno con un backend robusto basado en servicios SOAP.

---

## 📚 Documentación del Sistema

Para entender más a fondo la arquitectura, el diseño de la base de datos y cómo interactuar con los servicios SOAP, revisa la documentación detallada en la carpeta `docs/`:

*   [🔌 Guía de Pruebas con SoapUI](file:///d:/Data-Analytic-Proyect/cafeteria-soap/docs/soapui-guide.md): Ejemplos de sobres XML y paso a paso para realizar consultas con SoapUI.
*   [🔀 Flujo Paso a Paso de Pruebas en SoapUI](file:///d:/Data-Analytic-Proyect/cafeteria-soap/docs/flujo-soapui.md): Caso de uso real de la cafetería recreado cronológicamente mediante peticiones SOAP.
*   [🌌 Arquitectura del Sistema](file:///d:/Data-Analytic-Proyect/cafeteria-soap/docs/arquitectura.md): Diagrama de flujo de datos, proxy de Nginx y redes de Docker.
*   [📡 Servicios SOAP (JAX-WS)](file:///d:/Data-Analytic-Proyect/cafeteria-soap/docs/soap-services.md): Interfaces Java, publicación de endpoints y listado de métodos por servicio.
*   [🔐 Roles, Permisos e Inicialización](file:///d:/Data-Analytic-Proyect/cafeteria-soap/docs/usuarios-roles.md): Matriz de permisos del frontend, guardia de rutas en Vue 3 y base de datos semilla.
*   [❓ Preguntas de Defensa del Proyecto](file:///d:/Data-Analytic-Proyect/cafeteria-soap/docs/preguntas-defensa.md): Balotario de 30 preguntas de examen técnico y de negocio con respuestas detalladas.

---

## 🚀 Arquitectura y Tecnologías

El sistema está dividido en dos componentes principales y utiliza contenedores para facilitar su despliegue:

*   **Frontend (Cliente SPA)**:
    *   **Vue 3 (Composition API)**: Framework interactivo y modular.
    *   **Tailwind CSS (v4)**: Estilos premium con estética *Glassmorphism* (efecto de cristal translúcido), micro-animaciones y tema oscuro marciano.
    *   **Vue Router** & **Pinia**: Manejo de rutas y estados reactivos.
    *   **Protocolo SOAP**: Comunicación nativa mediante sobres XML enviados directamente a los endpoints del servidor.
*   **Backend (Servidor SOAP)**:
    *   **Spring Boot (Java 21)**: Base de la aplicación de servidor.
    *   **JAX-WS (Jakarta XML Web Services)**: Publicación de los Web Services SOAP.
    *   **Spring Data JPA** & **Hibernate**: Mapeo relacional y acceso a base de datos.
    *   **MySQL 8.0**: Motor de base de datos relacional para persistir la información.
*   **Despliegue y Orquestación**:
    *   **Docker** & **Docker Compose**: Containerización de la base de datos, el backend y el frontend (servido por Nginx como servidor web y proxy inverso).

---

## 🔑 Roles y Permisos (Seguridad)

El sistema cuenta con un control de accesos basado en roles implementado a lo largo de toda la interfaz:

1.  **Mesero (`MESERO`)**:
    *   *Funciones*: Crear nuevos pedidos asignados a mesas, agregar productos al carrito y ver el historial de pedidos en el sistema.
    *   *Accesos*: Dashboard, Nuevo Pedido e Historial. (Sección de Cocina y Configuración bloqueadas).
2.  **Cocinero (`COCINERO`)**:
    *   *Funciones*: Visualizar la cola de pedidos pendientes en tiempo real, marcar pedidos en preparación y terminarlos para entrega.
    *   *Accesos*: Redirección automática y acceso exclusivo a la vista de **Cocina** (Kanban de preparación).
3.  **Administrador (`ADMINISTRADOR`)**:
    *   *Funciones*: Control total del sistema. Registro y eliminación de usuarios (meseros/cocineros), mantenimiento de categorías y catálogo de productos.
    *   *Accesos*: Todo el sistema sin restricciones de navegación.

---

## 🎮 Credenciales de Prueba (Semilla)

Al iniciar el sistema por primera vez, la base de datos se poblará automáticamente mediante un script SQL con categorías de bebidas, catálogo inicial y los siguientes usuarios semilla:

| Rol | Usuario | Contraseña | Nombre Completo |
| :--- | :--- | :--- | :--- |
| **Mesero** | `mesero1` | `123` | Juan Mesero |
| **Cocinero** | `cocinero1` | `123` | Chef Carlos |
| **Administrador** | `admin` | `123` | Admin Ambrosia |

---

## 🛠️ Instrucciones de Despliegue

### Opción A: Despliegue con Docker Compose (Recomendado)

Esta opción compila los proyectos de forma automática y levanta todos los servicios en contenedores aislados. Solo necesitas tener instalado **Docker** y **Docker Compose**.

1.  Abre una terminal en la raíz del proyecto y ejecuta:
    ```bash
    docker compose up --build
    ```
2.  Una vez que termine el proceso de construcción e inicialización, accede desde tu navegador a:
    *   **Frontend**: `http://localhost` (Puerto HTTP estándar 80).
    *   **Servicios SOAP (Backend)**:
        *   Categoría: `http://localhost:8085/ws/categoria?wsdl`
        *   Producto: `http://localhost:8085/ws/producto?wsdl`
        *   Pedido: `http://localhost:8085/ws/pedido?wsdl`
        *   Detalle Pedido: `http://localhost:8085/ws/detalle?wsdl`
        *   Usuario/Login: `http://localhost:8085/ws/usuario?wsdl`

---

### Opción B: Ejecución en Desarrollo (Manual)

Si deseas realizar modificaciones o depurar en caliente, puedes levantar los componentes de forma manual:

#### 1. Base de Datos (MySQL)
Crea una base de datos local llamada `cafeteria_db` e inicialízala ejecutando el script SQL ubicado en [db-init/init.sql](file:///d:/Data-Analytic-Proyect/cafeteria-soap/db-init/init.sql).

#### 2. Levantar el Backend (Spring Boot)
1.  Navega a la carpeta del servidor:
    ```bash
    cd cafeteria-server
    ```
2.  Configura tus credenciales de MySQL en `src/main/resources/application.properties` (opcional si usas los valores por defecto: usuario `root` y contraseña `marte`).
3.  Compila y ejecuta la aplicación con el Maven Wrapper:
    ```bash
    .\mvnw.cmd spring-boot:run
    ```
    *(El servidor SOAP estará activo en los puertos `8080` y `8085`)*.

#### 3. Levantar el Frontend (Vue 3 / Vite)
1.  Navega a la carpeta del cliente en otra terminal:
    ```bash
    cd cafeteria-frontend
    ```
2.  Instala las dependencias de Node.js:
    ```bash
    npm install
    ```
3.  Inicia el servidor de desarrollo local de Vite:
    ```bash
    npm run dev
    ```
4.  Abre tu navegador en `http://localhost:5173`.

---

## 📂 Estructura del Proyecto

```text
cafeteria-soap/
├── cafeteria-frontend/       # Cliente web en Vue 3
│   ├── src/
│   │   ├── assets/           # Estilos globales y variables de Tailwind
│   │   ├── services/         # Clientes SOAP en JS (soapClient, pedidoService, etc.)
│   │   ├── views/            # Vistas del Dashboard, Cocina, POS, Login y CRUDs
│   │   └── App.vue           # Layout principal y control de sesión/permisos
│   ├── nginx.conf            # Configuración de Nginx para Docker (Proxy Inverso /ws)
│   └── Dockerfile            # Construcción e imagen de producción para el Frontend
│
├── cafeteria-server/         # Servidor Backend en Spring Boot
│   ├── src/main/java/mars/dev/
│   │   ├── entity/           # Clases de entidad JPA (Pedido, Usuario, etc.)
│   │   ├── repository/       # Repositorios JPA de Spring Data
│   │   ├── service/          # Interfaces JAX-WS anotadas con @WebService
│   │   └── impl/             # Implementaciones de servicios SOAP
│   └── Dockerfile            # Imagen multi-stage para compilar y correr el jar
│
├── db-init/
│   └── init.sql              # Script SQL de creación de base de datos e inserción de datos semilla
└── docker-compose.yml        # Orquestación de MySQL, Backend y Frontend
```
