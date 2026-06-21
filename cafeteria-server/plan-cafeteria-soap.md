# Plan de Implementación — Sistema de Gestión de Pedidos para Cafetería
**Stack:** Spring Boot 3.5.15 + JAX-WS (SOAP) · MySQL · Vue 3 + Vite  
**Arquitectura:** Server-side SOAP · Frontend Vue consume XML vía fetch

---

## Configuración del proyecto (ya hecha)

| Campo | Valor |
|-------|-------|
| Group | `mars.dev` |
| Artifact | `cafeteria-server` |
| Spring Boot | `3.5.15` |
| Java | 21 |
| Dependencias base | Spring Web, Spring Data JPA, MySQL Driver |

---

## Paso 1 — Agregar dependencias JAX-WS al `pom.xml`

Abrir `pom.xml` y agregar dentro de `<dependencies>`:

```xml
<!-- JAX-WS API -->
<dependency>
    <groupId>jakarta.jws</groupId>
    <artifactId>jakarta.jws-api</artifactId>
    <version>3.0.0</version>
</dependency>

<!-- JAX-WS RI (runtime) -->
<dependency>
    <groupId>com.sun.xml.ws</groupId>
    <artifactId>jaxws-ri</artifactId>
    <version>4.0.4</version>
    <type>pom</type>
</dependency>

<dependency>
    <groupId>com.sun.xml.ws</groupId>
    <artifactId>jaxws-rt</artifactId>
    <version>4.0.4</version>
</dependency>
```

---

## Paso 2 — Base de datos MySQL

```sql
CREATE DATABASE IF NOT EXISTS cafeteria_db;
USE cafeteria_db;
```

Hibernate crea las tablas automáticamente. Las 4 entidades producirán:

| Tabla | Descripción |
|-------|-------------|
| `tbl_categoria` | Categorías del menú (Bebidas, Comidas, Postres...) |
| `tbl_producto` | Productos del menú con precio y stock |
| `tbl_pedido` | Cabecera del pedido (mesa, estado, total, fecha) |
| `tbl_detalle_pedido` | Líneas del pedido (producto, cantidad, subtotal) |

### `application.properties`

```properties
spring.application.name=cafeteria-server
server.port=8085

spring.datasource.url=jdbc:mysql://localhost:3306/cafeteria_db
spring.datasource.username=root
spring.datasource.password=mysql

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## Paso 3 — Estructura de paquetes

```
src/main/java/mars/dev/
├── entity/
│   ├── Categoria.java
│   ├── Producto.java
│   ├── Pedido.java
│   └── DetallePedido.java
├── repository/
│   ├── ICategoriaRepository.java
│   ├── IProductoRepository.java
│   ├── IPedidoRepository.java
│   └── IDetalleRepository.java
├── service/
│   ├── ICategoriaService.java
│   ├── IProductoService.java
│   ├── IPedidoService.java
│   └── IDetalleService.java
├── impl/
│   ├── CategoriaImpl.java
│   ├── ProductoImpl.java
│   ├── PedidoImpl.java
│   └── DetalleImpl.java
└── CafeteriaServerApplication.java
```

---

## Paso 4 — Entidades JPA

### `Categoria.java`

```java
package mars.dev.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private String descripcion;

    // getters, setters, constructores
}
```

### `Producto.java`

```java
package mars.dev.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_producto")
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private double precio;
    private int stock;

    @ManyToOne
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // getters, setters, constructores
}
```

### `Pedido.java`

```java
package mars.dev.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tbl_pedido")
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String mesa;
    private String estado;           // PENDIENTE | EN_PREPARACION | ENTREGADO | CANCELADO
    private double total;
    private LocalDateTime fecha;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL)
    private List<DetallePedido> detalles;

    // getters, setters, constructores
}
```

### `DetallePedido.java`

```java
package mars.dev.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_detalle_pedido")
public class DetallePedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int cantidad;
    private double precioUnitario;
    private double subtotal;

    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    // getters, setters, constructores
}
```

---

## Paso 5 — Repositories

```java
// ICategoriaRepository.java
public interface ICategoriaRepository extends JpaRepository<Categoria, Integer> {}

// IProductoRepository.java
public interface IProductoRepository extends JpaRepository<Producto, Integer> {}

// IPedidoRepository.java
public interface IPedidoRepository extends JpaRepository<Pedido, Integer> {
    List<Pedido> findByEstado(String estado);
}

