<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { crearPedido } from '../services/pedidoService.js'
import { listarProductos } from '../services/productoService.js'
import { listarCategorias } from '../services/categoriaService.js'
import { agregarDetalle, listarDetallesPorPedido, eliminarDetalle } from '../services/detalleService.js'

const router = useRouter()

// Pasos: 1 = Crear Pedido (Mesa), 2 = Agregar Productos
const paso = ref(1)

const mesa = ref('')
const pedidoActual = ref(null)

const productos = ref([])
const categorias = ref([])
const categoriaFiltro = ref('')

const detalles = ref([])
const cantidades = ref({}) // { productoId: cantidad }

const loading = ref(false)
const error = ref('')

async function cargarDatos() {
  loading.value = true
  try {
    const [pRes, cRes] = await Promise.all([
      listarProductos(),
      listarCategorias()
    ])
    productos.value = pRes
    categorias.value = cRes
    
    // Inicializar cantidades
    pRes.forEach(p => { cantidades.value[p.id] = 1 })
  } catch (err) {
    error.value = 'Error al cargar el catálogo de productos.'
    console.error(err)
  } finally {
    loading.value = false
  }
}

const productosFiltrados = computed(() => {
  if (!categoriaFiltro.value) return productos.value
  return productos.value.filter(p => String(p.categoriaId) === String(categoriaFiltro.value))
})

const totalPedido = computed(() => {
  return detalles.value.reduce((sum, det) => sum + det.subtotal, 0)
})

async function iniciarPedido() {
  if (!mesa.value) return
  try {
    loading.value = true
    pedidoActual.value = await crearPedido(mesa.value)
    paso.value = 2
    await cargarDatos()
  } catch (err) {
    error.value = 'Error al crear el pedido: ' + err.message
  } finally {
    loading.value = false
  }
}

async function agregarAlCarrito(producto) {
  const cant = cantidades.value[producto.id]
  if (cant < 1) return
  
  if (producto.stock < cant) {
    alert(`Stock insuficiente. Solo quedan ${producto.stock} disponibles.`)
    return
  }

  try {
    loading.value = true
    await agregarDetalle(pedidoActual.value.id, producto.id, cant)
    // Refrescar carrito
    detalles.value = await listarDetallesPorPedido(pedidoActual.value.id)
    
    // Actualizar stock localmente (idealmente recargar productos)
    producto.stock -= cant
    cantidades.value[producto.id] = 1 // Resetear input
  } catch (err) {
    alert('Error al agregar: ' + err.message)
  } finally {
    loading.value = false
  }
}

async function quitarDelCarrito(detalle) {
  try {
    loading.value = true
    await eliminarDetalle(detalle.id)
    detalles.value = await listarDetallesPorPedido(pedidoActual.value.id)
    
    // Devolver stock localmente
    const p = productos.value.find(p => String(p.id) === String(detalle.productoId))
    if (p) p.stock += detalle.cantidad
  } catch (err) {
    alert('Error al quitar: ' + err.message)
  } finally {
    loading.value = false
  }
}

function finalizarPedido() {
  // El pedido ya está PENDIENTE desde que se creó
  router.push('/pedidos')
}
</script>

