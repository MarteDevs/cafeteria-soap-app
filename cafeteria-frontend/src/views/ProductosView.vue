<script setup>
import { ref, onMounted, computed } from 'vue'
import { listarProductos, crearProducto, actualizarProducto, eliminarProducto } from '../services/productoService.js'
import { listarCategorias } from '../services/categoriaService.js'

const items      = ref([])
const categorias = ref([])
const form       = ref({ id: null, nombre: '', precio: 0, stock: 0, categoriaId: '' })
const editando   = ref(false)
const showModal  = ref(false)
const loading    = ref(false)
const error      = ref('')

async function cargar() {
  loading.value = true
  error.value = ''
  try { 
    const [prodRes, catRes] = await Promise.all([
      listarProductos(),
      listarCategorias()
    ])
    items.value = prodRes
    categorias.value = catRes
  } catch (err) { 
    error.value = 'Error al cargar productos. Verifica la conexión con el servidor.' 
    console.error(err)
  } finally { 
    loading.value = false 
  }
}

function getCategoriaNombre(id) {
  const cat = categorias.value.find(c => String(c.id) === String(id))
  return cat ? cat.nombre : 'Sin categoría'
}

function abrirCrear() {
  form.value  = { id: null, nombre: '', precio: 0, stock: 0, categoriaId: categorias.value[0]?.id || '' }
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
      await actualizarProducto(form.value.id, form.value.nombre, form.value.precio, form.value.stock, form.value.categoriaId)
    } else {
      await crearProducto(form.value.nombre, form.value.precio, form.value.stock, form.value.categoriaId)
    }
    showModal.value = false
    await cargar()
  } catch (err) {
    alert('Error al guardar: ' + err.message)
  }
}

async function borrar(id) {
  if (!confirm('¿Estás seguro de eliminar este producto?')) return
  try {
    await eliminarProducto(id)
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
        <h1 class="text-2xl font-bold text-slate-800">Productos</h1>
        <p class="text-slate-500 text-sm mt-1">Catálogo de productos disponibles</p>
      </div>
      <button @click="abrirCrear" class="bg-amber-600 hover:bg-amber-700 text-white px-4 py-2 rounded-lg shadow transition-colors font-medium flex items-center gap-2">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path></svg>
        Nuevo Producto
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
            <th class="p-4 font-semibold">Producto</th>
            <th class="p-4 font-semibold">Categoría</th>
            <th class="p-4 font-semibold text-right">Precio</th>
            <th class="p-4 font-semibold text-right">Stock</th>
            <th class="p-4 font-semibold text-right">Acciones</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-slate-100">
          <tr v-for="item in items" :key="item.id" class="hover:bg-slate-50 transition-colors">
            <td class="p-4">
              <div class="font-medium text-slate-800">{{ item.nombre }}</div>
              <div class="text-xs text-slate-400">ID: {{ item.id }}</div>
            </td>
            <td class="p-4 text-slate-600">
              <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-slate-100 text-slate-800">
                {{ getCategoriaNombre(item.categoriaId) }}
              </span>
            </td>
            <td class="p-4 font-medium text-slate-800 text-right">${{ item.precio.toFixed(2) }}</td>
            <td class="p-4 text-right">
              <span :class="['font-medium', item.stock > 10 ? 'text-green-600' : item.stock > 0 ? 'text-yellow-600' : 'text-red-600']">
                {{ item.stock }}
              </span>
            </td>
            <td class="p-4 text-right space-x-3">
              <button @click="abrirEditar(item)" class="text-blue-600 hover:text-blue-800 font-medium transition-colors">Editar</button>
              <button @click="borrar(item.id)" class="text-red-600 hover:text-red-800 font-medium transition-colors">Eliminar</button>
            </td>
          </tr>
          <tr v-if="items.length === 0">
            <td colspan="5" class="p-8 text-center text-slate-500">No hay productos registrados</td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- Modal -->
    <div v-if="showModal" class="fixed inset-0 bg-black/50 backdrop-blur-sm flex items-center justify-center p-4 z-50 animate-fade-in">
      <div class="bg-white rounded-2xl shadow-xl w-full max-w-md overflow-hidden transform transition-all">
        <div class="p-6 border-b border-slate-100">
          <h3 class="text-xl font-bold text-slate-800">{{ editando ? 'Editar Producto' : 'Nuevo Producto' }}</h3>
        </div>
        <form @submit.prevent="guardar" class="p-6 space-y-4">
          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">Nombre</label>
            <input v-model="form.nombre" required type="text" class="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-amber-500 outline-none transition-shadow" placeholder="Ej. Café Moka">
          </div>
          
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">Precio ($)</label>
              <input v-model.number="form.precio" required type="number" step="0.01" min="0" class="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-amber-500 outline-none transition-shadow">
            </div>
            <div>
              <label class="block text-sm font-medium text-slate-700 mb-1">Stock Inicial</label>
              <input v-model.number="form.stock" required type="number" min="0" class="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-amber-500 outline-none transition-shadow">
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-slate-700 mb-1">Categoría</label>
            <select v-model="form.categoriaId" required class="w-full px-4 py-2 border border-slate-300 rounded-lg focus:ring-2 focus:ring-amber-500 focus:border-amber-500 outline-none transition-shadow bg-white">
              <option value="" disabled>Seleccione una categoría</option>
              <option v-for="cat in categorias" :key="cat.id" :value="cat.id">{{ cat.nombre }}</option>
            </select>
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
