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
  <div class="max-w-6xl mx-auto font-inter">
    <div class="flex justify-between items-end mb-8">
      <div>
        <h1 class="text-4xl font-outfit font-bold text-white mb-2">Nuevo Pedido</h1>
        <p class="text-[#a09fb9]">
          <span v-if="paso === 1">Paso 1: Identificar la mesa</span>
          <span v-else>Paso 2: Agregar productos (Mesa {{ pedidoActual?.mesa }})</span>
        </p>
      </div>
      
      <div class="flex gap-4 items-center" v-if="paso === 2">
        <div class="glass-panel px-6 py-2 flex items-center gap-3">
          <span class="text-[#a09fb9] text-sm uppercase tracking-wider">Total</span>
          <span class="text-2xl font-outfit font-bold text-[#ff7a45]">S/. {{ totalPedido.toFixed(2) }}</span>
        </div>
        <button @click="finalizarPedido" class="btn-primary">
          Confirmar Orden
        </button>
      </div>
    </div>

    <div v-if="error" class="bg-[#93000a]/20 text-[#ffb4ab] border border-[#93000a] p-4 rounded-xl mb-6 backdrop-blur-md">
      {{ error }}
    </div>

    <!-- PASO 1: Ingreso de Mesa -->
    <div v-if="paso === 1" class="max-w-md mx-auto glass-panel p-10 mt-16 animate-fade-in text-center">
      <div class="w-20 h-20 bg-gradient-to-br from-[#ff7a45]/20 to-[#6b46c1]/20 border border-[#ff7a45]/30 rounded-2xl flex items-center justify-center mx-auto mb-6 shadow-[0_0_20px_rgba(255,122,69,0.15)]">
        <svg class="w-10 h-10 text-[#ff7a45]" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"></path></svg>
      </div>
      <h2 class="text-2xl font-outfit font-bold text-white mb-6">Apertura de Orden</h2>
      
      <form @submit.prevent="iniciarPedido" class="space-y-6">
        <div class="text-left">
          <label class="block text-sm font-medium text-[#c6c4df] mb-2">Mesa o Referencia</label>
          <input v-model="mesa" required type="text" class="w-full bg-[#131316] border border-white/10 rounded-xl px-4 py-3 text-white focus:outline-none focus:border-[#ff7a45] focus:shadow-[0_0_10px_rgba(255,122,69,0.3)] transition-all" placeholder="Ej: Mesa 5" :disabled="loading">
        </div>
        <button type="submit" :disabled="loading || !mesa" class="w-full btn-primary disabled:opacity-50 disabled:cursor-not-allowed">
          {{ loading ? 'Iniciando...' : 'Comenzar Pedido' }}
        </button>
      </form>
    </div>

    <!-- PASO 2: Catálogo y Carrito -->
    <div v-if="paso === 2" class="grid grid-cols-1 lg:grid-cols-3 gap-8 animate-fade-in relative">
      
      <!-- Catálogo (2/3) -->
      <div class="lg:col-span-2 flex flex-col gap-6">
        <!-- Filtro de categorías -->
        <div class="flex gap-3 overflow-x-auto pb-2 scrollbar-hide">
          <button @click="categoriaFiltro = ''" :class="['px-5 py-2 rounded-full font-medium whitespace-nowrap transition-all duration-300 border', categoriaFiltro === '' ? 'bg-[#ff7a45] text-[#131316] border-[#ff7a45] shadow-[0_0_15px_rgba(255,122,69,0.4)]' : 'bg-[#1a1a2e]/50 text-[#c6c4df] border-white/10 hover:border-white/30']">
            Todos
          </button>
          <button v-for="cat in categorias" :key="cat.id" @click="categoriaFiltro = cat.id" :class="['px-5 py-2 rounded-full font-medium whitespace-nowrap transition-all duration-300 border', String(categoriaFiltro) === String(cat.id) ? 'bg-[#ff7a45] text-[#131316] border-[#ff7a45] shadow-[0_0_15px_rgba(255,122,69,0.4)]' : 'bg-[#1a1a2e]/50 text-[#c6c4df] border-white/10 hover:border-white/30']">
            {{ cat.nombre }}
          </button>
        </div>

        <div v-if="loading && productos.length === 0" class="flex justify-center py-20">
          <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-[#ff7a45]"></div>
        </div>

        <!-- Grid de Productos -->
        <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div v-for="prod in productosFiltrados" :key="prod.id" class="glass-card flex flex-col relative group">
            <!-- Imagen Placeholder con Gradiente -->
            <div class="h-40 w-full bg-gradient-to-br from-[#1a1a2e] to-[#2a2a2d] border-b border-white/5 relative overflow-hidden">
               <!-- Simulación de imagen con icono para ambiente espacial -->
               <div class="absolute inset-0 flex items-center justify-center opacity-20 group-hover:scale-110 transition-transform duration-500">
                  <svg class="w-20 h-20 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z"></path></svg>
               </div>
               <div class="absolute inset-0 bg-gradient-to-t from-[#131316] to-transparent"></div>
               <div class="absolute bottom-3 left-4 right-4 flex justify-between items-end">
                 <h3 class="font-outfit font-bold text-xl text-white drop-shadow-md">{{ prod.nombre }}</h3>
                 <span class="text-[#ff7a45] font-outfit font-bold text-xl">S/. {{ prod.precio.toFixed(2) }}</span>
               </div>
            </div>

            <div class="p-5 flex flex-col flex-1">
              <p class="text-sm text-[#a09fb9] mb-4">Stock: <span :class="prod.stock > 0 ? 'text-[#e9ddff]' : 'text-[#ffb4ab] font-bold'">{{ prod.stock }}</span></p>
              
              <div class="flex gap-3 items-center mt-auto" v-if="prod.stock > 0">
                <input type="number" v-model.number="cantidades[prod.id]" min="1" :max="prod.stock" class="w-16 bg-[#0f0f12] border border-white/10 rounded-lg px-2 py-2 text-center text-white focus:outline-none focus:border-[#6b46c1]">
                <button @click="agregarAlCarrito(prod)" :disabled="loading" class="flex-1 bg-white/5 border border-white/10 hover:border-[#ff7a45]/50 hover:bg-[#ff7a45]/10 text-white py-2 rounded-lg font-medium transition-all duration-300 disabled:opacity-50">
                  Agregar
                </button>
              </div>
              <div v-else class="mt-auto text-center text-[#ffb4ab] text-sm bg-[#93000a]/20 border border-[#93000a]/50 rounded-lg py-2">
                Agotado
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Carrito (1/3) -->
      <div class="lg:col-span-1">
        <div class="glass-panel overflow-hidden sticky top-8 flex flex-col max-h-[calc(100vh-120px)]">
          <div class="p-5 border-b border-white/10 bg-[#1a1a2e]/80 backdrop-blur-md">
            <h3 class="font-outfit font-bold text-xl text-white flex items-center gap-2">
              <svg class="w-5 h-5 text-[#ff7a45]" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z"></path></svg>
              Orden Actual
            </h3>
          </div>
          
          <div class="p-0 overflow-y-auto flex-1 scrollbar-hide">
            <ul v-if="detalles.length > 0" class="divide-y divide-white/5">
              <li v-for="det in detalles" :key="det.id" class="p-5 hover:bg-white/5 transition-colors group relative overflow-hidden">
                <div class="absolute inset-y-0 left-0 w-1 bg-[#ff7a45] opacity-0 group-hover:opacity-100 transition-opacity"></div>
                <div class="flex justify-between mb-1">
                  <span class="font-medium text-[#e4e1e6]">{{ det.productoNombre }}</span>
                  <span class="font-outfit font-bold text-[#e4e1e6]">S/. {{ det.subtotal.toFixed(2) }}</span>
                </div>
                <div class="flex justify-between items-center text-sm text-[#a09fb9]">
                  <span>{{ det.cantidad }} x S/. {{ det.precioUnitario.toFixed(2) }}</span>
                  <button @click="quitarDelCarrito(det)" class="text-[#ffb4ab] hover:text-[#ffdad6] opacity-0 group-hover:opacity-100 transition-opacity bg-[#93000a]/50 p-1 rounded">
                    <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
                  </button>
                </div>
              </li>
            </ul>
            <div v-else class="p-10 text-center flex flex-col items-center justify-center h-full opacity-50">
              <svg class="w-16 h-16 mb-4 text-[#a09fb9]" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z"></path></svg>
              <p class="text-[#a09fb9] font-medium">El carrito está vacío</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.scrollbar-hide::-webkit-scrollbar {
    display: none;
}
.scrollbar-hide {
    -ms-overflow-style: none;
    scrollbar-width: none;
}
</style>
