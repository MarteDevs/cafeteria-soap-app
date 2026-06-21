# Plan Frontend — Sistema de Cafetería
**Stack:** Vue 3 + Vite + Tailwind CSS · Consume SOAP vía fetch  
**Backend:** `http://localhost:8085/ws/`

---

## Paso 1 — Crear el proyecto Vue 3

```bash
npm create vue@latest cafeteria-frontend
```

Seleccionar en el asistente:
- ✅ Add Vue Router
- ✅ Add Pinia (manejo de estado)
- ❌ TypeScript (no necesario)
- ❌ Todo lo demás en No

```bash
cd cafeteria-frontend
npm install
```

---

## Paso 2 — Instalar Tailwind CSS

```bash
npm install -D tailwindcss @tailwindcss/vite
```

En `vite.config.js` agregar el plugin:

```js
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [
    vue(),
    tailwindcss(),
  ],
})
```

En `src/assets/main.css` reemplazar todo con:

```css
@import "tailwindcss";
```

---

## Paso 3 — Estructura de carpetas

```
src/
├── assets/
│   └── main.css
├── services/
│   ├── soapClient.js          ← helper genérico
│   ├── categoriaService.js
│   ├── productoService.js
│   ├── pedidoService.js
│   └── detalleService.js
├── stores/
│   ├── categoriaStore.js
│   ├── productoStore.js
│   └── pedidoStore.js
├── components/
│   ├── Navbar.vue
│   ├── Modal.vue              ← modal reutilizable
│   ├── TablaGenerica.vue      ← tabla reutilizable
│   └── BadgeEstado.vue        ← badge de color por estado
├── views/
│   ├── HomeView.vue           ← dashboard con resumen
│   ├── CategoriasView.vue     ← CRUD categorías
│   ├── ProductosView.vue      ← CRUD productos
│   ├── PedidosView.vue        ← lista todos los pedidos
│   ├── NuevoPedidoView.vue    ← crear pedido + agregar detalles
│   └── CocinaView.vue         ← vista cocina (solo PENDIENTE y EN_PREPARACION)
├── router/
│   └── index.js
└── main.js
```

---

## Paso 4 — Router (`src/router/index.js`)

```js
import { createRouter, createWebHistory } from 'vue-router'
import HomeView        from '../views/HomeView.vue'
import CategoriasView  from '../views/CategoriasView.vue'
import ProductosView   from '../views/ProductosView.vue'
import PedidosView     from '../views/PedidosView.vue'
import NuevoPedidoView from '../views/NuevoPedidoView.vue'
import CocinaView      from '../views/CocinaView.vue'

const routes = [
  { path: '/',            component: HomeView },
  { path: '/categorias',  component: CategoriasView },
  { path: '/productos',   component: ProductosView },
  { path: '/pedidos',     component: PedidosView },
  { path: '/nuevo-pedido',component: NuevoPedidoView },
  { path: '/cocina',      component: CocinaView },
]

export default createRouter({
  history: createWebHistory(),
  routes
})
```

---

## Paso 5 — SOAP Client (`src/services/soapClient.js`)

> ⚠️ El namespace `NS` debe coincidir exactamente con el que aparece en tu WSDL.
> Ábrelo en el navegador y busca `targetNamespace="..."` para confirmarlo.

```js
const BASE = 'http://localhost:8085/ws'
const NS   = 'http://impl.mars.dev/'   // verificar en tu WSDL

export async function callSoap(endpoint, method, paramsXml = '') {
  const envelope = `
    <soapenv:Envelope
      xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
      xmlns:tns="${NS}">
      <soapenv:Header/>
      <soapenv:Body>
        <tns:${method}>${paramsXml}</tns:${method}>
      </soapenv:Body>
    </soapenv:Envelope>`

  const res = await fetch(`${BASE}/${endpoint}`, {
    method:  'POST',
    headers: { 'Content-Type': 'text/xml;charset=UTF-8' },
    body:    envelope
  })

  if (!res.ok) throw new Error(`SOAP error ${res.status}`)
  const text = await res.text()
  return new DOMParser().parseFromString(text, 'text/xml')
}

// Helpers para extraer datos del XML de respuesta
export function getNodes(xml, tag) {
  return [...xml.getElementsByTagNameNS('*', tag)]
}

export function getText(node, tag) {
  const el = node.getElementsByTagNameNS('*', tag)[0]
  return el ? el.textContent.trim() : ''
}
```

