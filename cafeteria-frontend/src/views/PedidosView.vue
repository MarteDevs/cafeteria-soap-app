<script setup>
import { ref, onMounted } from 'vue'
import { listarPedidos, cancelarPedido, cambiarEstado } from '../services/pedidoService.js'
import BadgeEstado from '../components/BadgeEstado.vue'

const items   = ref([])
const loading = ref(false)
const error   = ref('')

async function cargar() {
  loading.value = true
  error.value = ''
  try { 
    items.value = await listarPedidos() 
    // Ordenar por ID descendente (más recientes primero)
    items.value.sort((a, b) => parseInt(b.id) - parseInt(a.id))
  } catch (err) { 
    error.value = 'Error al cargar los pedidos.' 
    console.error(err)
  } finally { 
    loading.value = false 
  }
}

async function anular(id) {
  if (!confirm(`¿Estás seguro de cancelar el pedido #${id}?`)) return
  try {
    await cancelarPedido(id)
    await cargar()
  } catch (err) {
    alert('Error al cancelar: ' + err.message)
  }
}

async function marcarEntregado(id) {
  try {
    await cambiarEstado(id, 'ENTREGADO')
    await cargar()
  } catch (err) {
    alert('Error al actualizar estado: ' + err.message)
  }
}

function formatFecha(fechaStr) {
  if (!fechaStr) return ''
  try {
    return new Date(fechaStr).toLocaleString()
  } catch {
    return fechaStr
  }
}

onMounted(cargar)
</script>

<template>
  <div class="space-y-6">
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold text-slate-800">Historial de Pedidos</h1>
        <p class="text-slate-500 text-sm mt-1">Registro completo de las órdenes realizadas</p>
      </div>
      <button @click="cargar" class="bg-white border border-slate-200 text-slate-600 hover:bg-slate-50 px-4 py-2 rounded-lg shadow-sm transition-colors font-medium flex items-center gap-2">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path></svg>
        Actualizar
      </button>
    </div>

    <div v-if="error" class="bg-red-50 text-red-600 p-4 rounded-lg border border-red-200">
      {{ error }}
    </div>

    <div v-if="loading && items.length === 0" class="flex justify-center py-12">
      <div class="animate-spin rounded-full h-10 w-10 border-b-2 border-amber-600"></div>
    </div>

    <div v-else class="bg-white rounded-xl shadow-sm border border-slate-200 overflow-hidden">
      <table class="w-full text-left border-collapse">
        <thead>
          <tr class="bg-slate-50 border-b border-slate-200 text-slate-600 text-sm uppercase tracking-wider">
            <th class="p-4 font-semibold">ID / Fecha</th>
            <th class="p-4 font-semibold">Mesa</th>
            <th class="p-4 font-semibold">Estado</th>
            <th class="p-4 font-semibold text-right">Total</th>
            <th class="p-4 font-semibold text-right">Acciones</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-100">
          <tr v-for="item in items" :key="item.id" class="hover:bg-slate-50 transition-colors">
            <td class="p-4">
              <div class="font-medium text-slate-800">#{{ item.id }}</div>
              <div class="text-xs text-slate-500">{{ formatFecha(item.fecha) }}</div>
            </td>
            <td class="p-4 font-medium text-slate-700">Mesa {{ item.mesa }}</td>
            <td class="p-4">
              <BadgeEstado :estado="item.estado" />
            </td>
            <td class="p-4 font-bold text-slate-800 text-right">${{ item.total.toFixed(2) }}</td>
            <td class="p-4 text-right space-x-3">
              <button 
                v-if="item.estado === 'EN_PREPARACION'" 
                @click="marcarEntregado(item.id)" 
                class="text-green-600 hover:text-green-800 font-medium transition-colors"
              >
                Marcar Entregado
              </button>
              <button 
                v-if="item.estado !== 'CANCELADO' && item.estado !== 'ENTREGADO'" 
                @click="anular(item.id)" 
                class="text-red-600 hover:text-red-800 font-medium transition-colors"
              >
                Cancelar
              </button>
              <span v-if="item.estado === 'CANCELADO' || item.estado === 'ENTREGADO'" class="text-slate-400 text-sm italic">
                Sin acciones
              </span>
            </td>
          </tr>
          <tr v-if="items.length === 0">
            <td colspan="5" class="p-8 text-center text-slate-500">No hay pedidos registrados</td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>
