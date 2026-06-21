<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { listarPorEstado, cambiarEstado } from '../services/pedidoService.js'
import { listarDetallesPorPedido } from '../services/detalleService.js'

const pendientes = ref([])
const enPreparacion = ref([])
const detallesCargados = ref({}) // Cache de detalles
const loading = ref(false)
const error = ref('')

async function cargar() {
  error.value = ''
  try {
    const [pRes, prepRes] = await Promise.all([
      listarPorEstado('PENDIENTE'),
      listarPorEstado('EN_PREPARACION')
    ])
    pendientes.value = pRes.sort((a, b) => parseInt(a.id) - parseInt(b.id))
    enPreparacion.value = prepRes.sort((a, b) => parseInt(a.id) - parseInt(b.id))
    
    // Cargar detalles de los pedidos que no tenemos cacheados
    const todos = [...pRes, ...prepRes]
    for (const ped of todos) {
      if (!detallesCargados.value[ped.id]) {
        detallesCargados.value[ped.id] = await listarDetallesPorPedido(ped.id)
      }
    }
  } catch (err) {
    error.value = 'Error al actualizar pedidos de cocina.'
    console.error(err)
  }
}

async function iniciarPreparacion(id) {
  try {
    await cambiarEstado(id, 'EN_PREPARACION')
    await cargar()
  } catch (err) {
    alert('Error: ' + err.message)
  }
}

async function marcarListo(id) {
  try {
    await cambiarEstado(id, 'ENTREGADO')
    await cargar()
  } catch (err) {
    alert('Error: ' + err.message)
  }
}

let interval
onMounted(() => {
  loading.value = true
  cargar().finally(() => loading.value = false)
  interval = setInterval(cargar, 10000)
})

onUnmounted(() => {
  clearInterval(interval)
})
</script>