// IDetalleRepository.java
public interface IDetalleRepository extends JpaRepository<DetallePedido, Integer> {
    List<DetallePedido> findByPedidoId(int pedidoId);
}
```

---

## Paso 6 — Interfaces de Servicio `@WebService`

### `ICategoriaService.java`

```java
@WebService
public interface ICategoriaService {
    List<Categoria> listarCategorias();
    Categoria crearCategoria(Categoria categoria);
    Categoria buscarCategoria(int id);
    String actualizarCategoria(int id, Categoria categoria);
    String eliminarCategoria(int id);
}
```

### `IProductoService.java`

```java
@WebService
public interface IProductoService {
    List<Producto> listarProductos();
    Producto crearProducto(Producto producto);
    Producto buscarProducto(int id);
    String actualizarProducto(int id, Producto producto);
    String eliminarProducto(int id);
    List<Producto> listarPorCategoria(int categoriaId);
}
```

### `IPedidoService.java`

```java
@WebService
public interface IPedidoService {
    List<Pedido> listarPedidos();
    Pedido crearPedido(String mesa);
    Pedido buscarPedido(int id);
    String cambiarEstado(int id, String nuevoEstado);
    String cancelarPedido(int id);
    List<Pedido> listarPorEstado(String estado);
}
```

### `IDetalleService.java`

```java
@WebService
public interface IDetalleService {
    List<DetallePedido> listarDetallesPorPedido(int pedidoId);
    DetallePedido agregarDetalle(int pedidoId, int productoId, int cantidad);
    String eliminarDetalle(int detalleId);
    String actualizarCantidad(int detalleId, int nuevaCantidad);
}
```

---

## Paso 7 — Implementaciones `@WebService @Component`

### `PedidoImpl.java` (ejemplo completo)

```java
package mars.dev.impl;

import jakarta.jws.WebService;
import mars.dev.entity.Pedido;
import mars.dev.repository.IPedidoRepository;
import mars.dev.service.IPedidoService;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;

@WebService
@Component
public class PedidoImpl implements IPedidoService {

    private final IPedidoRepository pedidoRepo;

    public PedidoImpl(IPedidoRepository pedidoRepo) {
        this.pedidoRepo = pedidoRepo;
    }

    @Override
    public List<Pedido> listarPedidos() {
        return pedidoRepo.findAll();
    }

    @Override
    public Pedido crearPedido(String mesa) {
        Pedido p = new Pedido();
        p.setMesa(mesa);
        p.setEstado("PENDIENTE");
        p.setTotal(0.0);
        p.setFecha(LocalDateTime.now());
        return pedidoRepo.save(p);
    }

    @Override
    public Pedido buscarPedido(int id) {
        return pedidoRepo.findById(id).orElse(null);
    }

    @Override
    public String cambiarEstado(int id, String nuevoEstado) {
        return pedidoRepo.findById(id).map(p -> {
            p.setEstado(nuevoEstado);
            pedidoRepo.save(p);
            return "Estado actualizado a: " + nuevoEstado;
        }).orElse("Pedido no encontrado");
    }

    @Override
    public String cancelarPedido(int id) {
        return cambiarEstado(id, "CANCELADO");
    }

    @Override
    public List<Pedido> listarPorEstado(String estado) {
        return pedidoRepo.findByEstado(estado);
    }
}
```

---

## Paso 8 — Publicar los 4 endpoints en `main()`

```java
package mars.dev;

import jakarta.xml.ws.Endpoint;
import mars.dev.impl.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CafeteriaServerApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx =
            SpringApplication.run(CafeteriaServerApplication.class, args);

        Endpoint.publish("http://localhost:8085/ws/categoria",
            ctx.getBean(CategoriaImpl.class));

        Endpoint.publish("http://localhost:8085/ws/producto",
            ctx.getBean(ProductoImpl.class));

        Endpoint.publish("http://localhost:8085/ws/pedido",
            ctx.getBean(PedidoImpl.class));

        Endpoint.publish("http://localhost:8085/ws/detalle",
            ctx.getBean(DetalleImpl.class));

        System.out.println("✅ Servicios SOAP publicados:");
        System.out.println("   http://localhost:8085/ws/categoria?wsdl");
        System.out.println("   http://localhost:8085/ws/producto?wsdl");
        System.out.println("   http://localhost:8085/ws/pedido?wsdl");
        System.out.println("   http://localhost:8085/ws/detalle?wsdl");
    }
}
```

---

## Paso 9 — Verificar WSDLs

Levantar el servidor con `mvn spring-boot:run` y verificar en el navegador:

```
http://localhost:8085/ws/categoria?wsdl  ✅
http://localhost:8085/ws/producto?wsdl   ✅
http://localhost:8085/ws/pedido?wsdl     ✅
http://localhost:8085/ws/detalle?wsdl    ✅
```

---

## Paso 10 — Frontend Vue 3

### Crear proyecto

```bash
npm create vue@latest cafeteria-frontend
cd cafeteria-frontend
npm install
```

### Estructura

```
src/
├── services/
│   ├── soapClient.js        ← helper genérico SOAP
│   ├── categoriaService.js
│   ├── productoService.js
│   ├── pedidoService.js
│   └── detalleService.js
├── views/
│   ├── CategoriasView.vue   ← CRUD categorías
│   ├── ProductosView.vue    ← CRUD productos del menú
│   ├── PedidosView.vue      ← Lista y gestión de pedidos
│   ├── NuevoPedidoView.vue  ← Crear pedido + agregar productos
│   └── CocinaView.vue       ← Vista cocina: pedidos EN_PREPARACION
└── router/index.js
```

### `soapClient.js`

```javascript
const BASE = 'http://localhost:8085/ws';
const NS = 'http://impl.mars.dev/';   // ajustar según WSDL generado

