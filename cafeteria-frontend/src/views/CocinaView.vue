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
  <div class="h-[calc(100vh-8rem)] flex flex-col">
    <div class="flex justify-between items-center mb-6">
      <div>
        <h1 class="text-3xl font-bold text-slate-800">Vista de Cocina</h1>
        <p class="text-slate-500 mt-1 flex items-center gap-2">
          <span class="relative flex h-3 w-3">
            <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-green-400 opacity-75"></span>
            <span class="relative inline-flex rounded-full h-3 w-3 bg-green-500"></span>
          </span>
          Actualización en tiempo real
        </p>
      </div>
    </div>

    <div v-if="error" class="bg-red-50 text-red-600 p-4 rounded-lg border border-red-200 mb-6">
      {{ error }}
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-6 flex-1 min-h-0">
      <!-- Columna Pendientes -->
      <div class="bg-yellow-50 rounded-2xl border border-yellow-200 flex flex-col overflow-hidden">
        <div class="bg-yellow-100 p-4 border-b border-yellow-200">
          <h2 class="font-bold text-yellow-800 text-lg flex items-center gap-2">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
            Pendientes ({{ pendientes.length }})
          </h2>
        </div>
        <div class="p-4 overflow-y-auto flex-1 space-y-4">
          <div v-if="loading && pendientes.length === 0" class="text-center text-yellow-600 py-8">Cargando...</div>
          <div v-for="ped in pendientes" :key="ped.id" class="bg-white rounded-xl p-4 shadow-sm border border-yellow-100 animate-fade-in relative overflow-hidden group">
            <div class="absolute top-0 left-0 w-1 h-full bg-yellow-400"></div>
            <div class="flex justify-between items-start mb-3 pl-2">
              <div>
                <h3 class="font-bold text-lg text-slate-800">Mesa {{ ped.mesa }}</h3>
                <p class="text-xs text-slate-500">Pedido #{{ ped.id }}</p>
              </div>
              <button @click="iniciarPreparacion(ped.id)" class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors shadow-sm text-sm">
                ▶ Iniciar
              </button>
            </div>
            
            <ul class="pl-2 space-y-1">
              <li v-for="det in detallesCargados[ped.id]" :key="det.id" class="flex justify-between text-slate-700 text-sm py-1 border-b border-slate-50 last:border-0">
                <span><span class="font-bold text-slate-900 mr-1">{{ det.cantidad }}x</span> {{ det.productoNombre }}</span>
              </li>
              <li v-if="!detallesCargados[ped.id]" class="text-slate-400 text-sm italic">Cargando detalles...</li>
            </ul>
          </div>
          <div v-if="pendientes.length === 0 && !loading" class="text-center text-yellow-600/50 font-medium py-12 flex flex-col items-center">
            <svg class="w-12 h-12 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"></path></svg>
            No hay pedidos pendientes
          </div>
        </div>
      </div>

      <!-- Columna En Preparación -->
      <div class="bg-blue-50 rounded-2xl border border-blue-200 flex flex-col overflow-hidden">
        <div class="bg-blue-100 p-4 border-b border-blue-200">
          <h2 class="font-bold text-blue-800 text-lg flex items-center gap-2">
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 002-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path></svg>
            En Preparación ({{ enPreparacion.length }})
          </h2>
        </div>
        <div class="p-4 overflow-y-auto flex-1 space-y-4">
          <div v-if="loading && enPreparacion.length === 0" class="text-center text-blue-600 py-8">Cargando...</div>
          <div v-for="ped in enPreparacion" :key="ped.id" class="bg-white rounded-xl p-4 shadow-sm border border-blue-100 animate-fade-in relative overflow-hidden">
            <div class="absolute top-0 left-0 w-1 h-full bg-blue-500"></div>
            <div class="flex justify-between items-start mb-3 pl-2">
              <div>
                <h3 class="font-bold text-lg text-slate-800">Mesa {{ ped.mesa }}</h3>
                <p class="text-xs text-slate-500">Pedido #{{ ped.id }}</p>
              </div>
              <button @click="marcarListo(ped.id)" class="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg font-medium transition-colors shadow-sm text-sm">
                ✅ Terminado
              </button>
            </div>
            
            <ul class="pl-2 space-y-1">
              <li v-for="det in detallesCargados[ped.id]" :key="det.id" class="flex justify-between text-slate-700 text-sm py-1 border-b border-slate-50 last:border-0">
                <span><span class="font-bold text-blue-900 mr-1">{{ det.cantidad }}x</span> {{ det.productoNombre }}</span>
              </li>
              <li v-if="!detallesCargados[ped.id]" class="text-slate-400 text-sm italic">Cargando detalles...</li>
            </ul>
          </div>
          <div v-if="enPreparacion.length === 0 && !loading" class="text-center text-blue-600/50 font-medium py-12 flex flex-col items-center">
            <svg class="w-12 h-12 mb-2" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
            No hay pedidos en preparación
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