---

## Paso 6 — Services

### `categoriaService.js`

```js
import { callSoap, getNodes, getText } from './soapClient.js'

function parseCategoria(node) {
  return {
    id:          getText(node, 'id'),
    nombre:      getText(node, 'nombre'),
    descripcion: getText(node, 'descripcion'),
  }
}

export async function listarCategorias() {
  const xml = await callSoap('categoria', 'listarCategorias')
  return getNodes(xml, 'return').map(parseCategoria)
}

export async function crearCategoria(nombre, descripcion) {
  const xml = await callSoap('categoria', 'crearCategoria', `
    <arg0>
      <nombre>${nombre}</nombre>
      <descripcion>${descripcion}</descripcion>
    </arg0>`)
  return parseCategoria(getNodes(xml, 'return')[0])
}

export async function actualizarCategoria(id, nombre, descripcion) {
  const xml = await callSoap('categoria', 'actualizarCategoria', `
    <arg0>${id}</arg0>
    <arg1>
      <nombre>${nombre}</nombre>
      <descripcion>${descripcion}</descripcion>
    </arg1>`)
  return getText(xml, 'return')
}

export async function eliminarCategoria(id) {
  const xml = await callSoap('categoria', 'eliminarCategoria', `<arg0>${id}</arg0>`)
  return getText(xml, 'return')
}
```

### `productoService.js`

```js
import { callSoap, getNodes, getText } from './soapClient.js'

function parseProducto(node) {
  return {
    id:          getText(node, 'id'),
    nombre:      getText(node, 'nombre'),
    precio:      parseFloat(getText(node, 'precio')),
    stock:       parseInt(getText(node, 'stock')),
    categoriaId: getText(node, 'categoriaId') || getText(node, 'id', node.querySelector?.('[localName="categoria"]')),
  }
}

export async function listarProductos() {
  const xml = await callSoap('producto', 'listarProductos')
  return getNodes(xml, 'return').map(parseProducto)
}

export async function crearProducto(nombre, precio, stock, categoriaId) {
  const xml = await callSoap('producto', 'crearProducto', `
    <arg0>
      <nombre>${nombre}</nombre>
      <precio>${precio}</precio>
      <stock>${stock}</stock>
      <categoria><id>${categoriaId}</id></categoria>
    </arg0>`)
  return parseProducto(getNodes(xml, 'return')[0])
}

export async function actualizarProducto(id, nombre, precio, stock, categoriaId) {
  const xml = await callSoap('producto', 'actualizarProducto', `
    <arg0>${id}</arg0>
    <arg1>
      <nombre>${nombre}</nombre>
      <precio>${precio}</precio>
      <stock>${stock}</stock>
      <categoria><id>${categoriaId}</id></categoria>
    </arg1>`)
  return getText(xml, 'return')
}

export async function eliminarProducto(id) {
  const xml = await callSoap('producto', 'eliminarProducto', `<arg0>${id}</arg0>`)
  return getText(xml, 'return')
}
```

### `pedidoService.js`

```js
import { callSoap, getNodes, getText } from './soapClient.js'

function parsePedido(node) {
  return {
    id:     getText(node, 'id'),
    mesa:   getText(node, 'mesa'),
    estado: getText(node, 'estado'),
    total:  parseFloat(getText(node, 'total')),
    fecha:  getText(node, 'fecha'),
  }
}

export async function listarPedidos() {
  const xml = await callSoap('pedido', 'listarPedidos')
  return getNodes(xml, 'return').map(parsePedido)
}

export async function crearPedido(mesa) {
  const xml = await callSoap('pedido', 'crearPedido', `<arg0>${mesa}</arg0>`)
  return parsePedido(getNodes(xml, 'return')[0])
}

export async function cambiarEstado(id, nuevoEstado) {
  const xml = await callSoap('pedido', 'cambiarEstado',
    `<arg0>${id}</arg0><arg1>${nuevoEstado}</arg1>`)
  return getText(xml, 'return')
}

export async function cancelarPedido(id) {
  const xml = await callSoap('pedido', 'cancelarPedido', `<arg0>${id}</arg0>`)
  return getText(xml, 'return')
}

export async function listarPorEstado(estado) {
  const xml = await callSoap('pedido', 'listarPorEstado', `<arg0>${estado}</arg0>`)
  return getNodes(xml, 'return').map(parsePedido)
}
```