<template>
  <div class="h-[calc(100vh-8rem)] flex flex-col font-inter">
    <div class="flex justify-between items-end mb-6">
      <div>
        <h1 class="text-4xl font-outfit font-bold text-white mb-2">Vista de Cocina</h1>
        <p class="text-[#a09fb9] flex items-center gap-2">
          <span class="relative flex h-3 w-3">
            <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-[#ff7a45] opacity-75"></span>
            <span class="relative inline-flex rounded-full h-3 w-3 bg-[#ff7a45] shadow-[0_0_10px_rgba(255,122,69,0.8)]"></span>
          </span>
          Actualización en tiempo real
        </p>
      </div>
    </div>

    <div v-if="error" class="bg-[#93000a]/20 text-[#ffb4ab] border border-[#93000a] p-4 rounded-xl mb-6 backdrop-blur-md">
      {{ error }}
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-8 flex-1 min-h-0">
      <!-- Columna Pendientes -->
      <div class="glass-panel flex flex-col overflow-hidden border border-[#ff7a45]/30 shadow-[0_0_20px_rgba(255,122,69,0.1)]">
        <div class="bg-[#ff7a45]/10 p-5 border-b border-[#ff7a45]/20 backdrop-blur-md">
          <h2 class="font-outfit font-bold text-[#ffb394] text-xl flex items-center gap-2">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
            Pendientes ({{ pendientes.length }})
          </h2>
        </div>
        <div class="p-5 overflow-y-auto flex-1 space-y-4 scrollbar-hide">
          <div v-if="loading && pendientes.length === 0" class="flex justify-center py-8">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-[#ff7a45]"></div>
          </div>
          <div v-for="ped in pendientes" :key="ped.id" class="bg-[#131316]/80 rounded-xl p-5 shadow-sm border border-white/5 hover:border-[#ff7a45]/50 transition-colors animate-fade-in relative overflow-hidden group">
            <div class="absolute top-0 left-0 w-1 h-full bg-[#ff7a45] shadow-[0_0_10px_rgba(255,122,69,1)]"></div>
            <div class="flex justify-between items-start mb-4 pl-3">
              <div>
                <h3 class="font-outfit font-bold text-xl text-white">Mesa {{ ped.mesa }}</h3>
                <p class="text-xs text-[#a09fb9]">Pedido #{{ ped.id }}</p>
              </div>
              <button @click="iniciarPreparacion(ped.id)" class="bg-white/5 hover:bg-[#6b46c1]/20 border border-white/10 hover:border-[#6b46c1]/50 text-white px-4 py-2 rounded-lg font-medium transition-all shadow-sm text-sm flex items-center gap-2">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14.752 11.168l-3.197-2.132A1 1 0 0010 9.87v4.263a1 1 0 001.555.832l3.197-2.132a1 1 0 000-1.664z"></path><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 12a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                Preparar
              </button>
            </div>
            
            <ul class="pl-3 space-y-2">
              <li v-for="det in detallesCargados[ped.id]" :key="det.id" class="flex justify-between text-[#c6c4df] text-sm py-2 border-t border-white/5">
                <span><span class="font-bold text-[#ff7a45] mr-2 text-base">{{ det.cantidad }}x</span> {{ det.productoNombre }}</span>
              </li>
              <li v-if="!detallesCargados[ped.id]" class="text-[#a09fb9] text-sm italic">Cargando detalles...</li>
            </ul>
          </div>
          <div v-if="pendientes.length === 0 && !loading" class="text-center text-[#a09fb9] font-medium py-16 flex flex-col items-center">
            <svg class="w-16 h-16 mb-4 text-white/10" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M5 13l4 4L19 7"></path></svg>
            No hay pedidos pendientes
          </div>
        </div>
      </div>

      <!-- Columna En Preparación -->
      <div class="glass-panel flex flex-col overflow-hidden border border-[#6b46c1]/30 shadow-[0_0_20px_rgba(107,70,193,0.1)]">
        <div class="bg-[#6b46c1]/10 p-5 border-b border-[#6b46c1]/20 backdrop-blur-md">
          <h2 class="font-outfit font-bold text-[#d9cbf6] text-xl flex items-center gap-2">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 002-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path></svg>
            En Preparación ({{ enPreparacion.length }})
          </h2>
        </div>
        <div class="p-5 overflow-y-auto flex-1 space-y-4 scrollbar-hide">
          <div v-if="loading && enPreparacion.length === 0" class="flex justify-center py-8">
            <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-[#6b46c1]"></div>
          </div>
          <div v-for="ped in enPreparacion" :key="ped.id" class="bg-[#131316]/80 rounded-xl p-5 shadow-sm border border-white/5 hover:border-[#6b46c1]/50 transition-colors animate-fade-in relative overflow-hidden">
            <div class="absolute top-0 left-0 w-1 h-full bg-[#6b46c1] shadow-[0_0_10px_rgba(107,70,193,1)]"></div>
            <div class="flex justify-between items-start mb-4 pl-3">
              <div>
                <h3 class="font-outfit font-bold text-xl text-white">Mesa {{ ped.mesa }}</h3>
                <p class="text-xs text-[#a09fb9]">Pedido #{{ ped.id }}</p>
              </div>
              <button @click="marcarListo(ped.id)" class="bg-[#6b46c1] hover:bg-[#5536a0] text-white px-4 py-2 rounded-lg font-medium transition-all shadow-[0_0_15px_rgba(107,70,193,0.4)] text-sm flex items-center gap-2">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
                Terminado
              </button>
            </div>
            
            <ul class="pl-3 space-y-2">
              <li v-for="det in detallesCargados[ped.id]" :key="det.id" class="flex justify-between text-[#c6c4df] text-sm py-2 border-t border-white/5">
                <span><span class="font-bold text-[#d9cbf6] mr-2 text-base">{{ det.cantidad }}x</span> {{ det.productoNombre }}</span>
              </li>
              <li v-if="!detallesCargados[ped.id]" class="text-[#a09fb9] text-sm italic">Cargando detalles...</li>
            </ul>
          </div>
          <div v-if="enPreparacion.length === 0 && !loading" class="text-center text-[#a09fb9] font-medium py-16 flex flex-col items-center">
            <svg class="w-16 h-16 mb-4 text-white/10" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
            No hay pedidos en preparación
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
