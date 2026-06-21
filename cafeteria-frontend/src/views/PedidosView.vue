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
  <div class="space-y-6 font-inter">
    <div class="flex justify-between items-end">
      <div>
        <h1 class="text-4xl font-outfit font-bold text-white mb-1">Historial de Pedidos</h1>
        <p class="text-[#a09fb9]">Registro completo de las órdenes realizadas</p>
      </div>
      <button @click="cargar" class="bg-white/5 border border-white/10 text-white hover:bg-white/10 hover:border-white/20 px-4 py-2 rounded-lg shadow-sm transition-all font-medium flex items-center gap-2">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path></svg>
        Actualizar
      </button>
    </div>

    <div v-if="error" class="bg-[#93000a]/20 text-[#ffb4ab] border border-[#93000a] p-4 rounded-xl backdrop-blur-md">
      {{ error }}
    </div>

    <div v-if="loading && items.length === 0" class="flex justify-center py-20">
      <div class="animate-spin rounded-full h-12 w-12 border-b-2 border-[#ff7a45]"></div>
    </div>

    <div v-else class="glass-panel overflow-hidden">
      <div class="overflow-x-auto">
        <table class="w-full text-left border-collapse">
          <thead>
            <tr class="bg-[#1a1a2e]/80 border-b border-white/10 text-[#a09fb9] text-sm uppercase tracking-wider font-outfit">
              <th class="p-5 font-semibold">ID / Fecha</th>
              <th class="p-5 font-semibold">Mesa</th>
              <th class="p-5 font-semibold">Estado</th>
              <th class="p-5 font-semibold text-right">Total</th>
              <th class="p-5 font-semibold text-right">Acciones</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-white/5">
            <tr v-for="item in items" :key="item.id" class="hover:bg-white/5 transition-colors group relative">
              <td class="p-5">
                <div class="font-outfit font-bold text-white text-lg">#{{ item.id }}</div>
                <div class="text-xs text-[#a09fb9]">{{ formatFecha(item.fecha) }}</div>
              </td>
              <td class="p-5 font-medium text-[#c6c4df]">Mesa {{ item.mesa }}</td>
              <td class="p-5">
                <BadgeEstado :estado="item.estado" />
              </td>
              <td class="p-5 font-outfit font-bold text-[#ff7a45] text-right">${{ item.total.toFixed(2) }}</td>
              <td class="p-5 text-right space-x-3">
                <button 
                  v-if="item.estado === 'EN_PREPARACION'" 
                  @click="marcarEntregado(item.id)" 
                  class="text-[#82e8a6] hover:text-white font-medium transition-colors"
                >
                  Marcar Entregado
                </button>
                <button 
                  v-if="item.estado !== 'CANCELADO' && item.estado !== 'ENTREGADO'" 
                  @click="anular(item.id)" 
                  class="text-[#ffb4ab] hover:text-white font-medium transition-colors"
                >
                  Cancelar
                </button>
                <span v-if="item.estado === 'CANCELADO' || item.estado === 'ENTREGADO'" class="text-[#a09fb9] text-sm italic">
                  Sin acciones
                </span>
              </td>
            </tr>
            <tr v-if="items.length === 0">
              <td colspan="5" class="p-12 text-center text-[#a09fb9]">
                <div class="flex flex-col items-center gap-3">
                  <svg class="w-12 h-12 text-white/20" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"></path></svg>
                  <p>No hay pedidos registrados</p>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </div>
</template>