### `detalleService.js`

```js
import { callSoap, getNodes, getText } from './soapClient.js'

function parseDetalle(node) {
  return {
    id:            getText(node, 'id'),
    cantidad:      parseInt(getText(node, 'cantidad')),
    precioUnitario:parseFloat(getText(node, 'precioUnitario')),
    subtotal:      parseFloat(getText(node, 'subtotal')),
    productoId:    getText(node, 'productoId'),
    productoNombre:getText(node, 'nombre'),   // ajustar según tu WSDL
  }
}

export async function listarDetallesPorPedido(pedidoId) {
  const xml = await callSoap('detalle', 'listarDetallesPorPedido', `<arg0>${pedidoId}</arg0>`)
  return getNodes(xml, 'return').map(parseDetalle)
}

export async function agregarDetalle(pedidoId, productoId, cantidad) {
  const xml = await callSoap('detalle', 'agregarDetalle',
    `<arg0>${pedidoId}</arg0><arg1>${productoId}</arg1><arg2>${cantidad}</arg2>`)
  return parseDetalle(getNodes(xml, 'return')[0])
}

export async function eliminarDetalle(detalleId) {
  const xml = await callSoap('detalle', 'eliminarDetalle', `<arg0>${detalleId}</arg0>`)
  return getText(xml, 'return')
}
```

---

## Paso 7 — Vistas

### Orden de implementación

| # | Vista | Depende de |
|---|-------|------------|
| 1 | `CategoriasView.vue` | `categoriaService` |
| 2 | `ProductosView.vue` | `productoService` + `categoriaService` |
| 3 | `NuevoPedidoView.vue` | `pedidoService` + `productoService` + `detalleService` |
| 4 | `PedidosView.vue` | `pedidoService` |
| 5 | `CocinaView.vue` | `pedidoService` |
| 6 | `HomeView.vue` | todos (solo lectura, resumen) |

---

### Patrón base de cada vista (CRUD)

Cada vista de CRUD sigue este patrón:

```vue
<script setup>
import { ref, onMounted } from 'vue'
import { listar, crear, actualizar, eliminar } from '../services/XxxService.js'

const items     = ref([])
const form      = ref({ id: null, campo1: '', campo2: '' })
const editando  = ref(false)
const showModal = ref(false)
const loading   = ref(false)
const error     = ref('')

async function cargar() {
  loading.value = true
  try   { items.value = await listar() }
  catch { error.value = 'Error al cargar datos' }
  finally { loading.value = false }
}

function abrirCrear() {
  form.value  = { id: null, campo1: '', campo2: '' }
  editando.value = false
  showModal.value = true
}

function abrirEditar(item) {
  form.value  = { ...item }
  editando.value = true
  showModal.value = true
}

async function guardar() {
  if (editando.value) {
    await actualizar(form.value.id, form.value.campo1, form.value.campo2)
  } else {
    await crear(form.value.campo1, form.value.campo2)
  }
  showModal.value = false
  await cargar()
}

async function borrar(id) {
  if (!confirm('¿Eliminar?')) return
  await eliminar(id)
  await cargar()
}

onMounted(cargar)
</script>
```

---

### `NuevoPedidoView.vue` — flujo especial

Esta vista tiene el flujo más complejo:

```
1. Ingresar número de mesa → crearPedido() → guarda pedidoId
2. Mostrar catálogo de productos (filtrable por categoría)
3. Para cada producto: botón "+ Agregar" → agregarDetalle(pedidoId, productoId, cantidad)
4. Mostrar lista de detalles agregados con subtotales
5. Botón "Confirmar Pedido" → cambiarEstado(pedidoId, 'PENDIENTE') (ya viene así)
   o navegar a /pedidos
```

Estado local que necesita:

```js
const pedidoActual    = ref(null)   // { id, mesa, estado, total }
const detalles        = ref([])     // lista de DetallePedido
const productos       = ref([])     // catálogo completo
const categorias      = ref([])     // para el filtro
const categoriaFiltro = ref('')     // id de categoría seleccionada
const cantidades      = ref({})     // { productoId: cantidad } para el input
```

---

