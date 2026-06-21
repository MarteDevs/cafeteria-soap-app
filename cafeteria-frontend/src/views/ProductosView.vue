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
  <div class="space-y-6 font-inter">
    <div class="flex justify-between items-end">
      <div>
        <h1 class="text-4xl font-outfit font-bold text-white mb-1">Productos</h1>
        <p class="text-[#a09fb9]">Catálogo de productos disponibles</p>
      </div>
      <button @click="abrirCrear" class="btn-primary flex items-center gap-2">
        <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4"></path></svg>
        Nuevo Producto
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
              <th class="p-5 font-semibold">Producto</th>
              <th class="p-5 font-semibold">Categoría</th>
              <th class="p-5 font-semibold text-right">Precio</th>
              <th class="p-5 font-semibold text-center">Stock</th>
              <th class="p-5 font-semibold text-right">Acciones</th>
            </tr>
          </thead>
          <tbody class="divide-y divide-white/5">
            <tr v-for="item in items" :key="item.id" class="hover:bg-white/5 transition-colors group relative">
              <td class="p-5">
                <div class="font-medium text-[#e4e1e6]">{{ item.nombre }}</div>
                <div class="text-xs text-[#a09fb9]">ID: {{ item.id }}</div>
              </td>
              <td class="p-5">
                <span class="inline-flex items-center px-3 py-1 rounded-full text-xs font-medium bg-white/5 text-[#c6c4df] border border-white/10">
                  {{ getCategoriaNombre(item.categoriaId) }}
                </span>
              </td>
              <td class="p-5 font-outfit font-bold text-[#ff7a45] text-right">S/. {{ item.precio.toFixed(2) }}</td>
              <td class="p-5 text-center">
                <div :class="['inline-flex items-center justify-center w-8 h-8 rounded-full font-medium text-sm border', item.stock > 10 ? 'bg-[#154b2d]/30 text-[#82e8a6] border-[#154b2d]' : item.stock > 0 ? 'bg-[#6b46c1]/30 text-[#e9ddff] border-[#6b46c1]/50' : 'bg-[#93000a]/30 text-[#ffb4ab] border-[#93000a]/50']">
                  {{ item.stock }}
                </div>
              </td>
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
              <td colspan="5" class="p-12 text-center text-[#a09fb9]">
                <div class="flex flex-col items-center gap-3">
                  <svg class="w-12 h-12 text-white/20" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="1" d="M20 13V6a2 2 0 00-2-2H6a2 2 0 00-2 2v7m16 0v5a2 2 0 01-2 2H6a2 2 0 01-2-2v-5m16 0h-2.586a1 1 0 00-.707.293l-2.414 2.414a1 1 0 01-.707.293h-3.172a1 1 0 01-.707-.293l-2.414-2.414A1 1 0 006.586 13H4"></path></svg>
                  <p>No hay productos registrados</p>
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
          <h3 class="text-2xl font-outfit font-bold text-white">{{ editando ? 'Editar Producto' : 'Nuevo Producto' }}</h3>
        </div>
        <form @submit.prevent="guardar" class="p-6 space-y-5">
          <div>
            <label class="block text-sm font-medium text-[#c6c4df] mb-2">Nombre</label>
            <input v-model="form.nombre" required type="text" class="w-full bg-[#131316] border border-white/10 rounded-xl px-4 py-3 text-white focus:outline-none focus:border-[#ff7a45] transition-colors" placeholder="Ej. Café Moka">
          </div>
          
          <div class="grid grid-cols-2 gap-4">
            <div>
              <label class="block text-sm font-medium text-[#c6c4df] mb-2">Precio (S/.)</label>
              <input v-model.number="form.precio" required type="number" step="0.01" min="0" class="w-full bg-[#131316] border border-white/10 rounded-xl px-4 py-3 text-white focus:outline-none focus:border-[#ff7a45] transition-colors">
            </div>
            <div>
              <label class="block text-sm font-medium text-[#c6c4df] mb-2">Stock Inicial</label>
              <input v-model.number="form.stock" required type="number" min="0" class="w-full bg-[#131316] border border-white/10 rounded-xl px-4 py-3 text-white focus:outline-none focus:border-[#ff7a45] transition-colors">
            </div>
          </div>

          <div>
            <label class="block text-sm font-medium text-[#c6c4df] mb-2">Categoría</label>
            <select v-model="form.categoriaId" required class="w-full bg-[#131316] border border-white/10 rounded-xl px-4 py-3 text-white focus:outline-none focus:border-[#ff7a45] transition-colors appearance-none">
              <option value="" disabled>Seleccione una categoría</option>
              <option v-for="cat in categorias" :key="cat.id" :value="cat.id">{{ cat.nombre }}</option>
            </select>
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
