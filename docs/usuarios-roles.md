# 🔐 Roles, Permisos e Inicialización

Esta sección detalla cómo está estructurado el control de accesos de **Cafetería Ambrosia**, cómo funciona la semilla de datos de la base de datos y las políticas de visualización en el frontend.

---

## 1. Diseño de Roles y Permisos

El sistema asigna un **rol** a cada usuario (`Usuario`), el cual define qué rutas y módulos de la interfaz de usuario están habilitados:

### Matriz de Permisos

| Módulo / Vista | Ruta Frontend | Mesero (`MESERO`) | Cocinero (`COCINERO`) | Administrador (`ADMINISTRADOR`) |
| :--- | :--- | :---: | :---: | :---: |
| **Dashboard** | `/` | Sí | No | Sí |
| **Nuevo Pedido (POS)** | `/nuevo-pedido` | Sí | No | Sí |
| **Historial de Ventas** | `/pedidos` | Sí | No | Sí |
| **Cocina (Kanban)** | `/cocina` | No | Sí | Sí |
| **Gestión de Usuarios** | `/usuarios` | No | No | Sí |
| **Categorías** | `/categorias` | No | No | Sí |
| **Catálogo de Productos** | `/productos` | No | No | Sí |

---

## 2. Implementación de Seguridad en el Frontend ([App.vue](file:///d:/Data-Analytic-Proyect/cafeteria-soap/cafeteria-frontend/src/App.vue))

La validación y bloqueo de rutas se realiza a través de un watcher y observador del ciclo de vida del componente principal.

### A. Almacenamiento de Sesión
Cuando el usuario inicia sesión de forma correcta, sus datos (excluyendo la contraseña por seguridad) se persisten en el `localStorage` del navegador bajo la clave `usuario`:
```javascript
{
  "id": 1,
  "username": "mesero1",
  "nombre": "Juan Mesero",
  "rol": "MESERO"
}
```

### B. Guardia de Rutas y Redirecciones
En [App.vue](file:///d:/Data-Analytic-Proyect/cafeteria-soap/cafeteria-frontend/src/App.vue), se observa el cambio de rutas a través del router de Vue 3. Si un usuario intenta acceder por URL directa a una ruta no permitida para su rol, es interceptado y redirigido automáticamente:

```javascript
function verificarPermisosRuta() {
  if (!currentUsuario.value) return;

  const rol = currentUsuario.value.rol;
  const path = route.path;

  if (rol === 'MESERO') {
    // El mesero no puede ingresar a cocina o pantallas de mantenimiento/configuración
    if (path === '/cocina' || path === '/categorias' || path === '/productos' || path === '/usuarios') {
      router.push('/');
    }
  } else if (rol === 'COCINERO') {
    // El cocinero solo tiene permitido estar en la vista de cocina
    if (path !== '/cocina') {
      router.push('/cocina');
    }
  }
}
```

---

## 3. Inicialización Automática de Datos (Semilla)

Para garantizar que el sistema esté operativo inmediatamente al levantarlo con Docker Compose, se creó un script SQL en [db-init/init.sql](file:///d:/Data-Analytic-Proyect/cafeteria-soap/db-init/init.sql) que se ejecuta al iniciar el contenedor de MySQL.

### Datos Semilla Insertados:

1.  **Categorías de Bebidas**:
    *   ID 1: `Bebidas Calientes`
    *   ID 2: `Bebidas Frías`
    *   ID 3: `Jugos y Batidos`
2.  **Catálogo de Productos**:
    *   *Calientes*: Espresso, Café Americano, Cappuccino, Latte Macchiato, Café Moka.
    *   *Frías*: Iced Americano, Iced Latte, Frappé de Café, Frappé de Oreo.
    *   *Jugos*: Jugo de Naranja, Batido de Fresa, Jugo de Papaya.
3.  **Usuarios iniciales**:
    *   `mesero1` (contraseña: `123`, rol `MESERO`)
    *   `cocinero1` (contraseña: `123`, rol `COCINERO`)
    *   `admin` (contraseña: `123`, rol `ADMINISTRADOR`)