<template>
  <div class="max-w-5xl mx-auto">
    <div class="flex justify-between items-center mb-8">
      <div>
        <h1 class="text-3xl font-bold text-slate-800">Nuevo Pedido</h1>
        <p class="text-slate-500 mt-1">
          <span v-if="paso === 1">Paso 1: Identificar la mesa</span>
          <span v-else>Paso 2: Agregar productos (Mesa {{ pedidoActual?.mesa }})</span>
        </p>
      </div>
      
      <div class="flex gap-2" v-if="paso === 2">
        <div class="bg-amber-100 text-amber-800 px-4 py-2 rounded-lg font-bold text-lg shadow-sm border border-amber-200">
          Total: ${{ totalPedido.toFixed(2) }}
        </div>
        <button @click="finalizarPedido" class="bg-green-600 hover:bg-green-700 text-white px-6 py-2 rounded-lg shadow-md transition-colors font-bold">
          Confirmar Orden
        </button>
      </div>
    </div>

    <div v-if="error" class="bg-red-50 text-red-600 p-4 rounded-lg border border-red-200 mb-6 shadow-sm">
      {{ error }}
    </div>

    <!-- PASO 1: Ingreso de Mesa -->
    <div v-if="paso === 1" class="max-w-md mx-auto bg-white p-8 rounded-2xl shadow-sm border border-slate-200 mt-12 animate-fade-in">
      <div class="text-center mb-6">
        <div class="w-16 h-16 bg-amber-100 text-amber-600 rounded-full flex items-center justify-center mx-auto mb-4">
          <svg class="w-8 h-8" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path></svg>
        </div>
        <h2 class="text-xl font-bold text-slate-800">Apertura de Orden</h2>
      </div>
      
      <form @submit.prevent="iniciarPedido" class="space-y-4">
        <div>
          <label class="block text-sm font-medium text-slate-700 mb-2">Número o Nombre de la Mesa</label>
          <input v-model="mesa" required type="text" class="w-full px-4 py-3 border border-slate-300 rounded-xl focus:ring-2 focus:ring-amber-500 focus:border-amber-500 outline-none transition-shadow text-lg text-center" placeholder="Ej: Mesa 5" :disabled="loading">
        </div>
        <button type="submit" :disabled="loading || !mesa" class="w-full bg-amber-600 hover:bg-amber-700 disabled:bg-slate-300 disabled:cursor-not-allowed text-white py-3 rounded-xl font-bold shadow-md transition-all">
          {{ loading ? 'Iniciando...' : 'Comenzar Pedido' }}
        </button>
      </form>
    </div>

    <!-- PASO 2: Catálogo y Carrito -->
    <div v-if="paso === 2" class="grid grid-cols-1 lg:grid-cols-3 gap-8 animate-fade-in">
      
      <!-- Catálogo (2/3) -->
      <div class="lg:col-span-2 space-y-6">
        <!-- Filtro de categorías -->
        <div class="flex gap-2 overflow-x-auto pb-2 scrollbar-hide">
          <button @click="categoriaFiltro = ''" :class="['px-4 py-2 rounded-full font-medium whitespace-nowrap transition-colors border', categoriaFiltro === '' ? 'bg-amber-600 text-white border-amber-600 shadow-md' : 'bg-white text-slate-600 hover:bg-slate-50 border-slate-200']">
            Todos
          </button>
          <button v-for="cat in categorias" :key="cat.id" @click="categoriaFiltro = cat.id" :class="['px-4 py-2 rounded-full font-medium whitespace-nowrap transition-colors border', String(categoriaFiltro) === String(cat.id) ? 'bg-amber-600 text-white border-amber-600 shadow-md' : 'bg-white text-slate-600 hover:bg-slate-50 border-slate-200']">
            {{ cat.nombre }}
          </button>
        </div>

        <div v-if="loading && productos.length === 0" class="flex justify-center py-12">
          <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-amber-600"></div>
        </div>

        <!-- Grid de Productos -->
        <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-4">
          <div v-for="prod in productosFiltrados" :key="prod.id" class="bg-white rounded-xl shadow-sm border border-slate-200 p-5 hover:shadow-md transition-shadow flex flex-col justify-between">
            <div>
              <div class="flex justify-between items-start mb-2">
                <h3 class="font-bold text-lg text-slate-800">{{ prod.nombre }}</h3>
                <span class="text-amber-600 font-bold text-lg">${{ prod.precio.toFixed(2) }}</span>
              </div>
              <p class="text-sm text-slate-500 mb-4">Stock disponible: <span :class="prod.stock > 0 ? 'text-green-600 font-semibold' : 'text-red-600 font-bold'">{{ prod.stock }}</span></p>
            </div>
            
            <div class="flex gap-2 items-center mt-auto pt-4 border-t border-slate-100" v-if="prod.stock > 0">
              <input type="number" v-model.number="cantidades[prod.id]" min="1" :max="prod.stock" class="w-20 px-3 py-2 border border-slate-300 rounded-lg text-center focus:ring-amber-500 outline-none">
              <button @click="agregarAlCarrito(prod)" :disabled="loading" class="flex-1 bg-slate-800 hover:bg-slate-900 text-white py-2 rounded-lg font-medium transition-colors disabled:opacity-50">
                Agregar
              </button>
            </div>
            <div v-else class="mt-auto pt-4 text-center text-red-500 font-semibold bg-red-50 rounded-lg py-2">
              Agotado
            </div>
          </div>
        </div>
      </div>

      <!-- Carrito (1/3) -->
      <div class="lg:col-span-1">
        <div class="bg-white rounded-2xl shadow-sm border border-slate-200 overflow-hidden sticky top-24">
          <div class="bg-slate-800 p-4 text-white">
            <h3 class="font-bold text-lg flex items-center gap-2">
              <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"></path></svg>
              Detalle de Orden
            </h3>
          </div>
          
          <div class="p-0 max-h-[60vh] overflow-y-auto">
            <ul v-if="detalles.length > 0" class="divide-y divide-slate-100">
              <li v-for="det in detalles" :key="det.id" class="p-4 hover:bg-slate-50 transition-colors group">
                <div class="flex justify-between mb-1">
                  <span class="font-semibold text-slate-800">{{ det.productoNombre }}</span>
                  <span class="font-bold text-slate-800">${{ det.subtotal.toFixed(2) }}</span>
                </div>
                <div class="flex justify-between items-center text-sm text-slate-500">
                  <span>{{ det.cantidad }} x ${{ det.precioUnitario.toFixed(2) }}</span>
                  <button @click="quitarDelCarrito(det)" class="text-red-500 hover:text-red-700 opacity-0 group-hover:opacity-100 transition-opacity">
                    <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
                  </button>
                </div>
              </li>
            </ul>
            <div v-else class="p-8 text-center text-slate-400 flex flex-col items-center">
              <svg class="w-12 h-12 mb-3 text-slate-200" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"></path></svg>
              <p>El carrito está vacío</p>
            </div>
          </div>
          
          <div class="p-4 bg-slate-50 border-t border-slate-200">
            <div class="flex justify-between items-center mb-4">
              <span class="text-slate-600 font-medium">Total:</span>
              <span class="text-2xl font-bold text-amber-600">${{ totalPedido.toFixed(2) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Ocultar scrollbar en el filtro de categorías */
.scrollbar-hide::-webkit-scrollbar {
    display: none;
}
.scrollbar-hide {
    -ms-overflow-style: none;
    scrollbar-width: none;
}
</style>
