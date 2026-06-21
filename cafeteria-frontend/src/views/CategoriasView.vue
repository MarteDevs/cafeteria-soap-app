<script setup>
import { ref, onMounted } from 'vue'
import { listarCategorias, crearCategoria, actualizarCategoria, eliminarCategoria } from '../services/categoriaService.js'

const items     = ref([])
const form      = ref({ id: null, nombre: '', descripcion: '' })
const editando  = ref(false)
const showModal = ref(false)
const loading   = ref(false)
const error     = ref('')

async function cargar() {
  loading.value = true
  error.value = ''
  try { 
    items.value = await listarCategorias() 
  } catch (err) { 
    error.value = 'Error al cargar categorías. Verifica que el servidor SOAP esté corriendo.' 
    console.error(err)
  } finally { 
    loading.value = false 
  }
}

function abrirCrear() {
  form.value  = { id: null, nombre: '', descripcion: '' }
  editando.value = false
  showModal.value = true
}

function abrirEditar(item) {
  form.value  = { ...item }
  editando.value = true
  showModal.value = true
}

async function guardar() {
  try {
    if (editando.value) {
      await actualizarCategoria(form.value.id, form.value.nombre, form.value.descripcion)
    } else {
      await crearCategoria(form.value.nombre, form.value.descripcion)
    }
    showModal.value = false
    await cargar()
  } catch (err) {
    alert('Error al guardar: ' + err.message)
  }
}

async function borrar(id) {
  if (!confirm('¿Estás seguro de eliminar esta categoría?')) return
  try {
    await eliminarCategoria(id)
    await cargar()
  } catch (err) {
    alert('Error al eliminar: ' + err.message)
  }
}

onMounted(cargar)
</script>

<template>
  <div class="space-y-6 font-inter">
    <div class="flex justify-between items-end">
      <div>
        <h1 class="text-4xl font-outfit font-bold text-white mb-1">Categorías</h1>
        <p class="text-[#a09fb9]">Gestiona las categorías de tus productos</p>
      </div>
      <button @click="abrirCrear" class="btn-primary flex items-center gap-2">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path></svg>
        Nueva Categoría
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
              <th class="p-5 font-semibold w-24">ID</th>
              <th class="p-5 font-semibold w-1/3">Nombre</th>
              <th class="p-5 font-semibold">Descripción</th>
              <th class="p-5 font-semibold text-right">Acciones</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-white/5">
            <tr v-for="item in items" :key="item.id" class="hover:bg-white/5 transition-colors group relative">
              <td class="p-5 text-[#c6c4df]">#{{ item.id }}</td>
              <td class="p-5 font-outfit font-bold text-[#e4e1e6] text-lg">{{ item.nombre }}</td>
              <td class="p-5 text-[#a09fb9]">{{ item.descripcion }}</td>
              <td class="p-5 text-right space-x-2">
                <button @click="abrirEditar(item)" class="p-2 rounded-lg bg-white/5 text-[#a09fb9] hover:text-white hover:bg-white/10 border border-white/5 transition-all">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"></path></svg>
                </button>
                <button @click="borrar(item.id)" class="p-2 rounded-lg bg-white/5 text-[#ffb4ab] hover:text-white hover:bg-[#93000a]/80 border border-white/5 transition-all">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"></path></svg>
                </button>
              </td>
            </tr>
            <tr v-if="items.length === 0">
              <td colspan="4" class="p-12 text-center text-[#a09fb9]">
                <div class="flex flex-col items-center gap-3">
                  <svg class="w-12 h-12 text-white/20" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M19 11H5m14 0a2 2 0 012 2v6a2 2 0 01-2 2H5a2 2 0 01-2-2v-6a2 2 0 012-2m14 0V9a2 2 0 00-2-2M5 11V9a2 2 0 012-2m0 0V5a2 2 0 012-2h6a2 2 0 012 2v2M7 7h10"></path></svg>
                  <p>No hay categorías registradas</p>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- Modal -->
    <div v-if="showModal" class="fixed inset-0 bg-[#0f0f12]/80 backdrop-blur-sm flex items-center justify-center p-4 z-50 animate-fade-in">
      <div class="glass-panel w-full max-w-md overflow-hidden transform transition-all border border-white/20 shadow-[0_0_30px_rgba(0,0,0,0.5)]">
        <div class="p-6 border-b border-white/10 bg-[#1a1a2e]/50">
          <h3 class="text-2xl font-outfit font-bold text-white">{{ editando ? 'Editar Categoría' : 'Nueva Categoría' }}</h3>
        </div>
        <form @submit.prevent="guardar" class="p-6 space-y-5">
          <div>
            <label class="block text-sm font-medium text-[#c6c4df] mb-2">Nombre</label>
            <input v-model="form.nombre" required type="text" class="w-full bg-[#131316] border border-white/10 rounded-xl px-4 py-3 text-white focus:outline-none focus:border-[#ff7a45] transition-colors" placeholder="Ej. Bebidas Calientes">
          </div>
          <div>
            <label class="block text-sm font-medium text-[#c6c4df] mb-2">Descripción</label>
            <textarea v-model="form.descripcion" required rows="3" class="w-full bg-[#131316] border border-white/10 rounded-xl px-4 py-3 text-white focus:outline-none focus:border-[#ff7a45] transition-colors" placeholder="Descripción de la categoría..."></textarea>
          </div>
          <div class="pt-6 flex gap-3 justify-end border-t border-white/5">
            <button type="button" @click="showModal = false" class="btn-secondary">Cancelar</button>
            <button type="submit" class="btn-primary py-2 text-sm shadow-[0_0_10px_rgba(255,122,69,0.3)] hover:shadow-[0_0_20px_rgba(255,122,69,0.6)]">Guardar</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