export async function callSoap(endpoint, method, paramsXml = '') {
  const envelope = `
    <soapenv:Envelope
      xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
      xmlns:tns="${NS}">
      <soapenv:Body>
        <tns:${method}>${paramsXml}</tns:${method}>
      </soapenv:Body>
    </soapenv:Envelope>`;

  const res = await fetch(`${BASE}/${endpoint}`, {
    method: 'POST',
    headers: { 'Content-Type': 'text/xml;charset=UTF-8' },
    body: envelope
  });

  const text = await res.text();
  return new DOMParser().parseFromString(text, 'text/xml');
}

export function getNodes(xml, tag) {
  return [...xml.getElementsByTagNameNS('*', tag)];
}

export function getText(xml, tag) {
  const node = xml.getElementsByTagNameNS('*', tag)[0];
  return node ? node.textContent : '';
}
```

### Ejemplo `pedidoService.js`

```javascript
import { callSoap, getNodes, getText } from './soapClient.js';

export async function listarPedidos() {
  const xml = await callSoap('pedido', 'listarPedidos');
  return getNodes(xml, 'return').map(node => ({
    id:     getText(node, 'id'),
    mesa:   getText(node, 'mesa'),
    estado: getText(node, 'estado'),
    total:  getText(node, 'total'),
    fecha:  getText(node, 'fecha'),
  }));
}

export async function crearPedido(mesa) {
  const xml = await callSoap('pedido', 'crearPedido', `<arg0>${mesa}</arg0>`);
  return getText(xml, 'return');
}

export async function cambiarEstado(id, nuevoEstado) {
  const xml = await callSoap('pedido', 'cambiarEstado',
    `<arg0>${id}</arg0><arg1>${nuevoEstado}</arg1>`);
  return getText(xml, 'return');
}
```

---

## Flujo completo del sistema

```
[Cajero — NuevoPedidoView]
  1. Selecciona mesa
  2. Busca productos por categoría
  3. Agrega productos al pedido (DetallePedido)
  4. Confirma → estado = PENDIENTE

[Cocina — CocinaView]
  5. Ve pedidos PENDIENTES
  6. Cambia a EN_PREPARACION
  7. Cambia a ENTREGADO

[Admin — ProductosView / CategoriasView]
  8. CRUD completo de menú y categorías
```

---

## Estados del pedido

```
PENDIENTE → EN_PREPARACION → ENTREGADO
    └──────────────────────→ CANCELADO
```

---

## Orden de implementación recomendado

| # | Tarea |
|---|-------|
| 1 | Agregar deps JAX-WS al `pom.xml` |
| 2 | Crear DB en MySQL |
| 3 | Crear entidades + repositories |
| 4 | Crear interfaces `@WebService` |
| 5 | Implementar `CategoriaImpl` y verificar WSDL |
| 6 | Implementar `ProductoImpl` y verificar WSDL |
| 7 | Implementar `PedidoImpl` y verificar WSDL |
| 8 | Implementar `DetalleImpl` y verificar WSDL |
| 9 | Crear proyecto Vue 3 |
| 10 | Implementar `soapClient.js` |
| 11 | Implementar vistas en orden: Categorias → Productos → NuevoPedido → Pedidos → Cocina |
| 12 | Probar flujo completo |

---

> **Tip CORS:** Si Vue y el servidor corren en puertos distintos, 
> Spring Boot bloqueará las peticiones fetch desde Vue. 
> Agregar un `@CrossOrigin` o un bean `CorsFilter` antes de levantar los endpoints SOAP.