### `CocinaView.vue` — flujo especial

Vista de solo gestión de estados, sin formularios:

```
Muestra 2 columnas:
│  PENDIENTES          │  EN PREPARACIÓN      │
│  [Mesa 3] [▶ Iniciar]│  [Mesa 1] [✅ Listo] │
│  [Mesa 5] [▶ Iniciar]│  [Mesa 2] [✅ Listo] │

Auto-refresh cada 10 segundos
```

```js
// auto-refresh
onMounted(() => {
  cargar()
  const interval = setInterval(cargar, 10000)
  onUnmounted(() => clearInterval(interval))
})
```

---

### `Navbar.vue`

```vue
<template>
  <nav class="bg-amber-700 text-white px-6 py-3 flex gap-6 items-center shadow">
    <span class="font-bold text-lg">☕ Cafetería</span>
    <RouterLink to="/"             class="hover:underline">Inicio</RouterLink>
    <RouterLink to="/categorias"   class="hover:underline">Categorías</RouterLink>
    <RouterLink to="/productos"    class="hover:underline">Productos</RouterLink>
    <RouterLink to="/nuevo-pedido" class="hover:underline">Nuevo Pedido</RouterLink>
    <RouterLink to="/pedidos"      class="hover:underline">Pedidos</RouterLink>
    <RouterLink to="/cocina"       class="hover:underline bg-amber-900 px-3 py-1 rounded">
      🍳 Cocina
    </RouterLink>
  </nav>
</template>
```

---

### `BadgeEstado.vue` — colores por estado

```vue
<script setup>
defineProps(['estado'])
const colores = {
  PENDIENTE:       'bg-yellow-100 text-yellow-800',
  EN_PREPARACION:  'bg-blue-100 text-blue-800',
  ENTREGADO:       'bg-green-100 text-green-800',
  CANCELADO:       'bg-red-100 text-red-800',
}
</script>

<template>
  <span :class="['px-2 py-1 rounded text-xs font-semibold', colores[estado] ?? 'bg-gray-100']">
    {{ estado }}
  </span>
</template>
```

---

## Paso 8 — Manejar CORS

El servidor Spring Boot rechazará las peticiones fetch desde Vue porque corren en puertos distintos.
Crear este archivo en el servidor:

```java
// src/main/java/mars/dev/config/CorsConfig.java
package mars.dev.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173"));
        config.setAllowedMethods(List.of("POST", "GET", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

Reiniciar el servidor después de agregar esto.

---

## Paso 9 — `App.vue` limpio

```vue
<template>
  <Navbar />
  <main class="p-6 bg-gray-50 min-h-screen">
    <RouterView />
  </main>
</template>

<script setup>
import Navbar from './components/Navbar.vue'
</script>
```

---

## Paso 10 — Verificar el namespace del WSDL

Antes de hacer cualquier llamada SOAP, abrir en el navegador:

```
http://localhost:8085/ws/categoria?wsdl
```

Buscar la línea `targetNamespace` y copiar exactamente ese valor en `soapClient.js`:

```js
// Ejemplo — el tuyo puede ser diferente
const NS = 'http://impl.mars.dev/'
```

Si el namespace está mal, el servidor devolverá un fault SOAP y ninguna llamada funcionará.

---

## Orden final de trabajo

```
Día 1 — Base
  ✅ crear proyecto Vue + Tailwind
  ✅ configurar router + App.vue + Navbar
  ✅ implementar soapClient.js
  ✅ implementar categoriaService.js
  ✅ CategoriasView.vue funcionando con CRUD completo

Día 2 — Productos y Pedidos
  ✅ productoService.js
  ✅ ProductosView.vue (con filtro por categoría)
  ✅ pedidoService.js
  ✅ PedidosView.vue (lista + cambio de estado)

Día 3 — Flujo principal + Cocina
  ✅ detalleService.js
  ✅ NuevoPedidoView.vue (catálogo + carrito + confirmar)
  ✅ CocinaView.vue (con auto-refresh)
  ✅ HomeView.vue (dashboard con conteos)
```

---

> **Tip de debug:** Si el fetch falla silenciosamente, abrir DevTools → Network →
> ver el cuerpo de la respuesta XML. Los errores SOAP vienen como `<soap:Fault>`
> con un `<faultstring>` que dice exactamente qué está mal.
