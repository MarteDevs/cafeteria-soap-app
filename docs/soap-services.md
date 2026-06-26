# 📡 Servicios SOAP (JAX-WS)

Esta sección describe a nivel de código y de interfaz cómo se definen y exponen los servicios web SOAP en el backend de **Cafetería Ambrosia**.

---

## 1. Definición del Contrato SOAP en Java

Los servicios se estructuran bajo el patrón **Interface + Implementation** utilizando las anotaciones estándar de **JAX-WS** (`jakarta.jws.*`).

### Ejemplo: Interfaz `IUsuarioService`
La interfaz define la firma de los métodos SOAP accesibles. La anotación `@WebService` le indica a JAX-WS que debe publicar esta clase como un servicio web.

```java
package mars.dev.service;

import jakarta.jws.WebService;
import mars.dev.entity.Usuario;
import java.util.List;

@WebService
public interface IUsuarioService {
    Usuario login(String username, String password);
    Usuario crearUsuario(String username, String password, String nombre, String rol);
    List<Usuario> listarUsuarios();
    String eliminarUsuario(int id);
}
```

### Ejemplo: Implementación `UsuarioImpl`
La clase implementadora lleva la anotación `@WebService(endpointInterface = "...")` para enlazarla con la interfaz y `@Component` para que Spring la gestione en su contenedor.

```java
package mars.dev.impl;

import jakarta.jws.WebService;
import mars.dev.entity.Usuario;
import mars.dev.repository.IUsuarioRepository;
import mars.dev.service.IUsuarioService;
import org.springframework.stereotype.Component;
import java.util.List;

@WebService(endpointInterface = "mars.dev.service.IUsuarioService")
@Component
public class UsuarioImpl implements IUsuarioService {

    private final IUsuarioRepository usuarioRepo;

    public UsuarioImpl(IUsuarioRepository usuarioRepo) {
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    public Usuario login(String username, String password) {
        return usuarioRepo.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElse(null);
    }
    // Otros métodos...
}
```

---

## 2. Publicación de los Endpoints

En Spring Boot, los endpoints SOAP se publican utilizando `jakarta.xml.ws.Endpoint`. Esto ocurre en la clase principal [CafeteriaServerApplication.java](file:///d:/Data-Analytic-Proyect/cafeteria-soap/cafeteria-server/src/main/java/mars/dev/CafeteriaServerApplication.java) tras levantar el contexto de Spring:

```java
// Recupera los beans administrados por Spring e inicializa los servidores web ligeros HTTP de JAX-WS
Endpoint.publish("http://0.0.0.0:8085/ws/categoria", ctx.getBean(CategoriaImpl.class));
Endpoint.publish("http://0.0.0.0:8085/ws/producto", ctx.getBean(ProductoImpl.class));
Endpoint.publish("http://0.0.0.0:8085/ws/pedido", ctx.getBean(PedidoImpl.class));
Endpoint.publish("http://0.0.0.0:8085/ws/detalle", ctx.getBean(DetalleImpl.class));
Endpoint.publish("http://0.0.0.0:8085/ws/usuario", ctx.getBean(UsuarioImpl.class));
```

> [!NOTE]
> La dirección `0.0.0.0` es mandatoria en entornos Docker, ya que permite al servidor web interno escuchar en cualquier interfaz de red (p. ej. peticiones procedentes del contenedor Nginx), y no solo en `localhost` (127.0.0.1) interna del contenedor.

---

## 3. Detalle de los Métodos Disponibles por Servicio

A continuación se listan las firmas de los métodos accesibles y su descripción:

### 👤 A. Servicio de Usuarios (`IUsuarioService`)
*   `Usuario login(String username, String password)`: Autentica un usuario. Devuelve el objeto `Usuario` con su rol si es exitoso; de lo contrario, devuelve `null`.
*   `Usuario crearUsuario(String username, String password, String nombre, String rol)`: Registra un nuevo usuario en la base de datos (con rol `MESERO`, `COCINERO` o `ADMINISTRADOR`).
*   `List<Usuario> listarUsuarios()`: Devuelve un listado completo de todos los usuarios registrados.
*   `String eliminarUsuario(int id)`: Borra el registro de un usuario en base a su ID.

### 📝 B. Servicio de Pedidos (`IPedidoService`)
*   `Pedido crearPedido(String mesa, String mesero)`: Inicializa una orden para una mesa asignándole el nombre del mesero logueado en sesión. Su estado inicial es `PENDIENTE`.
*   `List<Pedido> listarPedidos()`: Retorna todas las órdenes generadas.
*   `Pedido buscarPedido(int id)`: Devuelve la información detallada de una orden en base a su ID.
*   `String cambiarEstado(int id, String nuevoEstado)`: Modifica el estado del pedido (`PENDIENTE`, `EN_PREPARACION`, `ENTREGADO`, `CANCELADO`).
*   `String cancelarPedido(int id)`: Cambia el estado del pedido a `CANCELADO`.
*   `List<Pedido> listarPorEstado(String estado)`: Filtra pedidos por su estado actual (muy útil para la vista de cocina).

### 🛍️ C. Servicio de Detalles de Pedido (`IDetalleService`)
*   `DetallePedido agregarDetalle(int pedidoId, int productoId, int cantidad)`: Añade un producto a la orden. Si el producto ya está en el carrito, incrementa la cantidad y recalcula los subtotales e importes.
*   `String eliminarDetalle(int detalleId)`: Elimina un producto específico del carrito de la orden.
*   `String actualizarCantidad(int detalleId, int nuevaCantidad)`: Modifica la cantidad de ítems pedidos y recalcula los subtotales de la orden.
*   `List<DetallePedido> listarDetallesPorPedido(int pedidoId)`: Retorna todos los productos agregados a una orden particular.

### ☕ D. Servicio de Productos (`IProductoService`)
*   `Producto crearProducto(Producto p)`: Registra una nueva bebida en el catálogo vinculándola a una categoría.
*   `List<Producto> listarProductos()`: Devuelve la lista de productos disponibles.
*   `String actualizarProducto(int id, Producto p)`: Modifica los datos (nombre, precio, stock, categoría) de un producto.
*   `String eliminarProducto(int id)`: Elimina el producto del catálogo.
*   `List<Producto> listarPorCategoria(int categoriaId)`: Filtra los productos de acuerdo a su identificador de categoría.

### 🏷️ E. Servicio de Categorías (`ICategoriaService`)
*   `Categoria crearCategoria(Categoria c)`: Registra una nueva agrupación de productos (Bebidas Calientes, Frías, etc.).
*   `List<Categoria> listarCategorias()`: Devuelve todas las categorías de la cafetería.
*   `String actualizarCategoria(int id, Categoria c)`: Modifica el nombre o descripción de una categoría.
*   `String eliminarCategoria(int id)`: Elimina una categoría.
