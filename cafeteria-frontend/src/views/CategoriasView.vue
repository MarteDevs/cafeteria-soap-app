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
  <div class="space-y-6">
    <div class="flex justify-between items-center">
      <div>
        <h1 class="text-2xl font-bold text-slate-800">Categorías</h1>
        <p class="text-slate-500 text-sm mt-1">Gestiona las categorías de tus productos</p>
      </div>
      <button @click="abrirCrear" class="bg-amber-600 hover:bg-amber-700 text-white px-4 py-2 rounded-lg shadow transition-colors font-medium flex items-center gap-2">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path></svg>
        Nueva Categoría
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
            <th class="p-4 font-semibold">ID</th>
            <th class="p-4 font-semibold">Nombre</th>
            <th class="p-4 font-semibold">Descripción</th>
            <th class="p-4 font-semibold text-right">Acciones</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-100">
          <tr v-for="item in items" :key="item.id" class="hover:bg-slate-50 transition-colors">
            <td class="p-4 text-slate-500">#{{ item.id }}</td>
            <td class="p-4 font-medium text-slate-800">{{ item.nombre }}</td>
            <td class="p-4 text-slate-600">{{ item.descripcion }}</td>
            <td class="p-4 text-right space-x-3">
              <button @click="abrirEditar(item)" class="text-blue-600 hover:text-blue-800 font-medium transition-colors">Editar</button>
              <button @click="borrar(item.id)" class="text-red-600 hover:text-red-800 font-medium transition-colors">Eliminar</button>
            </td>
          </tr>
          <tr v-if="items.length === 0">
            <td colspan="4" class="p-8 text-center text-slate-500">No hay categorías registradas</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal -->
    <div v-if="showModal" class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50 animate-fade-in">
      <div class="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden transform transition-all">
        <div class="p-6 border-b border-slate-100">
          <h3 class="text-xl font-bold text-slate-800">{{ editando ? 'Editar Categoría' : 'Nueva Categoría' }}</h3>
        </div>
        <form @submit.prevent="guardar" class="p-6 space-y-4">
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">Nombre</label>
            <input v-model="form.nombre" required type="text" class="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-amber-500 outline-none transition-shadow" placeholder="Ej. Bebidas Calientes">
          </div>
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">Descripción</label>
            <textarea v-model="form.descripcion" required rows="3" class="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-amber-500 outline-none transition-shadow" placeholder="Descripción de la categoría..."></textarea>
          </div>
          <div class="pt-4 flex gap-3 justify-end">
            <button type="button" @click="showModal = false" class="px-5 py-2 text-slate-600 hover:bg-slate-100 rounded-lg font-medium transition-colors">Cancelar</button>
            <button type="submit" class="px-5 py-2 bg-amber-600 hover:bg-amber-700 text-white rounded-lg font-medium shadow transition-colors">Guardar</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
